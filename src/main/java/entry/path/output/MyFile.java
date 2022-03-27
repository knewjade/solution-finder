package entry.path.output;

import exceptions.FinderInitializeException;
import lib.AsyncBufferedFileWriter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class MyFile {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final String path;
    private final File file;
    private final boolean outputToConsole;

    public MyFile(String path) {
        this(path, false);
    }

    public MyFile(String path, boolean outputToConsole) {
        this.path = path;
        this.file = new File(path);
        this.outputToConsole = outputToConsole;
    }

    public void mkdirs() throws FinderInitializeException {
        // 親ディレクトリが有効でない場合はエラー
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            throw new FinderInitializeException("Parent directory is not invalid: OutputBase=" + path);
        }

        // ベースディレクトリがない場合は作成
        if (!parentFile.exists()) {
            boolean mkdirsSuccess = parentFile.mkdirs();
            if (!mkdirsSuccess) {
                throw new FinderInitializeException("Failed to make output directory: OutputBase=" + path);
            }
        }
    }

    public void verify() throws FinderInitializeException {
        if (file.isDirectory())
            throw new FinderInitializeException("Cannot specify directory as output file path: Path=" + path);

        if (file.exists() && !file.canWrite())
            throw new FinderInitializeException("Cannot write output file: Path=" + path);
    }

    public AsyncBufferedFileWriter newAsyncWriter() throws IOException {
        return new AsyncBufferedFileWriter(newBufferedWriter(), 10L);
    }

    public BufferedWriter newBufferedWriter() throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(getOutputStream(), CHARSET));
    }

    private OutputStream getOutputStream() throws FileNotFoundException {
        if (outputToConsole) {
            return new OutputStream() {
                private final List<OutputStream> streams = Arrays.asList(
                        System.out,
                        new FileOutputStream(file, false)
                );

                @Override
                public void write(int b) throws IOException {
                    for (OutputStream stream : streams) {
                        stream.write(b);
                    }
                }

                @Override
                public void flush() throws IOException {
                    for (OutputStream stream : streams) {
                        stream.flush();
                    }
                }

                @Override
                public void close() throws IOException {
                    for (OutputStream stream : streams) {
                        stream.close();
                    }
                }
            };
        } else {
            return new FileOutputStream(file, false);
        }
    }
}
