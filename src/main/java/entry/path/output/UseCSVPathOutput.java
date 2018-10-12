package entry.path.output;

import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.datastore.PieceCounter;
import common.datastore.blocks.LongPieces;
import common.pattern.PatternGenerator;
import core.field.Field;
import core.mino.Piece;
import entry.path.*;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import lib.AsyncBufferedFileWriter;
import searcher.pack.SizedBit;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UseCSVPathOutput implements PathOutput {
    private static final String FILE_EXTENSION = ".csv";

    private final PathEntryPoint pathEntryPoint;

    private final MyFile outputBaseFile;
    private final ReducePatternGenerator generator;

    public UseCSVPathOutput(PathEntryPoint pathEntryPoint, PathSettings pathSettings, PatternGenerator generator, int maxDepth) throws FinderInitializeException {
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
        this.generator = new ReducePatternGenerator(generator, maxDepth);
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

        outputLog("Found path = " + pathPairList.size());

        AtomicInteger allCounter = new AtomicInteger();

        Map<PieceCounter, List<PathPair>> groupingByClockCounter = pathPairList.parallelStream()
                .collect(Collectors.groupingBy(pathPair -> {
                    List<MinoOperationWithKey> operations = pathPair.getSampleOperations();
                    return new PieceCounter(operations.stream().map(OperationWithKey::getPiece));
                }));

        List<PathPair> emptyValidList = Collections.emptyList();
        try (AsyncBufferedFileWriter writer = outputBaseFile.newAsyncWriter()) {
            writer.writeAndNewLine("使用ミノ,対応地形数,対応ツモ数 (対パターン),テト譜,ツモ (対パターン)");

            generator.blockCountersStream().parallel()
                    .map(blockCounter -> {
                        // カウンターをインクリメント
                        allCounter.incrementAndGet();

                        // 組み合わせ名を取得
                        String blockCounterName = blockCounter.getBlockStream()
                                .map(Piece::getName)
                                .collect(Collectors.joining());

                        // パフェ可能な地形を抽出
                        List<PathPair> valid = groupingByClockCounter.getOrDefault(blockCounter, emptyValidList);

                        // パフェ可能な地形数
                        int possibleSize = valid.size();

                        // パフェ可能な地形のテト譜を連結
                        String fumens = valid.stream()
                                .sorted(Comparator.comparing(PathPair::getPatternSize).reversed())
                                .map(pathPair -> "v115@" + pathPair.getFumen())
                                .collect(Collectors.joining(";"));

                        // 対応できるパターンを重複なく抽出
                        Set<LongPieces> possiblePatternSet = valid.stream()
                                .flatMap(PathPair::blocksStreamForPattern)
                                .collect(Collectors.toSet());

                        // 対応できるパターン数
                        int possiblePatternSize = possiblePatternSet.size();

                        // パターンを連結
                        String patterns = possiblePatternSet.stream()
                                .map(LongPieces::getPieces)
                                .map(blocks -> blocks.stream().map(Piece::getName).collect(Collectors.joining("")))
                                .collect(Collectors.joining(";"));

                        return String.format("%s,%d,%d,%s,%s", blockCounterName, possibleSize, possiblePatternSize, fumens, patterns);
                    })
                    .forEach(writer::writeAndNewLine);

            writer.flush();
        } catch (IOException e) {
            throw new FinderExecuteException("Failed to output file", e);
        }

        outputLog("Found piece combinations = " + allCounter.get());
    }

    private void outputLog(String str) throws FinderExecuteException {
        pathEntryPoint.output(str);
    }
}
