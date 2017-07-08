package _long;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import entry.EntryPointMain;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class PercentEntryPointMainTest {
    private String getPercentLog(List<String> fields, String pattern, boolean isUsingHold) throws Exception {
        File fieldTempFile = createTempTextFile("field", String.join(System.lineSeparator(), fields));
        String fieldTempFilePath = fieldTempFile.getPath();

        // パターンの作成
        File patternsTempFile = createTempTextFile("patterns", pattern);
        String patternsTempFilePath = patternsTempFile.getPath();

        // ログの作成
        File logTempFile = createTempTextFile("output");

        String command = String.format("percent -fp %s -pp %s -l %s -H %s", fieldTempFilePath, patternsTempFilePath, logTempFile, isUsingHold);
        EntryPointMain.main(command.split(" "));

        // 結果の取得
        List<String> lines = Files.readLines(logTempFile, Charsets.UTF_8);
        return String.join(System.lineSeparator(), lines);
    }

    private File createTempTextFile(String name) throws IOException {
        return createTempTextFile(name, "");
    }

    private File createTempTextFile(String name, String str) throws IOException {
        File file = File.createTempFile(name, ".txt");
        file.deleteOnExit();
        Files.append(str, file, Charsets.UTF_8);
        return file;
    }

    @Test
    public void test1() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "__X_______",
                "__XX_____X",
                "XXXX_____X",
                "XXXXX___XX"
        );
        String pattern = "*p7";
        String expected = "success = 99.52% (5016/5040)";
        String join = getPercentLog(fields, pattern, true);
        assertThat(join, containsString(expected));
    }

    @Test
    public void test2() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "_______XXX",
                "_______XXX",
                "XX_____XXX",
                "XX_____XXX"
        );
        String pattern = "*p7";
        String expected = "success = 99.96% (5038/5040)";
        String join = getPercentLog(fields, pattern, true);
        assertThat(join, containsString(expected));
    }

    @Test
    public void test3() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "_______XXX",
                "_______XXX",
                "XX_____XXX",
                "XX_____XXX"
        );
        String pattern = "*p7";
        String expected = "success = 99.96% (5038/5040)";
        String join = getPercentLog(fields, pattern, true);
        assertThat(join, containsString(expected));
        assertThat(join, containsString("[L, S, J, T, Z, O, I]"));
        assertThat(join, containsString("[S, L, J, T, Z, O, I]"));
    }

    @Test
    public void test4() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "__________",
                "___X______",
                "XXXXXX____",
                "XXXXX_____"
        );
        String pattern = "T,*p7";
        String expected = "success = 100.00% (5040/5040)";
        String join = getPercentLog(fields, pattern, true);
        assertThat(join, containsString(expected));
    }

    @Test
    public void test5() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "__________",
                "__________",
                "__________",
                "__________"
        );
        String pattern = "S,[TZ]p2,*p7";
        String expected = "success = 100.00% (10080/10080)";
        String join = getPercentLog(fields, pattern, true);
        assertThat(join, containsString(expected));
    }

    @Test
    public void test6() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "__________",
                "__________",
                "_XX_______",
                "XX________"
        );
        String pattern = "[TZ]p2,*p7";
        String expected = "success = 100.00% (10080/10080)";
        String join = getPercentLog(fields, pattern, true);
        assertThat(join, containsString(expected));
    }

    @Test
    public void test7() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXX______X",
                "XX_______X",
                "XXXX_____X",
                "XXX______X"
        );
        String pattern = "*p7";
        String expected = "success = 84.96% (4282/5040)";
        String join = getPercentLog(fields, pattern, true);
        assertThat(join, containsString(expected));
        assertThat(join, containsString("T -> 99.9 %"));
    }

    @Test
    public void test8() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXX_______",
                "XX________",
                "XXXX______",
                "XXXXXXX___"
        );
        String pattern = "*p7";
        String expected = "success = 89.76% (4524/5040)";
        String join = getPercentLog(fields, pattern, true);
        assertThat(join, containsString(expected));
    }

    @Test
    public void test9() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "X_________",
                "XXXX______",
                "XXXXX_____",
                "XXXXXX____"
        );
        String pattern = "*p7";
        String expected = "success = 91.98% (4636/5040)";
        String join = getPercentLog(fields, pattern, true);
        assertThat(join, containsString(expected));
        assertThat(join, containsString("T -> 100.0 %"));
    }

    @Test
    public void test10WithHold() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXXXXXXX_",
                "XXXXXXXXX_",
                "__XXXXXXX_",
                "__XXXXXXX_"
        );
        String pattern = "[IO]p2";
        String expected = "success = 100.00% (2/2)";
        String join = getPercentLog(fields, pattern, true);
        assertThat(join, containsString(expected));
    }

    @Test
    public void test10WithoutHold() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXXXXXXX_",
                "XXXXXXXXX_",
                "__XXXXXXX_",
                "__XXXXXXX_"
        );
        String pattern = "[IO]p2";
        String expected = "success = 50.00% (1/2)";
        String join = getPercentLog(fields, pattern, false);
        assertThat(join, containsString(expected));
        assertThat(join, containsString("[O, I]"));
    }

    @Test
    public void test11() throws Exception {
        List<String> fields = Arrays.asList(
                "4",
                "XXXXXX____",
                "XXXXX_____",
                "XXXXXXXXXX",
                "XXXXXX___X"
        );
        String pattern = "T, *p3";
        String expected = "success = 90.48% (190/210)";
        String join = getPercentLog(fields, pattern, true);
        assertThat(join, containsString(expected));
        assertThat(join, containsString("Max clear lines: 4"));
    }
}
