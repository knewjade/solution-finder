package _experimental.square4x10;

import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import common.parser.OperationWithKeyInterpreter;
import core.mino.Mino;
import core.mino.MinoFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

// csv (SRS)から7Bagで使えそうなパターンだけを抽出
public class SquareFigureStep2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        String name = "result_9x4";
        String inputPath = "input/" + name + ".csv";
        String outputPath = "output/" + name + ".csv";

        MinoFactory minoFactory = new MinoFactory();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(outputPath), false))) {
            Files.lines(Paths.get(inputPath))
                    .parallel()
                    .filter(s -> {
                        Stream<OperationWithKey> operationWithKeyStream = OperationWithKeyInterpreter.parseToStream(s, minoFactory);

                        // BlockCounterに変換
                        BlockCounter blockCounter = new BlockCounter(
                                operationWithKeyStream
                                        .map(OperationWithKey::getMino)
                                        .map(Mino::getBlock)
                        );

                        return SevenBagFilter.isIn7Bag(blockCounter);
                    })
                    .forEach(s -> {
                        executorService.submit(() -> {
                            try {
                                bufferedWriter.write(s);
                                bufferedWriter.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    });

            executorService.shutdown();
            executorService.awaitTermination(10L, TimeUnit.DAYS);

            bufferedWriter.flush();
        }
    }
}
