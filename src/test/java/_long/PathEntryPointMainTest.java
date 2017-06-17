package _long;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import entry.EntryPointMain;
import entry.path.OutputType;
import entry.path.PathLayer;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PathEntryPointMainTest {
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
    public void testLog1() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXXX____X",
                "XXXXXX___X",
                "XXXXXXX__X",
                "XXXXXX___X"
        );
        String pattern = "*p4";
        String log = getPathLog(fields, pattern, true);
        assertThat(log, containsString("Found path [unique] = 18"));
        assertThat(log, containsString("Found path [minimal] = 16"));
    }

    @Test
    public void testLog1HeadT() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXXX____X",
                "XXXXXX___X",
                "XXXXXXX__X",
                "XXXXXX___X"
        );
        String pattern = "T, *p3";
        String log = getPathLog(fields, pattern, false);
        assertThat(log, containsString("Found path [unique] = 9"));
        assertThat(log, containsString("Found path [minimal] = 8"));
    }

    @Test
    public void testUnique1() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXXX____X",
                "XXXXXX___X",
                "XXXXXXX__X",
                "XXXXXX___X"
        );
        String pattern = "*p4";
        String output = getPathOutput(fields, pattern, true, PathLayer.Unique, OutputType.Link);
        assertThat(output, containsString("18パターン"));
    }

    @Test
    public void testMinimal1() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXXX____X",
                "XXXXXX___X",
                "XXXXXXX__X",
                "XXXXXX___X"
        );
        String pattern = "*p4";
        String output = getPathOutput(fields, pattern, true, PathLayer.Minimal, OutputType.Link);
        assertThat(output, containsString("16パターン"));
    }

    @Test
    public void testUnique2() throws Exception {
        List<String> fields = Arrays.asList(
                "3",
                "____XXX__X",
                "X__XXXXXXX",
                "X__XXXX__X"
        );
        String pattern = "[OSZ]p3";
        String output = getPathOutput(fields, pattern, true, PathLayer.Unique, OutputType.Link);
        assertThat(output, containsString("2パターン"));
    }

    @Test
    public void testMinimal2() throws Exception {
        List<String> fields = Arrays.asList(
                "3",
                "____XXX__X",
                "X__XXXXXXX",
                "X__XXXX__X"
        );
        String pattern = "[OSZ]p3";
        String output = getPathOutput(fields, pattern, true, PathLayer.Minimal, OutputType.Link);
        assertThat(output, containsString("2パターン"));
    }

    @Test
    public void testUnique3() throws Exception {
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
        assertThat(output, containsString(count + "パターン"));
        assertThat(output.split("href='http://fumen.zui.jp").length, is(count + 1));
        assertThat(output.split("ライン消去あり")[0].split("href='http://fumen.zui.jp").length, is(2 + 1));
        assertThat(output.split("ライン消去あり")[1].split("href='http://fumen.zui.jp").length, is(count - 2 + 1));
    }

    @Test
    public void testMinimal3() throws Exception {
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
        assertThat(output, containsString(count + "パターン"));
        assertThat(output.split("href='http://fumen.zui.jp").length, is(count + 1));
        assertThat(output.split("ライン消去あり")[0].split("href='http://fumen.zui.jp").length, is(2 + 1));
        assertThat(output.split("ライン消去あり")[1].split("href='http://fumen.zui.jp").length, is(count - 2 + 1));
    }

    @Test
    public void testUniqueCSV4() throws Exception {
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
        assertThat(output.split(System.lineSeparator()).length, is(173));
    }

    @Test
    public void testMinimalCSV4() throws Exception {
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
        assertThat(output.split(System.lineSeparator()).length, is(130));
    }

    @Test
    public void testMinimalCSV5() throws Exception {
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
        assertThat(output.split(System.lineSeparator()).length, is(1));
        assertThat(output, containsString("I,L,9,1;O,0,0,0"));
    }

    @Test
    public void testUnique6() throws Exception {
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
        System.out.println(log);
        assertThat(log, containsString("Found path [unique] = 1002"));
        assertThat(log, containsString("Found path [minimal] = 699"));
    }
}
