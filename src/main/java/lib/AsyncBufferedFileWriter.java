package lib;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

// 関数の呼び出し順に書き込まれることは保証する
// 書き込み時にエラーが発生した場合は RuntimeException が送出される
public class AsyncBufferedFileWriter implements Closeable, Flushable {
    private static final TimeUnit AWAIT_TERMINATION_UNIT = TimeUnit.SECONDS;

    private final BufferedWriter bufferedWriter;
    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private final LinkedBlockingDeque<Runnable> taskQueue = new LinkedBlockingDeque<>();
    private final long awaitTerminationInSec;

    public AsyncBufferedFileWriter(File file, Charset charset, boolean append, long awaitTerminationInSec) throws FileNotFoundException {
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset));
        this.awaitTerminationInSec = awaitTerminationInSec;
    }

    public void write(String line) {
        submitAndRun(() -> {
            try {
                bufferedWriter.write(line);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void writeAndNewLine(List<String> lines) {
        submitAndRun(() -> {
            try {
                for (String line : lines) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void writeAndNewLine(String line) {
        writeAndNewLine(Collections.singletonList(line));
    }

    private void submitAndRun(Runnable runnable) {
        taskQueue.addLast(runnable);
        singleThreadExecutor.submit(() -> {
            if (taskQueue.isEmpty())
                return;

            try {
                Runnable task = taskQueue.pollFirst(1L, TimeUnit.SECONDS);
                task.run();
            } catch (InterruptedException ignore) {
                ignore.printStackTrace();
            }
        });
    }

    @Override
    public void flush() throws IOException {
        bufferedWriter.flush();
    }

    @Override
    public void close() throws IOException {
        singleThreadExecutor.submit(() -> {
            while (!taskQueue.isEmpty()) {
                try {
                    Runnable task = taskQueue.pollFirst(1L, TimeUnit.SECONDS);
                    task.run();
                } catch (InterruptedException ignore) {
                    return;
                }
            }
        });
        singleThreadExecutor.shutdown();
        try {
            singleThreadExecutor.awaitTermination(awaitTerminationInSec, AWAIT_TERMINATION_UNIT);
        } catch (InterruptedException e) {
            throw new IOException(e);
        } finally {
            bufferedWriter.close();
        }
    }
}
