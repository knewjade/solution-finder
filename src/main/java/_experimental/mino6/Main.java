package _experimental.mino6;

import common.datastore.Pair;
import core.field.Field;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) throws IOException {
        Charset charset = Charset.defaultCharset();
        File outputFile = new File("link");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false), charset))) {
            // ファイルから読み込む
            File file = new File("sorted");
            Files.readAllLines(file.toPath(), charset).stream()
                    .filter(s -> !s.startsWith("0.00"))
                    .map(s -> {
                        String[] split = s.split(",");
                        return new Pair<>(split[0], split[1]);
                    })
                    .forEach(pair -> {
                        String percent = pair.getKey();
                        String url = pair.getValue();
                        String format = "<li><a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s %% => http://fumen.zui.jp/?v115@%s </a></li>";
                        String element = String.format(format, url, percent, url);
                        try {
                            writer.write(element);
                            writer.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            writer.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to output file", e);
        }

    }
}
