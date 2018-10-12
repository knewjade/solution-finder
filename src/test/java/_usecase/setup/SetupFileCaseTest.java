package _usecase.setup;

import _usecase.ConfigFileHelper;
import _usecase.Log;
import _usecase.RunnerHelper;
import _usecase.setup.files.OutputFileHelper;
import _usecase.setup.files.SetupHTML;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SetupFileCaseTest extends SetupUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void useFieldFileAndPatternsFile1() throws Exception {
        // フィールドファイル + パターンファイル

        String marks = "" +
                "++++++*___" +
                "******___X" +
                "*******_XX" +
                "........X_";
        ConfigFileHelper.createFieldFile(marks);
        ConfigFileHelper.createPatternFile("[^T]!");

        String command = "setup";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        // Log
        assertThat(log.getOutput()).contains(Messages.foundSolutions(43));
        assertThat(log.getError()).isEmpty();

        // HTML
        SetupHTML html = OutputFileHelper.loadSetupHTML();
        assertThat(html.getFumens())
                .hasSize(43)
                .contains("9gwhBeh0R4CewhRpg0R4CeA8whRpg0glBtAeB8whAe?ilAeBtA8KeAgWGAat/VCP+AAA")
                .contains("+gAtRpAeR4CeBtRpR4CeA8Atili0AeB8Aeglzhg0Ae?A8KeAgWGAp/rtCvXBAA");
    }

    @Test
    void useFieldFileAndPatternsFile1WithOptions() throws Exception {
        // フィールドファイル + パターンファイル

        String marks = "" +
                "++++++*___" +
                "******___X" +
                "*******_XX" +
                "........X_";
        ConfigFileHelper.createFieldFile(marks);
        ConfigFileHelper.createPatternFile("[^T]!");

        String command = "setup --operate T-Reverse(7,2) --exclude holes";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        // Log
        assertThat(log.getOutput()).contains(Messages.foundSolutions(14));
        assertThat(log.getError()).isEmpty();

        // HTML
        SetupHTML html = OutputFileHelper.loadSetupHTML();
        assertThat(html.getFumens())
                .hasSize(14)
                .contains("9gwhAeRpAeh0CewhQ4RpAtg0CeA8whR4Btg0glAeB8?whAeQ4AtilAeA8KeAgWGAs3HgCT+AAA")
                .contains("/gQ4zhCeh0R4RpCeA8g0BtQ4RpglAeB8g0AeBtilAe?A8KeAgWGAMuaFDz/AAA");
    }
}
