package entry.path.output;

import common.datastore.blocks.LongPieces;
import core.field.Field;
import core.mino.Piece;
import entry.path.PathEntryPoint;
import entry.path.PathPair;
import entry.path.PathSettings;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import lib.AsyncBufferedFileWriter;
import searcher.pack.SizedBit;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
        MyFile base = new MyFile(outputFilePath);
        base.mkdirs();
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

        try (AsyncBufferedFileWriter writer = outputBaseFile.newAsyncWriter()) {
            writer.writeAndNewLine("テト譜,使用ミノ,対応ツモ数 (対地形&パターン),対応ツモ数 (対地形),対応ツモ数 (対パターン),ツモ (対地形),ツモ (対パターン)");

            pathPairs.parallelStream()
                    .map(pathPair -> {
                        // テト譜
                        String encode = pathPair.getFumen();

                        // パターンに対する有効なミノ順をまとめる
                        List<LongPieces> patternBuildBlocks = pathPair.blocksStreamForPattern().collect(Collectors.toList());

                        // 対応ツモ数 (対パターン)
                        int pattern = patternBuildBlocks.size();

                        // 使用ミノをまとめる
                        String usingPieces = pathPair.getUsingBlockName();

                        // 地形に対する有効なミノ順 && パターンに対して有効なミノ順をまとめる
                        AtomicInteger counterValidSolutions = new AtomicInteger();
                        String validOrdersValidSolution = pathPair.blocksStreamForValidSolution()
                                .peek(it -> counterValidSolutions.incrementAndGet())
                                .map(longBlocks -> longBlocks.blockStream().map(Piece::getName).collect(Collectors.joining()))
                                .collect(Collectors.joining(";"));

                        // 地形に対する有効なミノ順をまとめる
                        HashSet<LongPieces> solutionBuildBlocks = pathPair.blocksHashSetForSolution();
                        String validOrdersSolution = solutionBuildBlocks.stream()
                                .map(longBlocks -> longBlocks.blockStream().map(Piece::getName).collect(Collectors.joining()))
                                .collect(Collectors.joining(";"));

                        // パターンに対する有効なミノ順をまとめる
                        String validOrdersPattern = patternBuildBlocks.stream()
                                .map(longBlocks -> longBlocks.blockStream().map(Piece::getName).collect(Collectors.joining()))
                                .collect(Collectors.joining(";"));

                        // 対応ツモ数 (対地形)
                        int solution = solutionBuildBlocks.size();

                        return String.format("http://fumen.zui.jp/?v115@%s,%s,%d,%d,%d,%s,%s,%s", encode, usingPieces, counterValidSolutions.get(), solution, pattern, validOrdersValidSolution, validOrdersSolution, validOrdersPattern);
                    })
                    .forEach(writer::writeAndNewLine);

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
