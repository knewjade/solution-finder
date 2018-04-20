package _usecase.setup;

import _usecase.Log;
import _usecase.RunnerHelper;
import entry.EntryPointMain;
import module.LongTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SetupTetfuCaseTest extends SetupUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    @LongTest
    void case1() throws Exception {
        // Iがふたをする形
        String tetfu = "v115@zgcpwhVpyhCe3hAe0hZpJeAgH";

        String command = String.format("setup -p [^T]! --fill i --margin o -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput()).contains(Messages.foundSolutions(24));

        assertThat(log.getError()).isEmpty();
    }

    @Test
    @LongTest
    void case2() throws Exception {
        // Iがふたをする形
        String tetfu = "v115@zgdpwhUpxhCe3hAe1hZpJeAgH";

        String command = String.format("setup -p [^T]! --fill i --margin o -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput()).contains(Messages.foundSolutions(14));

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void case3() throws Exception {
        // case4の対称系
        String tetfu = "v115@zgTpwhYpAeUpzhAe3hQpAeQpzhTpAeUpJeAgH";

        String command = String.format("setup -p [^T]! --fill i --margin o -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput()).contains(Messages.foundSolutions(7));

        assertThat(log.getError()).isEmpty();
    }

    @Test
    @LongTest
    void case4() throws Exception {
        // case3の対称系
        String tetfu = "v115@zgUpwhYpAeTp0hAe3hQpAeQpyhUpAeTpJeAgH";

        String command = String.format("setup -p [^T]! --fill i --margin o -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput()).contains(Messages.foundSolutions(7));

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void case5() throws Exception {
        // 高さ4
        String tetfu = "v115@9gTpwhUpxhCe3hAe1hZpJeAgH";

        String command = String.format("setup -p [^T]! --fill i --margin o -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput()).contains(Messages.foundSolutions(11));

        assertThat(log.getError()).isEmpty();
    }
}
