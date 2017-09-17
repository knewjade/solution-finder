package entry.path.output;

import common.buildup.BuildUpStream;
import common.datastore.OperationWithKey;
import common.datastore.pieces.LongBlocks;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.action.reachable.Reachable;
import core.field.Field;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import entry.path.*;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import searcher.pack.SizedBit;
import searcher.pack.task.Result;

import java.io.BufferedWriter;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class LinkPathOutput implements PathOutput {
    private static final String FILE_EXTENSION = ".html";

    private final PathEntryPoint pathEntryPoint;
    private final PathSettings settings;
    private final Reachable reachable;

    private final MyFile outputMinimalFile;
    private final MyFile outputUniqueFile;
    private final TetfuParser tetfuParser;

    public LinkPathOutput(PathEntryPoint pathEntryPoint, PathSettings pathSettings, MinoFactory minoFactory, Reachable reachable) throws FinderInitializeException {
        // 出力ファイルが正しく出力できるか確認
        String outputBaseFilePath = pathSettings.getOutputBaseFilePath();
        String namePath = getRemoveExtensionFromPath(outputBaseFilePath);

        // pathが空 または ディレクトリであるとき、pathを追加して、ファイルにする
        if (namePath.isEmpty() || namePath.endsWith(String.valueOf(File.separatorChar)))
            namePath += "path";

        // baseファイル
        String outputFilePath = String.format("%s%s", namePath, FILE_EXTENSION);
        MyFile.mkdirs(outputFilePath);

        // uniqueファイル
        String uniqueOutputFilePath = String.format("%s_unique%s", namePath, FILE_EXTENSION);
        MyFile unique = new MyFile(uniqueOutputFilePath);
        unique.verify();

        // minimalファイル
        String minimalOutputFilePath = String.format("%s_minimal%s", namePath, FILE_EXTENSION);
        MyFile minimal = new MyFile(minimalOutputFilePath);
        minimal.verify();

        // 保存
        this.pathEntryPoint = pathEntryPoint;
        this.settings = pathSettings;
        this.reachable = reachable;
        ColorConverter colorConverter = new ColorConverter();
        this.tetfuParser = createTetfuParser(settings.isTetfuSplit(), minoFactory, colorConverter);
        this.outputUniqueFile = unique;
        this.outputMinimalFile = minimal;
    }

    private String getRemoveExtensionFromPath(String path) throws FinderInitializeException {
        int pointIndex = path.lastIndexOf('.');
        int separatorIndex = path.lastIndexOf(File.separatorChar);

        // .がない or セパレータより前にあるとき
        if (pointIndex <= separatorIndex)
            return path;

        // .があるとき
        return path.substring(0, pointIndex);
    }

    private TetfuParser createTetfuParser(boolean isTetfuSplit, MinoFactory minoFactory, ColorConverter colorConverter) {
        if (isTetfuSplit)
            return new SequenceTetfuParser(minoFactory, colorConverter);
        return new OneTetfuParser(minoFactory, colorConverter);
    }

    @Override
    public void output(List<PathPair> pathPairs, Field field, SizedBit sizedBit) throws FinderExecuteException {
        PathLayer pathLayer = settings.getPathLayer();

        // 同一ミノ配置を取り除いたパスの出力
        if (pathLayer.contains(PathLayer.Unique)) {
            outputLog("Found path [unique] = " + pathPairs.size());
            outputOperationsToSimpleHTML(field, outputUniqueFile, pathPairs, sizedBit);
        }

        // 少ないパターンでカバーできるパスを出力
        if (pathLayer.contains(PathLayer.Minimal)) {
            Selector<PathPair, LongBlocks> selector = new Selector<>(pathPairs);
            List<PathPair> minimal = selector.select();
            outputLog("Found path [minimal] = " + minimal.size());
            outputOperationsToSimpleHTML(field, outputMinimalFile, minimal, sizedBit);
        }
    }

    private void outputLog(String str) throws FinderExecuteException {
        pathEntryPoint.output(str);
    }

    private void outputOperationsToSimpleHTML(Field field, MyFile file, List<PathPair> resultPairs, SizedBit sizedBit) throws FinderExecuteException {
        // Get height
        int maxClearLine = sizedBit.getHeight();

        // テト譜用のフィールド作成
        ColoredField initField = ColoredFieldFactory.createField(24);
        for (int y = 0; y < maxClearLine; y++)
            for (int x = 0; x < 10; x++)
                if (!field.isEmpty(x, y))
                    initField.setColorType(ColorType.Gray, x, y);

        // ライン消去ありとなしに振り分ける // true: ライン消去あり, false: ライン消去なし
        LockedBuildUpListUpThreadLocal threadLocal = new LockedBuildUpListUpThreadLocal(sizedBit.getHeight());
        Map<Boolean, List<LinkInformation>> groupByDelete = resultPairs.stream()
                .map(resultPair -> {
                    Result result = resultPair.getResult();
                    LinkedList<OperationWithKey> operations = result.getMemento().getOperationsStream(sizedBit.getWidth()).collect(Collectors.toCollection(LinkedList::new));
                    BuildUpStream buildUpStream = threadLocal.get();
                    List<OperationWithKey> operationWithKeys = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .findFirst()
                            .orElse(Collections.emptyList());
                    return new LinkInformation(resultPair, operationWithKeys);
                })
                .collect(Collectors.groupingBy(LinkInformation::containsDeletedLine));

        // それぞれで並び替える
        Comparator<LinkInformation> comparator = new LinkInformationComparator();
        for (List<LinkInformation> objs : groupByDelete.values())
            objs.sort(comparator);

        // 出力
        try (BufferedWriter writer = file.newBufferedWriter()) {
            // headerの出力
            writer.write("<!DOCTYPE html>");
            writer.newLine();
            writer.write("<html lang=ja><head><meta charset=\"UTF-8\"></head><body>");
            writer.newLine();

            // パターン数の出力
            writer.write(String.format("<div>%dパターン</div>", resultPairs.size()));
            writer.newLine();

            // 手順の出力 (ライン消去なし)
            writer.write("<h2>ライン消去なし</h2>");
            writer.newLine();

            for (LinkInformation information : groupByDelete.getOrDefault(false, Collections.emptyList())) {
                String link = createALink(information, field, maxClearLine);
                writer.write(String.format("<div>%s</div>", link));
                writer.newLine();
            }

            // 手順の出力 (ライン消去あり)
            writer.write("<h2>ライン消去あり</h2>");
            writer.newLine();

            for (LinkInformation information : groupByDelete.getOrDefault(true, Collections.emptyList())) {
                String link = createALink(information, field, maxClearLine);
                writer.write(String.format("<div>%s</div>", link));
                writer.newLine();
            }

            // footerの出力
            writer.write("<html lang=ja><head><meta charset=\"UTF-8\"></head><body>");
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            throw new FinderExecuteException("Failed to output file", e);
        }
    }

    private String createALink(LinkInformation information, Field field, int maxClearLine) {
        LinkedList<OperationWithKey> operations = new LinkedList<>(information.getSample());

        // パターンを表す名前 を生成
        String linkText = operations.stream()
                .map(OperationWithKey::getMino)
                .map(mino -> mino.getBlock().getName() + "-" + mino.getRotate().name())
                .collect(Collectors.joining(" "));

        // テト譜に変換
        String encode = tetfuParser.parse(operations, field, maxClearLine);

        // 有効なミノ順をまとめる
        BuildUpStream buildUpStream = new BuildUpStream(reachable, maxClearLine);
        String validOrders = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                .map(operationWithKeys -> operationWithKeys.stream().map(OperationWithKey::getMino).map(Mino::getBlock).map(Block::getName).collect(Collectors.joining()))
                .collect(Collectors.joining(", "));

        // 出力
        return String.format("<a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a> [%s]", encode, linkText, validOrders);
    }
}
