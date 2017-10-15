package lib;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncBufferedFileWriterTest {
    @Test
    void read() throws IOException, InterruptedException {
        File file = File.createTempFile("output", ".txt");
        Charset charset = Charset.defaultCharset();

        // Write
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        int maxThread = 10;
        int maxCount = 100;
        try (AsyncBufferedFileWriter writer = new AsyncBufferedFileWriter(file, charset, false, 10L)) {
            for (int thread = 0; thread < maxThread; thread++) {
                int numberOfThread = thread;
                executorService.submit(() -> {
                    for (int count = 0; count < maxCount; count++)
                        writer.writeAndNewLine(String.format("n%d-%02d", numberOfThread, count));
                });
            }
            executorService.shutdown();
            executorService.awaitTermination(3L, TimeUnit.SECONDS);
        }

        // Read
        assertThat(Files.lines(file.toPath(), charset).count()).isEqualTo(maxThread * maxCount);

        for (int thread = 0; thread < maxThread; thread++) {
            int numberOfThread = thread;
            List<String> lines = Files.lines(file.toPath(), charset)
                    .filter(line -> line.startsWith("n" + numberOfThread))
                    .collect(Collectors.toList());

            assertThat(lines).isEqualTo(
                    IntStream.range(0, maxCount)
                            .boxed()
                            .map(count -> String.format("n%d-%02d", numberOfThread, count))
                            .collect(Collectors.toList())
            );
        }
    }
}