package entry.path.output;

import common.buildup.BuildUpStream;
import common.datastore.OperationWithKey;
import common.datastore.Operations;
import common.datastore.pieces.LongBlocks;
import common.parser.OperationInterpreter;
import common.parser.OperationTransform;
import core.field.Field;
import entry.path.*;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import searcher.pack.SizedBit;
import searcher.pack.task.Result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CSVPathOutput implements PathOutput {
    private static final String FILE_EXTENSION = ".csv";
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final StandardOpenOption[] FILE_OPEN_OPTIONS = new StandardOpenOption[]{
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.CREATE
    };

    private final PathEntryPoint pathEntryPoint;
    private final PathSettings settings;

    private final File outputMinimalFile;
    private final File outputUniqueFile;

    public CSVPathOutput(PathEntryPoint pathEntryPoint, PathSettings pathSettings) throws FinderInitializeException {
        this.pathEntryPoint = pathEntryPoint;
        this.settings = pathSettings;

        // 出力ファイルが正しく出力できるか確認
        String outputBaseFilePath = settings.getOutputBaseFilePath();
        String namePath = getRemoveExtensionFromPath(outputBaseFilePath);

        // pathが空 または ディレクトリであるとき、pathを追加して、ファイルにする
        if (namePath.isEmpty() || namePath.endsWith(String.valueOf(File.separatorChar)))
            namePath += "path";

        // baseファイル
        String outputFilePath = String.format("%s%s", namePath, FILE_EXTENSION);
        File outputFile = new File(outputFilePath);

        // 親ディレクトリがない場合は作成
        if (!outputFile.getParentFile().exists()) {
            boolean mkdirsSuccess = outputFile.getParentFile().mkdirs();
            if (!mkdirsSuccess) {
                throw new FinderInitializeException("Failed to make output directory: OutputBase=" + outputBaseFilePath);
            }
        }

        // uniqueファイル
        String uniqueOutputFilePath = String.format("%s_unique%s", namePath, FILE_EXTENSION);
        this.outputUniqueFile = new File(uniqueOutputFilePath);

        if (outputUniqueFile.isDirectory())
            throw new FinderInitializeException("Cannot specify directory as output unique file path: OutputBase=" + outputBaseFilePath);
        if (outputUniqueFile.exists() && !outputUniqueFile.canWrite())
            throw new FinderInitializeException("Cannot write output unique file: OutputBase=" + outputBaseFilePath);

        // minimalファイル
        String minimalOutputFilePath = String.format("%s_minimal%s", namePath, FILE_EXTENSION);
        this.outputMinimalFile = new File(minimalOutputFilePath);

        if (outputMinimalFile.isDirectory())
            throw new FinderInitializeException("Cannot specify directory as output minimal file path: OutputBase=" + outputBaseFilePath);
        if (outputMinimalFile.exists() && !outputMinimalFile.canWrite())
            throw new FinderInitializeException("Cannot write output minimal file: OutputBase=" + outputBaseFilePath);
    }

    private String getCanonicalPath(String path) throws FinderInitializeException {
        try {
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            throw new FinderInitializeException(e);
        }
    }

    private String getRemoveExtensionFromPath(String filePath) throws FinderInitializeException {
        String canonicalPath = getCanonicalPath(filePath);

        int pointIndex = canonicalPath.lastIndexOf('.');
        int separatorIndex = canonicalPath.lastIndexOf(File.separatorChar);

        // .がない or セパレータより前にあるとき
        if (pointIndex <= separatorIndex)
            return canonicalPath;

        // .があるとき
        if (pointIndex != -1)
            return canonicalPath.substring(0, pointIndex);

        return canonicalPath;
    }

    @Override
    public void output(List<PathPair> pathPairs, Field field, SizedBit sizedBit) throws FinderExecuteException {
        PathLayer pathLayer = settings.getPathLayer();

        // 同一ミノ配置を取り除いたパスの出力
        if (pathLayer.contains(PathLayer.Unique)) {
            outputLog("Found path [unique] = " + pathPairs.size());
            outputOperationsToCSV(field, outputUniqueFile, pathPairs, sizedBit);
        }

        // 少ないパターンでカバーできるパスを出力
        if (pathLayer.contains(PathLayer.Minimal)) {
            Selector<PathPair, LongBlocks> selector = new Selector<>(pathPairs);
            List<PathPair> minimal = selector.select();
            outputLog("Found path [minimal] = " + minimal.size());
            outputOperationsToCSV(field, outputMinimalFile, minimal, sizedBit);
        }
    }

    private void outputLog(String str) throws FinderExecuteException {
        pathEntryPoint.output(str);
    }

    private void outputOperationsToCSV(Field field, File file, List<PathPair> pathPairs, SizedBit sizedBit) throws FinderExecuteException {
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

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), CHARSET, FILE_OPEN_OPTIONS)) {
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
}
