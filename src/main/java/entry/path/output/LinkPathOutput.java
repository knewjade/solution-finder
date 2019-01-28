package entry.path.output;

import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.datastore.Pair;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.Piece;
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
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;

    public LinkPathOutput(PathEntryPoint pathEntryPoint, PathSettings pathSettings, MinoFactory minoFactory, ColorConverter colorConverter) throws FinderInitializeException {
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
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
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
    public void output(PathPairs pathPairs, Field field, SizedBit sizedBit) throws FinderExecuteException {
        int numOfAllPatternSequences = pathPairs.getNumOfAllPatternSequences();

        PathLayer pathLayer = settings.getPathLayer();

        // 同一ミノ配置を取り除いたパスの出力
        if (pathLayer.contains(PathLayer.Unique)) {
            List<PathPair> pathPairList = pathPairs.getUniquePathPairList();

            outputLog("Found path [unique] = " + pathPairList.size());
            outputOperationsToSimpleHTML(field, outputUniqueFile, pathPairList, sizedBit, numOfAllPatternSequences);
        }

        // 少ないパターンでカバーできるパスを出力
        if (pathLayer.contains(PathLayer.Minimal)) {
            List<PathPair> pathPairList = pathPairs.getMinimalPathPairList();

            outputLog("Found path [minimal] = " + pathPairList.size());
            outputOperationsToSimpleHTML(field, outputMinimalFile, pathPairList, sizedBit, numOfAllPatternSequences);
        }
    }

    private void outputLog(String str) throws FinderExecuteException {
        pathEntryPoint.output(str);
    }

    private void outputOperationsToSimpleHTML(Field field, MyFile file, List<PathPair> pathPairs, SizedBit sizedBit, int numOfAllPatternSequences) throws FinderExecuteException {
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
        htmlBuilder.addHeader(String.format("<div>%d solutions <span style='color: #999'>[%d input sequences]</span></div>", pathPairs.size(), numOfAllPatternSequences));

        OneFumenParser oneFumenParser = new OneFumenParser(minoFactory, colorConverter);
        BufferedFumenParser bufferedFumenParser = new BufferedFumenParser(minoFactory, colorConverter, oneFumenParser);

        pathPairs.parallelStream()
                .forEach(pathPair -> {
                    PathHTMLColumn htmlColumn = getHTMLColumn(pathPair);

                    List<MinoOperationWithKey> operations = pathPair.getSampleOperations();

                    // パターンを表す名前 を生成
                    String blocksName = operations.stream()
                            .map(OperationWithKey::getPiece)
                            .map(Piece::getName)
                            .collect(Collectors.joining());

                    // 入力パターンのうち有効なミノ順を確率に変換
                    long counter = pathPair.getNumOfValidSpecifiedPatterns();
                    double validPercent = (double) counter / numOfAllPatternSequences * 100.0;
                    String comment = String.format("%.1f %% : %s", validPercent, blocksName);

                    bufferedFumenParser.add(operations, field, maxClearLine, comment, counter);

                    Pair<String, Long> linkAndPriority = createALink(pathPair, counter, validPercent);
                    String line = String.format("<div>%s</div>", linkAndPriority.getKey());
                    htmlBuilder.addColumn(htmlColumn, line, -linkAndPriority.getValue());
                });

        if (!pathPairs.isEmpty()) {
            String mergedFumen = bufferedFumenParser.parse();
            htmlBuilder.addHeader(String.format("<div><a href='http://fumen.zui.jp/?v115@%s'>All solutions<a></div>", mergedFumen));
        }

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

    private Pair<String, Long> createALink(PathPair pathPair, long counter, double validPercent) {
        // パターンを表す名前 を生成
        String linkText = pathPair.getSampleOperations().stream()
                .map(operationWithKey -> operationWithKey.getPiece().getName() + "-" + operationWithKey.getRotate().name())
                .collect(Collectors.joining(" "));

        // テト譜に変換
        String encode = pathPair.getFumen();

        // 出力
        return new Pair<>(String.format("<a href='http://fumen.zui.jp/?v115@%s'>%s</a> / %.1f %% <span style='color: #999'>[%d]</span>", encode, linkText, validPercent, counter), counter);
    }
}
