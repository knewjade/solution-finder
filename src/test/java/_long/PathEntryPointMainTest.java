package _long;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import entry.EntryPointMain;
import entry.path.OutputType;
import entry.path.PathLayer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PathEntryPointMainTest {
    private String getPathLog(List<String> fields, String pattern, boolean isUsingHold) throws Exception {
        File fieldTempFile = createTempTextFile("field", String.join(System.lineSeparator(), fields));
        String fieldTempFilePath = fieldTempFile.getPath();

        // ログの作成
        File logTempFile = createTempTextFile("output", "");

        // パターンの作成
        File patternsTempFile = createTempTextFile("patterns", pattern);
        String patternsTempFilePath = patternsTempFile.getPath();

        String command = String.format("path -fp %s -pp %s -l %s -H %s", fieldTempFilePath, patternsTempFilePath, logTempFile, isUsingHold);
        EntryPointMain.main(command.split(" "));

        // 結果の取得
        List<String> lines = Files.readLines(logTempFile, Charsets.UTF_8);
        return String.join(System.lineSeparator(), lines);
    }

    private String getPathOutput(List<String> fields, String pattern, boolean isUsingHold, PathLayer pathLayer, OutputType outputType) throws Exception {
        File fieldTempFile = createTempTextFile("field", String.join(System.lineSeparator(), fields));
        String fieldTempFilePath = fieldTempFile.getPath();

        // ログの作成
        File outputFile = File.createTempFile("output", "");

        // パターンの作成
        File patternsTempFile = createTempTextFile("patterns", pattern);
        String patternsTempFilePath = patternsTempFile.getPath();

        String command = String.format("path -fp %s -pp %s -o %s -H %s -L %d -f %s", fieldTempFilePath, patternsTempFilePath, outputFile, isUsingHold, pathLayer.getNumber(), outputType.getTypeName());
        EntryPointMain.main(command.split(" "));

        // 結果の取得
        String linkPath = String.format("%s_%s.%s", outputFile.getCanonicalPath(), pathLayer.getName(), outputType.getExtension());
        List<String> lines = Files.readLines(new File(linkPath), Charsets.UTF_8);
        return String.join(System.lineSeparator(), lines);
    }

    private File createTempTextFile(String fileName, String text) throws IOException {
        File file = File.createTempFile(fileName, ".txt");
        file.deleteOnExit();
        Files.append(text, file, Charsets.UTF_8);
        return file;
    }

    @Test
    void testLog1() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXXX____X",
                "XXXXXX___X",
                "XXXXXXX__X",
                "XXXXXX___X"
        );
        String pattern = "*p4";
        String log = getPathLog(fields, pattern, true);
        assertThat(log).contains("Found path [unique] = 18");
        assertThat(log).contains("Found path [minimal] = 16");
    }

    @Test
    void testLog1HeadT() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXXX____X",
                "XXXXXX___X",
                "XXXXXXX__X",
                "XXXXXX___X"
        );
        String pattern = "T, *p3";
        String log = getPathLog(fields, pattern, false);
        assertThat(log).contains("Found path [unique] = 9");
        assertThat(log).contains("Found path [minimal] = 8");
    }

    @Test
    void testUnique1() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXXX____X",
                "XXXXXX___X",
                "XXXXXXX__X",
                "XXXXXX___X"
        );
        String pattern = "*p4";
        String output = getPathOutput(fields, pattern, true, PathLayer.Unique, OutputType.Link);
        assertThat(output).contains("18パターン");
    }

    @Test
    void testMinimal1() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXXX____X",
                "XXXXXX___X",
                "XXXXXXX__X",
                "XXXXXX___X"
        );
        String pattern = "*p4";
        String output = getPathOutput(fields, pattern, true, PathLayer.Minimal, OutputType.Link);
        assertThat(output).contains("16パターン");
    }

    @Test
    void testUnique2() throws Exception {
        List<String> fields = Arrays.asList(
                "3",
                "____XXX__X",
                "X__XXXXXXX",
                "X__XXXX__X"
        );
        String pattern = "[OSZ]p3";
        String output = getPathOutput(fields, pattern, true, PathLayer.Unique, OutputType.Link);
        assertThat(output).contains("2パターン");
    }

    @Test
    void testMinimal2() throws Exception {
        List<String> fields = Arrays.asList(
                "3",
                "____XXX__X",
                "X__XXXXXXX",
                "X__XXXX__X"
        );
        String pattern = "[OSZ]p3";
        String output = getPathOutput(fields, pattern, true, PathLayer.Minimal, OutputType.Link);
        assertThat(output).contains("2パターン");
    }

    @Test
    void testUnique3() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXX______",
                "XXXX______",
                "XXXX______",
                "XXXX______"
        );
        String pattern = "*p7";
        String output = getPathOutput(fields, pattern, true, PathLayer.Unique, OutputType.Link);

        // Source: myself 20170617
        int count = 245;
        assertThat(output).contains(count + "パターン");
        assertThat(output.split("href='http://fumen.zui.jp")).hasSize(count + 1);
        assertThat(output.split("ライン消去あり")[0].split("href='http://fumen.zui.jp")).hasSize(2 + 1);
        assertThat(output.split("ライン消去あり")[1].split("href='http://fumen.zui.jp")).hasSize(count - 2 + 1);
    }

    @Test
    void testMinimal3() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXX______",
                "XXXX______",
                "XXXX______",
                "XXXX______"
        );
        String pattern = "*p7";
        String output = getPathOutput(fields, pattern, true, PathLayer.Minimal, OutputType.Link);

        // Source: myself 20170617
        int count = 199;
        assertThat(output).contains(count + "パターン");
        assertThat(output.split("href='http://fumen.zui.jp")).hasSize(count + 1);
        assertThat(output.split("ライン消去あり")[0].split("href='http://fumen.zui.jp")).hasSize(2 + 1);
        assertThat(output.split("ライン消去あり")[1].split("href='http://fumen.zui.jp")).hasSize(count - 2 + 1);
    }

    @Test
    void testUniqueCSV4() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXX_______",
                "XXX______X",
                "XXX_____XX",
                "XXX______X"
        );
        String pattern = "*p7";
        String output = getPathOutput(fields, pattern, true, PathLayer.Unique, OutputType.CSV);

        // Source: myself 20170617
        assertThat(output.split(System.lineSeparator())).hasSize(173);
    }

    @Test
    void testMinimalCSV4() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXX_______",
                "XXX______X",
                "XXX_____XX",
                "XXX______X"
        );
        String pattern = "*p7";
        String output = getPathOutput(fields, pattern, true, PathLayer.Minimal, OutputType.CSV);

        // Source: myself 20170617
        assertThat(output.split(System.lineSeparator())).hasSize(130);
    }

    @Test
    void testMinimalCSV5() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXXXXXXX_",
                "XXXXXXXXX_",
                "__XXXXXXX_",
                "__XXXXXXX_"
        );
        String pattern = "*p2";
        String output = getPathOutput(fields, pattern, true, PathLayer.Minimal, OutputType.CSV);

        // Source: myself 20170617
        assertThat(output.split(System.lineSeparator())).hasSize(1);
        assertThat(output).contains("I,L,9,1;O,0,0,0");
    }

    @Test
    void testMinimal6() throws Exception {
        List<String> fields = Arrays.asList(
                "6",
                "XXXXXX____",
                "XXXXXX____",
                "XXXXXX____",
                "XXXXXX____",
                "XXXXXX____",
                "XXXXXX____"
        );
        String pattern = "*p7";
        String log = getPathLog(fields, pattern, true);

        // Source: myself 20170617
        assertThat(log).contains("Found path [unique] = 1002");
        assertThat(log).contains("Found path [minimal] = 699");
    }

    @Test
    void testMinimal7() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXXXX____",
                "XXXXXX____",
                "XXXXXX____",
                "XXXXXX____"
        );
        String pattern = "*p4";
        String log = getPathLog(fields, pattern, true);

        // Source: myself 20170701
        assertThat(log).contains("Found path [unique] = 135");
        assertThat(log).contains("Found path [minimal] = 69");
    }

    @Test
    @Tag("long")
    void testMinimal8() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "__________",
                "__________",
                "__________",
                "__________"
        );
        String pattern = "J,Z,O,S,L,I,I,J,S,O,Z";
        String log = getPathLog(fields, pattern, true);

        // Source: myself 20170701
        assertThat(log).contains("Found path [unique] = 71");
        assertThat(log).contains("Found path [minimal] = 10");
    }

    @Disabled
    @Test
    void testMinimal9() throws Exception {
        List<String> fields = Arrays.asList(
                "8",
                "__XXXXXXXX",
                "__XXXXXXXX",
                "__XXXXXXXX",
                "__XXXXXXXX",
                "__XXXXXXXX",
                "__XXXXXXXX",
                "__XXXXXXXX",
                "__XXXXXXXX"
        );
        String pattern = "*, *p4";
        String log = getPathLog(fields, pattern, true);

        // Source: myself 20170701
        assertThat(log).contains("Found path [unique] = 298");
        assertThat(log).contains("Found path [minimal] = 239");
    }

    @Test
    void testMinimal10() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "X_________",
                "XXXXXXXX__",
                "XXXXXXXXX_",
                "XXXXXX____"
        );
        String pattern = "S,L,O,I,T";
        String log = getPathLog(fields, pattern, true);

        // Source: myself 20170701
        assertThat(log).contains("Found path [unique] = 3");
        assertThat(log).contains("Found path [minimal] = 3");
    }
}
