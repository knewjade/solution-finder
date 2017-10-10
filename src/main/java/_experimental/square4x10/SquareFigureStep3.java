package _experimental.square4x10;

import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import common.parser.OperationWithKeyInterpreter;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

// 使用している各ミノの個数をもとに、ファイルを振り分ける
public class SquareFigureStep3 {
    public static void main(String[] args) throws IOException {
        String squareSize = "4x4";
        String squareName = "result_" + squareSize;
        String inputPath = "input/" + squareName + ".csv";
        String outputDirectory = String.format("output/%s", squareSize);
        new File(outputDirectory).mkdirs();

        // 初期化
        MinoFactory minoFactory = new MinoFactory();
        HashMap<String, BufferedWriter> writers = new HashMap<>();

        // 出力
        Files.lines(Paths.get(inputPath))
                .forEach(line -> {
                    Stream<OperationWithKey> operationWithKeyStream = OperationWithKeyInterpreter.parseToStream(line, minoFactory);

                    // BlockCounterに変換
                    BlockCounter blockCounter = new BlockCounter(
                            operationWithKeyStream
                                    .map(OperationWithKey::getMino)
                                    .map(Mino::getBlock)
                    );

                    // BlockCounter -> Name
                    String name = toName(blockCounter);

                    // 使用ミノ種類数
                    int size = blockCounter.getEnumMap().keySet().size();

                    // アウトプットファイル名
                    String key = String.format("%s/%d_%s", outputDirectory, size, name);

                    // Writer
                    BufferedWriter writer = writers.computeIfAbsent(key, k -> {
                        try {
                            return new BufferedWriter(new FileWriter(new File(k)));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    assert writer != null;

                    // 書き出し
                    try {
                        writer.write(String.valueOf(line));
                        writer.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        // close
        for (BufferedWriter writer : writers.values()) {
            writer.flush();
            writer.close();
        }
    }

    private static String toName(BlockCounter blockCounter) {
        EnumMap<Block, Integer> map = blockCounter.getEnumMap();
        List<Integer> values = new ArrayList<>(map.values());
        values.sort(Comparator.reverseOrder());

        StringBuilder builder = new StringBuilder();
        for (Integer value : values)
            builder.append(value);
        return builder.toString();
    }
}
