package _usecase.percent;

import _usecase.ConfigFileHelper;
import _usecase.Log;
import _usecase.RunnerHelper;
import core.field.Field;
import core.field.FieldFactory;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PercentFileCaseTest extends PercentUseCaseBaseTest {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void useFieldFileAndPatternsFile1() throws Exception {
        // フィールドファイル + パターンファイル

        Field field = FieldFactory.createField("" +
                "__X_______" +
                "__XX_____X" +
                "XXXX_____X" +
                "XXXXX___XX"
        );

        ConfigFileHelper.createFieldFile(field, 4);
        ConfigFileHelper.createPatternFile("*p7");

        String command = "percent";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(5016, 5040))
                .contains("*p7")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(5040))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("*", 99.52))
                .contains(Messages.tree("T", 98.89))
                .contains(Messages.tree("LT", 96.67))
                .contains(Messages.tree("JTL", 83.33))
                .contains(Messages.failPatternSize(100))
                .doesNotContain(Messages.failNothing());

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void useFieldFileAndPatternsFile2() throws Exception {
        // フィールドファイル + パターンファイル

        Field field = FieldFactory.createField("" +
                "_______XXX" +
                "_______XXX" +
                "XX_____XXX" +
                "XX_____XXX"
        );

        ConfigFileHelper.createFieldFile(field, 4);
        ConfigFileHelper.createPatternFile("*p7");

        String command = "percent";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(5038, 5040))
                .contains("*p7")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(5040))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("*", 99.96))
                .contains(Messages.tree("T", 100.00))
                .contains(Messages.tree("SL", 99.17))
                .contains(Messages.tree("LSJ", 95.83))
                .contains(Messages.failPatternSize(100))
                .contains("[S, L, J, T, Z, O, I]")
                .contains("[L, S, J, T, Z, O, I]");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void useFieldFileAndCommand1() throws Exception {
        // フィールドファイル + パターンコマンド

        Field field = FieldFactory.createField("" +
                "XXX______X" +
                "XX_______X" +
                "XXXX_____X" +
                "XXX______X"
        );

        ConfigFileHelper.createFieldFile(field, 4);

        String command = "percent -p *p7";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(4282, 5040))
                .contains("*p7")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(5040))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("*", 84.96))
                .contains(Messages.tree("J", 82.64))
                .contains(Messages.tree("ST", 100.00))
                .contains(Messages.tree("ZSO", 45.83))
                .contains(Messages.failPatternSize(100))
                .doesNotContain(Messages.failNothing());

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void useFieldFileAndCommand2() throws Exception {
        // フィールドファイル + パターンコマンド (パターンファイル無視)

        Field field = FieldFactory.createField("" +
                "XXX_______" +
                "XX________" +
                "XXXX______" +
                "XXXXXXX___"
        );

        ConfigFileHelper.createFieldFile(field, 4);
        ConfigFileHelper.createPatternFile("*p4");

        String command = "percent -p *p7";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(4524, 5040))
                .contains("*p7")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(5040))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("*", 89.76))
                .contains(Messages.tree("L", 85.14))
                .contains(Messages.tree("SZ", 85.00))
                .contains(Messages.tree("OLZ", 91.67))
                .contains(Messages.failPatternSize(100))
                .doesNotContain(Messages.failNothing());

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void getLog() throws Exception {
        // フィールドファイル, パターンファイル, ログファイル (場所を変更する)

        Field field = FieldFactory.createField("" +
                "______XXXX" +
                "______XXXX" +
                "_____XXXXX" +
                "_______XXX"
        );

        int height = 4;

        ConfigFileHelper.createFieldFile(field, height, "test_field", "input");
        ConfigFileHelper.createPatternFile("*p7", "input", "test_patterns");

        String command = "percent -fp input/test_field.txt -pp input/test_patterns.txt --log-path test_output_log/test_last_output.txt";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        String logFile = Files.lines(Paths.get("test_output_log/test_last_output.txt")).collect(Collectors.joining(LINE_SEPARATOR)) + LINE_SEPARATOR;
        assertThat(log.getOutput())
                .isEqualTo(logFile);

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(4784, 5040))
                .contains("*p7")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(5040))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("*", 94.92))
                .contains(Messages.tree("Z", 91.94))
                .contains(Messages.tree("TS", 97.50))
                .contains(Messages.tree("LIS", 83.33))
                .contains(Messages.failPatternSize(100))
                .doesNotContain(Messages.failNothing());

        assertThat(log.getError()).isEmpty();
    }
}
