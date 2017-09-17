package entry.path.output;

import common.buildup.BuildUpStream;
import common.datastore.OperationWithKey;
import common.datastore.Operations;
import common.parser.OperationInterpreter;
import common.parser.OperationTransform;
import common.tetfu.common.ColorConverter;
import core.action.reachable.Reachable;
import core.field.Field;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import entry.path.LockedBuildUpListUpThreadLocal;
import entry.path.PathEntryPoint;
import entry.path.PathPair;
import entry.path.PathSettings;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import searcher.pack.SizedBit;
import searcher.pack.task.Result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TetfuCSVPathOutput implements PathOutput {
    private static final String FILE_EXTENSION = ".csv";

    private final PathEntryPoint pathEntryPoint;
    private final Reachable reachable;

    private final MyFile outputBaseFile;
    private final TetfuParser tetfuParser;
    private Exception lastException = null;

    public TetfuCSVPathOutput(PathEntryPoint pathEntryPoint, PathSettings pathSettings, MinoFactory minoFactory, Reachable reachable) throws FinderInitializeException {
        // 出力ファイルが正しく出力できるか確認
        String outputBaseFilePath = pathSettings.getOutputBaseFilePath();
        String namePath = getRemoveExtensionFromPath(outputBaseFilePath);

        // pathが空 または ディレクトリであるとき、pathを追加して、ファイルにする
        if (namePath.isEmpty() || namePath.endsWith(String.valueOf(File.separatorChar)))
            namePath += "path";

        // baseファイル
        String outputFilePath = String.format("%s%s", namePath, FILE_EXTENSION);
        MyFile.mkdirs(outputFilePath);
        MyFile base = new MyFile(outputFilePath);
        base.verify();

        // 保存
        this.pathEntryPoint = pathEntryPoint;
        this.reachable = reachable;
        ColorConverter colorConverter = new ColorConverter();
        this.tetfuParser = createTetfuParser(pathSettings.isTetfuSplit(), minoFactory, colorConverter);
        this.outputBaseFile = base;
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
        this.lastException = null;

        outputLog("Found path = " + pathPairs.size());

        int maxClearLine = sizedBit.getHeight();
        try (BufferedWriter writer = outputBaseFile.newBufferedWriter()) {
            writer.write("tetfu,pattern,using,valid(pattern),valid(solution)");
            writer.newLine();

            pathPairs.parallelStream()
                    .map(pathPair -> {
                        // テト譜
                        Result result = pathPair.getResult();
                        LinkedList<OperationWithKey> operations = result.getMemento().getOperationsStream(sizedBit.getWidth()).collect(Collectors.toCollection(LinkedList::new));
                        String encode = tetfuParser.parse(operations, field, maxClearLine);

                        // 対応パターン数
                        int pattern = pathPair.getBuildBlocks().size();

                        // 使用ミノをまとめる
                        String usingPieces = operations.stream()
                                .map(OperationWithKey::getMino)
                                .map(Mino::getBlock)
                                .sorted()
                                .map(Block::getName)
                                .collect(Collectors.joining());

                        // パターンに対する有効なミノ順をまとめる
                        String validOrders1 = pathPair.getBuildBlocks().stream()
                                .map(longBlocks -> longBlocks.blockStream().map(Block::getName).collect(Collectors.joining()))
                                .collect(Collectors.joining(";"));

                        // 地形に対する有効なミノ順をまとめる
                        BuildUpStream buildUpStream = new BuildUpStream(reachable, maxClearLine);
                        String validOrders2 = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                                .map(operationWithKeys -> operationWithKeys.stream().map(OperationWithKey::getMino).map(Mino::getBlock).map(Block::getName).collect(Collectors.joining()))
                                .collect(Collectors.joining(";"));

                        return String.format("http://fumen.zui.jp/?v115@%s,%d,%s,%s,%s%n", encode, pattern, usingPieces, validOrders1, validOrders2);
                    })
                    .forEach(line -> {
                        try {
                            writer.write(line);
                        } catch (IOException e) {
                            this.lastException = e;
                        }
                    });
            writer.flush();
        } catch (IOException e) {
            throw new FinderExecuteException("Failed to output file", e);
        }

        if (lastException != null)
            throw new FinderExecuteException("Error to output file", lastException);
    }

    private void outputLog(String str) throws FinderExecuteException {
        pathEntryPoint.output(str);
    }

    private void outputOperationsToCSV(Field field, MyFile file, List<PathPair> pathPairs, SizedBit sizedBit) throws FinderExecuteException {
        LockedBuildUpListUpThreadLocal threadLocal = new LockedBuildUpListUpThreadLocal(sizedBit.getHeight());
        List<List<OperationWithKey>> samples = pathPairs.parallelStream()
                .map(resultPair -> {
                    Result result = resultPair.getResult();
                    LinkedList<OperationWithKey> operations = result.getMemento().getOperationsStream(sizedBit.getWidth()).collect(Collectors.toCollection(LinkedList::new));

                    BuildUpStream buildUpStream = threadLocal.get();

                    return buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .findFirst()
                            .orElse(Collections.emptyList());
                })
                .collect(Collectors.toList());

        try (BufferedWriter writer = file.newBufferedWriter()) {
            for (List<OperationWithKey> operationWithKeys : samples) {
                Operations operations = OperationTransform.parseToOperations(field, operationWithKeys, sizedBit.getHeight());
                String operationLine = OperationInterpreter.parseToString(operations);
                writer.write(operationLine);
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            throw new FinderExecuteException("Failed to output file", e);
        }
    }

    private static class Obj {

    }
}
