package _usecase.setup;

import _usecase.Log;
import _usecase.RunnerHelper;
import _usecase.setup.files.OutputFileHelper;
import entry.EntryPointMain;
import helper.CSVStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class SetupCSVCaseTest extends SetupUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void case1() throws Exception {
            /*
            comment: <Empty>
            ___OOOOOOO
            ___OOOOOOO
            ____IIIIIO
            II_IIIIIII
            II_IIIIIII
             */

        String tetfu = "v115@2gWpCeWpDe0hQpxhAe4hAe2hJeAgH";

        String command = String.format("setup -p *! --fill i --margin o -t %s --format csv", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains("*!")
                .contains(Messages.foundSolutions(139))
                .contains(Messages.foundSubSolutions(214));

        assertThat(log.getError()).isEmpty();

        CSVStore csvStore = OutputFileHelper.loadSetupCSV();

        assertThat(csvStore.size()).isEqualTo(214);

        assertThat(csvStore.row("fumen", "http://fumen.zui.jp/?v115@6gQ4Feg0BeR4Eeg0ilQ4wwRpAeh0glBtxwRpAezhBt?wwJeAgWHAUtjWCKuqBA"))
                .contains(entry("use", "TSZLJIO"))
                .contains(entry("num-build", "581"));
    }

    @Test
    void case2() throws Exception {
            /*
            comment: <Empty>
            ___OOOOOOO
            ___OOOOOOO
            ____IIIIIO
            II_IIIIIII
            II_IIIIIII
             */

        String tetfu = "v115@2gWpCeWpDe0hQpxhAe4hAe2hJeAgH";

        String command = String.format("setup -p *! --fill i --margin o -t %s --format csv -np 6", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains("*!")
                .contains(Messages.foundSolutions(14))
                .contains(Messages.foundSubSolutions(14));

        assertThat(log.getError()).isEmpty();

        CSVStore csvStore = OutputFileHelper.loadSetupCSV();

        assertThat(csvStore.size()).isEqualTo(14);

        assertThat(csvStore.row("fumen", "http://fumen.zui.jp/?v115@DhwhGeAtg0whglQ4AeRpAeBtg0whglR4RpAeAth0wh?hlQ4JeAgWGAzSNPCaHBAA"))
                .contains(entry("use", "SLIJZO"))
                .contains(entry("num-build", "336"));
    }
}