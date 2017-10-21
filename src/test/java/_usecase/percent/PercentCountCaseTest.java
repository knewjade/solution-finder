package _usecase.percent;

import _usecase.Log;
import _usecase.RunnerHelper;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PercentCountCaseTest extends PercentUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void pattern1() throws Exception {
            /*
            comment: <Empty>
            _______XXX
            ________XX
            XX____XXXX
            XX_____XXX
             */

        String tetfu = "m115@EhC8HeD8DeF8EeC8JeAgH";

        String command = String.format("percent -p *p7 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(4374, 5040))
                .contains("*p7");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void pattern2() throws Exception {
            /*
            comment: <Empty>
            X____X____
            XX__XX____
            XX__XX____
            XXXXXX____
             */

        String tetfu = "http://harddrop.com/fumen/?v115@9gA8DeA8DeB8BeB8DeB8BeB8DeF8NeAgH";

        String command = String.format("percent -p T,I,O,*p4 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(742, 840))
                .contains("T,I,O,*p4");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void pattern3() throws Exception {
            /*
            comment: <Empty>
            __________
            __________
            _________X
            ___XX__XXX
             */

        String tetfu = "http://harddrop.com/fumen/?d115@ahA8CeB8BeC8JeAgH";

        String command = String.format("percent -c 3 -p *p7 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.clearLine(3))
                .contains(Messages.useHold())
                .contains(Messages.success(2368, 5040))
                .contains("*p7");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void pattern4() throws Exception {
            /*
            comment: <Empty>
            __________
            __________
            X________X
            X______XXX
             */

        String tetfu = "http://harddrop.com/fumen/?m115@RhA8HeB8FeC8JeAgH";

        String command = String.format("percent -c 3 -p *p7 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.clearLine(3))
                .contains(Messages.useHold())
                .contains(Messages.success(5028, 5040))
                .contains("*p7");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void pattern5() throws Exception {
            /*
            comment: <Empty>
            XXXX_____X
            XX______XX
            XXX____XXX
            XXX_____XX
             */

        String tetfu = "http://fumen.zui.jp/?m115@9gzhEewwRpFexwRpglDeBtwwilEeBtJeAgH";

        String command = String.format("percent -p [OSZTLJ]p6 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.clearLine(4))
                .contains(Messages.useHold())
                .contains(Messages.success(702, 720))
                .contains("[OSZTLJ]p6");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void pattern6() throws Exception {
        // ミノを置いてライン消去される場合

            /*
            XXXXX_____
            XXXXXXXXXX
            XXXXXXX___
            XXXXXX____
             */

        String tetfu = "v115@9gE8EeF8DeG8CeF8NexHJ";

        String command = String.format("percent -p *p3 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.clearLine(4))
                .contains(Messages.useHold())
                .contains(Messages.success(54, 210))
                .contains("*p3");

        assertThat(log.getError()).isEmpty();
    }
}
