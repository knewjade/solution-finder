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

import static org.assertj.core.api.Assertions.assertThat;

class PercentSpecialCaseTest extends PercentUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void filledLine() throws Exception {
        // ラインがすでに埋まっているケース

        Field field = FieldFactory.createField("" +
                "XXXXXX____" +
                "XXXXX_____" +
                "XXXXXXXXXX" +
                "XXXXXX___X"
        );

        ConfigFileHelper.createFieldFile(field, 4);
        ConfigFileHelper.createPatternFile("T, *p3");

        String command = "percent";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(190, 210))
                .contains("T, *p3")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(210))
                .contains(Messages.treeHeadSize(3))
                .doesNotContain(Messages.tree("*"))
                .contains(Messages.tree("T", 90.48))
                .contains(Messages.tree("TJ", 90.00))
                .contains(Messages.tree("TOI", 60.00))
                .contains(Messages.failPatternSize(100))
                .contains("[T, S, T, J]")
                .contains("[T, O, I, L]")
                .contains("[T, T, J, I]");

        assertThat(log.getError()).isEmpty();
    }
}
