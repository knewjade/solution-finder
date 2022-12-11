package entry.path.output;

import common.buildup.BuildUpStream;
import common.datastore.MinoOperationWithKey;
import common.datastore.Operations;
import common.parser.OperationInterpreter;
import common.parser.OperationTransform;
import core.field.Field;
import core.srs.MinoRotation;
import entry.path.*;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import searcher.pack.SizedBit;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.task.Result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CSVPathOutput implements PathOutput {
    private static final String FILE_EXTENSION = ".csv";

    private final PathEntryPoint pathEntryPoint;
    private final PathSettings settings;

    private final MyFile outputMinimalFile;
    private final MyFile outputUniqueFile;

    public CSVPathOutput(PathEntryPoint pathEntryPoint, PathSettings pathSettings) throws FinderInitializeException {
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

        PathLayer pathLayer = pathSettings.getPathLayer();

        // 結果を標準出力に表示するか
        boolean resultOutputToConsole = pathSettings.isResultOutputToConsole();

        // uniqueファイル
        String uniqueOutputFilePath = String.format("%s_unique%s", namePath, FILE_EXTENSION);
        MyFile unique = new MyFile(uniqueOutputFilePath, resultOutputToConsole && pathLayer == PathLayer.Unique);
        unique.verify();

        // minimalファイル
        String minimalOutputFilePath = String.format("%s_minimal%s", namePath, FILE_EXTENSION);
        MyFile minimal = new MyFile(minimalOutputFilePath, resultOutputToConsole && pathLayer == PathLayer.Minimal);
        minimal.verify();

        // 保存
        this.pathEntryPoint = pathEntryPoint;
        this.settings = pathSettings;
        this.outputUniqueFile = unique;
        this.outputMinimalFile = minimal;
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
        List<PathPair> pathPairList = pathPairs.getUniquePathPairList();

        PathLayer pathLayer = settings.getPathLayer();
        boolean use180Rotation = this.settings.getDropType().uses180Rotation();

        Supplier<MinoRotation> minoRotationSupplier = settings.createMinoRotationSupplier();

        // 同一ミノ配置を取り除いたパスの出力
        if (pathLayer.contains(PathLayer.Unique)) {
            outputLog("Found path [unique] = " + pathPairList.size());
            outputOperationsToCSV(minoRotationSupplier, field, outputUniqueFile, pathPairList, sizedBit, use180Rotation);
        }

        // 少ないパターンでカバーできるパスを出力
        if (pathLayer.contains(PathLayer.Minimal)) {
            List<PathPair> minimal = pathPairs.getMinimalPathPairList(settings.getMinimalSpecifiedOnly());
            outputLog("Found path [minimal] = " + minimal.size());
            outputOperationsToCSV(minoRotationSupplier, field, outputMinimalFile, minimal, sizedBit, use180Rotation);
        }
    }

    private void outputLog(String str) throws FinderExecuteException {
        pathEntryPoint.output(str);
    }

    private void outputOperationsToCSV(
            Supplier<MinoRotation> minoRotationSupplier, Field field, MyFile file,
            List<PathPair> pathPairs, SizedBit sizedBit, boolean use180Rotation
    ) throws FinderExecuteException {
        LockedBuildUpListUpThreadLocal threadLocal = new LockedBuildUpListUpThreadLocal(minoRotationSupplier, sizedBit.getHeight(), use180Rotation);
        List<List<MinoOperationWithKey>> samples = pathPairs.parallelStream()
                .map(resultPair -> {
                    Result result = resultPair.getResult();
                    LinkedList<MinoOperationWithKey> operations = result.getMemento()
                            .getSeparableMinoStream(sizedBit.getWidth())
                            .map(SeparableMino::toMinoOperationWithKey)
                            .collect(Collectors.toCollection(LinkedList::new));

                    BuildUpStream buildUpStream = threadLocal.get();

                    return buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .findFirst()
                            .orElse(Collections.emptyList());
                })
                .collect(Collectors.toList());

        try (BufferedWriter writer = file.newBufferedWriter()) {
            for (List<MinoOperationWithKey> operationWithKeys : samples) {
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
}
