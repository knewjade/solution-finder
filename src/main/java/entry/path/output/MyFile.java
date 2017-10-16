package entry.path.output;

import exceptions.FinderInitializeException;
import lib.AsyncBufferedFileWriter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class MyFile {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    static void mkdirs(String baseFilePath) throws FinderInitializeException {
        File outputFile = new File(baseFilePath);

        // 親ディレクトリがない場合は作成
        if (!outputFile.getParentFile().exists()) {
            boolean mkdirsSuccess = outputFile.getParentFile().mkdirs();
            if (!mkdirsSuccess) {
                throw new FinderInitializeException("Failed to make output directory: OutputBase=" + baseFilePath);
            }
        }
    }

    private final String path;
    private final File file;

    MyFile(String path) {
        this.path = path;
        this.file = new File(path);
    }

    void verify() throws FinderInitializeException {
        if (file.isDirectory())
            throw new FinderInitializeException("Cannot specify directory as output file path: Path=" + path);

        if (file.exists() && !file.canWrite())
            throw new FinderInitializeException("Cannot write output file: Path=" + path);
    }

    AsyncBufferedFileWriter newAsyncWriter() throws IOException {
        return new AsyncBufferedFileWriter(file, CHARSET, false, 10L);
    }

    BufferedWriter newBufferedWriter() throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), CHARSET));
    }
}
