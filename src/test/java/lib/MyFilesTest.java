package lib;

import com.google.common.io.Files;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MyFilesTest {
    @Test
    void writeList() throws IOException {
        List<String> lines = Arrays.asList(
                "# hello",
                "test",
                "1",
                "",
                "こんにちは"
        );

        File tempDir = Files.createTempDir();
        String path = tempDir.getAbsolutePath() + File.separator + "test";
        MyFiles.write(path, lines);

        List<String> strings = Files.readLines(new File(path), StandardCharsets.UTF_8);
        assertThat(strings).isEqualTo(lines);
    }
}