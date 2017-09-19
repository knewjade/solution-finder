package entry.path.output;

import common.datastore.BlockCounter;
import common.datastore.pieces.LongBlocks;
import common.pattern.BlocksGenerator;
import core.field.Field;
import core.mino.Block;
import entry.path.PathEntryPoint;
import entry.path.PathPair;
import entry.path.PathSettings;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import lib.ListComparator;
import searcher.pack.SizedBit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class NoUseCSVPathOutput implements PathOutput {
    private static final String FILE_EXTENSION = ".csv";

    private final PathEntryPoint pathEntryPoint;

    private final MyFile outputBaseFile;
    private Exception lastException = null;

    public NoUseCSVPathOutput(PathEntryPoint pathEntryPoint, PathSettings pathSettings) throws FinderInitializeException {
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
        
        Map<BlockCounter, HashSet<LongBlocks>> groupingByHold = new HashMap<>();
        for (PathPair pathPair : pathPairs) {
            BlockCounter usingBlockCounter = pathPair.getUsingBlockCounter();
            for (LongBlocks blocks : pathPair.blocksHashSetForPattern()) {
                BlockCounter blockCounter = new BlockCounter(blocks.blockStream());
                BlockCounter noUse = blockCounter.removeAndReturnNew(usingBlockCounter);
                HashSet<LongBlocks> sets = groupingByHold.computeIfAbsent(noUse, key -> new HashSet<>());
                sets.add(blocks);
            }
        }

        try (BufferedWriter writer = outputBaseFile.newBufferedWriter()) {
            Comparator<Block> blockComparator = Comparator.naturalOrder();
            ListComparator<Block> blockListComparator = new ListComparator<>(blockComparator);
            ArrayList<BlockCounter> keys = new ArrayList<>(groupingByHold.keySet());
            keys.sort((o1, o2) -> blockListComparator.compare(o1.getBlocks(), o2.getBlocks()));
            keys.stream()
                    .map(blockCounter -> {
                        HashSet<LongBlocks> possiblePatternSet = groupingByHold.get(blockCounter);

                        // 未使用ミノを取得
                        String blockCounterName = blockCounter.getBlockStream()
                                .map(Block::getName)
                                .collect(Collectors.joining());

                        // 対応できるパターン数
                        int possiblePatternSize = possiblePatternSet.size();

                        // パターンを連結
                        String patterns = possiblePatternSet.stream()
                                .map(LongBlocks::getBlocks)
                                .map(blocks -> blocks.stream().map(Block::getName).collect(Collectors.joining("")))
                                .collect(Collectors.joining(";"));

                        return String.format("%s,%d,%s%n", blockCounterName, possiblePatternSize, patterns);
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
