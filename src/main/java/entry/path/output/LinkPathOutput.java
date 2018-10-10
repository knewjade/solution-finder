package entry.path.output;

import common.datastore.Pair;
import common.datastore.blocks.LongPieces;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.field.Field;
import entry.path.*;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import output.HTMLBuilder;
import output.HTMLColumn;
import searcher.pack.SizedBit;

import java.io.BufferedWriter;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LinkPathOutput implements PathOutput {
    private static final String FILE_EXTENSION = ".html";

    private final PathEntryPoint pathEntryPoint;
    private final PathSettings settings;

    private final MyFile outputMinimalFile;
    private final MyFile outputUniqueFile;
    private final long numOfAllPieces;

    public LinkPathOutput(PathEntryPoint pathEntryPoint, PathSettings pathSettings, int numOfAllPieces) throws FinderInitializeException {
        // 出力ファイルが正しく出力できるか確認
        String outputBaseFilePath = pathSettings.getOutputBaseFilePath();
        String namePath = getRemoveExtensionFromPath(outputBaseFilePath);

        // pathが空 または ディレクトリであるとき、pathを追加して、ファイルにする
        if (namePath.isEmpty() || namePath.endsWith(String.valueOf(File.separatorChar)))
            namePath += "path";

        // baseファイル
        String outputFilePath = String.format("%s%s", namePath, FILE_EXTENSION);
        MyFile base = new MyFile(outputFilePath);
        base.mkdirs();

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
        this.outputUniqueFile = unique;
        this.outputMinimalFile = minimal;
        this.numOfAllPieces = numOfAllPieces;
    }

    private String getRemoveExtensionFromPath(String path) {
        int pointIndex = path.lastIndexOf('.');
        int separatorIndex = path.lastIndexOf(File.separatorChar);

        // .がない or セパレータより前にあるとき
        if (pointIndex <= separatorIndex)
            return path;

        // .があるとき
        return path.substring(0, pointIndex);
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
            Selector<PathPair, LongPieces> selector = new Selector<>(pathPairs);
            List<PathPair> minimal = selector.select();
            outputLog("Found path [minimal] = " + minimal.size());
            outputOperationsToSimpleHTML(field, outputMinimalFile, minimal, sizedBit);
        }
    }

    private void outputLog(String str) throws FinderExecuteException {
        pathEntryPoint.output(str);
    }

    private void outputOperationsToSimpleHTML(Field field, MyFile file, List<PathPair> pathPairs, SizedBit sizedBit) throws FinderExecuteException {
        // Get height
        int maxClearLine = sizedBit.getHeight();

        // テト譜用のフィールド作成
        ColoredField initField = ColoredFieldFactory.createField(24);
        for (int y = 0; y < maxClearLine; y++)
            for (int x = 0; x < 10; x++)
                if (!field.isEmpty(x, y))
                    initField.setColorType(ColorType.Gray, x, y);

        // 並び替える
        Comparator<PathPair> comparator = new PathPairComparator();
        pathPairs.sort(comparator);

        // HTMLの生成  // true: ライン消去あり, false: ライン消去なし
        HTMLBuilder<HTMLColumn> htmlBuilder = new HTMLBuilder<>("Path Result");
        htmlBuilder.addHeader(String.format("<div>%dパターン <span style='color: #999'>[%dシーケンス]</span></div>", pathPairs.size(), numOfAllPieces));

        pathPairs.parallelStream()
                .forEach(pathPair -> {
                    PathHTMLColumn htmlColumn = getHTMLColumn(pathPair);
                    Pair<String, Long> linkAndPriority = createALink(pathPair);
                    String line = String.format("<div>%s</div>", linkAndPriority.getKey());
                    htmlBuilder.addColumn(htmlColumn, line, -linkAndPriority.getValue());
                });

        // 出力
        try (BufferedWriter writer = file.newBufferedWriter()) {
            List<HTMLColumn> priorityList = Arrays.asList(PathHTMLColumn.NotDeletedLine, PathHTMLColumn.DeletedLine);
            for (String line : htmlBuilder.toList(priorityList, false))
                writer.write(line);
            writer.flush();
        } catch (Exception e) {
            throw new FinderExecuteException("Failed to output file", e);
        }
    }

    private PathHTMLColumn getHTMLColumn(PathPair pathPair) {
        if (pathPair.isDeletedLine())
            return PathHTMLColumn.DeletedLine;
        else
            return PathHTMLColumn.NotDeletedLine;
    }

    private Pair<String, Long> createALink(PathPair pathPair) {
        // パターンを表す名前 を生成
        String linkText = pathPair.getSampleOperations().stream()
                .map(operationWithKey -> operationWithKey.getPiece().getName() + "-" + operationWithKey.getRotate().name())
                .collect(Collectors.joining(" "));

        // テト譜に変換
        String encode = pathPair.getFumen();

        // 有効なミノ順をまとめる
        long counter = pathPair.blocksStreamForPattern().count();
        double validPercent = (double) counter / numOfAllPieces * 100.0;

        // 出力
        return new Pair<>(String.format("<a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a> / %.1f %% <span style='color: #999'>[%d]</span>", encode, linkText, validPercent, counter), counter);
    }
}
