package entry.path.output;

import common.datastore.OperationWithKey;
import common.datastore.pieces.LongBlocks;
import core.field.Field;
import core.mino.Block;
import core.mino.Mino;
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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FumenCSVPathOutput implements PathOutput {
    private static final String FILE_EXTENSION = ".csv";

    private final PathEntryPoint pathEntryPoint;

    private final MyFile outputBaseFile;
    private Exception lastException = null;

    public FumenCSVPathOutput(PathEntryPoint pathEntryPoint, PathSettings pathSettings) throws FinderInitializeException {
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

    @Override
    public void output(List<PathPair> pathPairs, Field field, SizedBit sizedBit) throws FinderExecuteException {
        this.lastException = null;

        outputLog("Found path = " + pathPairs.size());

        try (BufferedWriter writer = outputBaseFile.newBufferedWriter()) {
            writer.write("tetfu,pattern,using,valid(pattern),valid(solution)");
            writer.newLine();

            pathPairs.parallelStream()
                    .map(pathPair -> {
                        // テト譜
                        String encode = pathPair.getFumen();

                        // パターンに対する有効なミノ順をまとめる
                        List<LongBlocks> patternBuildBlocks = pathPair.blocksStreamForPattern().collect(Collectors.toList());

                        // 対応パターン数
                        int pattern = patternBuildBlocks.size();

                        // 使用ミノをまとめる
                        String usingPieces = pathPair.getUsingBlockName();

                        // パターンに対する有効なミノ順をまとめる
                        String validOrdersPattern = patternBuildBlocks.stream()
                                .map(longBlocks -> longBlocks.blockStream().map(Block::getName).collect(Collectors.joining()))
                                .collect(Collectors.joining(";"));

                        // 地形に対する有効なミノ順をまとめる
                        String validOrdersSolution = pathPair.blocksStreamForSolution()
                                .map(longBlocks -> longBlocks.blockStream().map(Block::getName).collect(Collectors.joining()))
                                .collect(Collectors.joining(";"));

                        return String.format("http://fumen.zui.jp/?v115@%s,%d,%s,%s,%s%n", encode, pattern, usingPieces, validOrdersPattern, validOrdersSolution);
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
}
