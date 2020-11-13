package entry.setup.output;

import common.buildup.BuildUpStream;
import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.datastore.Pair;
import core.field.Field;
import core.mino.Piece;
import entry.path.output.FumenParser;
import entry.path.output.MyFile;
import entry.setup.SetupResults;
import entry.setup.SetupSettings;
import entry.setup.filters.SetupResult;
import entry.setup.functions.SetupFunctions;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import lib.AsyncBufferedFileWriter;
import searcher.pack.SizedBit;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class CSVSetupOutput implements SetupOutput {
    private static final String FILE_EXTENSION = ".csv";

    private final MyFile outputSetupFile;

    private final SetupFunctions setupFunctions;
    private final FumenParser fumenParser;
    private final ThreadLocal<BuildUpStream> buildUpStreamThreadLocal;

    public CSVSetupOutput(SetupSettings setupSettings, SetupFunctions setupFunctions, FumenParser fumenParser, ThreadLocal<BuildUpStream> buildUpStreamThreadLocal) throws FinderInitializeException {
        // 出力ファイルが正しく出力できるか確認
        String outputBaseFilePath = setupSettings.getOutputBaseFilePath();
        String namePath = getRemoveExtensionFromPath(outputBaseFilePath);

        // pathが空 または ディレクトリであるとき、pathを追加して、ファイルにする
        if (namePath.isEmpty() || namePath.endsWith(String.valueOf(File.separatorChar)))
            namePath += "setup";

        // baseファイル
        String outputFilePath = String.format("%s%s", namePath, FILE_EXTENSION);
        MyFile setup = new MyFile(outputFilePath);
        setup.mkdirs();
        setup.verify();

        // 保存
        this.outputSetupFile = setup;
        this.setupFunctions = setupFunctions;
        this.fumenParser = fumenParser;
        this.buildUpStreamThreadLocal = buildUpStreamThreadLocal;
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
    public void output(SetupResults setupResults, Field initField, SizedBit sizedBit) throws FinderExecuteException {
        Map<BlockField, List<SetupResult>> resultMap = setupResults.getResultMap();
        int maxHeight = sizedBit.getHeight();

        BiFunction<List<MinoOperationWithKey>, Field, String> naming = setupFunctions.getNaming();

        Comparator<Pair<Long, ?>> comparator = Comparator.comparingLong(Pair::getKey);

        try (AsyncBufferedFileWriter writer = outputSetupFile.newAsyncWriter()) {
            writer.writeAndNewLine("テト譜,使用ミノ,手順数");

            for (List<SetupResult> results : resultMap.values()) {
                results.stream()
                        .map(setupResult -> {
                            List<MinoOperationWithKey> operationWithKeys = setupResult.getSolution();

                            BuildUpStream buildUpStream = buildUpStreamThreadLocal.get();
                            long counter = buildUpStream.existsValidBuildPattern(initField, operationWithKeys).count();
                            return new Pair<>(counter, setupResult);
                        })
                        .sorted(comparator.reversed())
                        .forEach(pair -> {
                            Long counter = pair.getKey();
                            SetupResult setupResult = pair.getValue();

                            // 操作に変換
                            List<MinoOperationWithKey> operationWithKeys = setupResult.getSolution();

                            // 譜面の作成
                            String encode = fumenParser.parse(operationWithKeys, initField, maxHeight);

                            // 名前の作成
                            String name = naming.apply(operationWithKeys, setupResult.getRawField());

                            String line = String.format("http://fumen.zui.jp/?v115@%s,%s,%d", encode, name, counter);

                            writer.writeAndNewLine(line);
                        });
            }

            writer.flush();
        } catch (IOException e) {
            throw new FinderExecuteException("Failed to output file", e);
        }
    }
}
