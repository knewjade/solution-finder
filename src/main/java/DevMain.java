import lib.AsyncBufferedFileWriter;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DevMain {
    public static void main(String[] args) throws Exception {
//        String command = "path -f csv -k p -t v115@9gF8DeF8DeF8DeF8NeAgH -p T,*p4";
//        int returnCode = EntryPointMain.main(command.split(" "));
//        System.exit(returnCode);
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        try (AsyncBufferedFileWriter writer = new AsyncBufferedFileWriter(new File("output/test"), Charset.defaultCharset(), false, 10L)) {
            for (int i = 0; i < 10; i++) {
                int finalI = i;
                executorService.submit(() -> {
                    for (int j = 0; j < 10; j++) {
                        writer.writeAndNewLine(String.format("hello %d-%03d", finalI, j));
                    }
                });
            }
            executorService.shutdown();
            executorService.awaitTermination(100L, TimeUnit.SECONDS);
        }
    }
}
