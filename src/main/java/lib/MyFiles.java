package lib;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class MyFiles {
    public static void write(String path, List<String> lines) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), charset))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
        }
    }

    public static Stream<String> lines(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            return lines;
        }
    }

    public static Stream<String> lines(Path path, Charset charset) throws IOException {
        try (Stream<String> lines = Files.lines(path, charset)) {
            return lines;
        }
    }
}
