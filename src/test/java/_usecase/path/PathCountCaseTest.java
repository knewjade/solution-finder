package _usecase.path;

import _usecase.Log;
import _usecase.RunnerHelper;
import entry.EntryPointMain;
import module.LongTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PathCountCaseTest extends PathUseCaseBaseTest {
    @Test
    void pattern1WithoutHold() throws Exception {
            /*
            XXXXXX____
            XXXXXXX___
            XXXXXXXX__
            XXXXXXX___
             */

        String tetfu = "v115@9gF8DeG8CeH8BeG8MeAgH";

        String command = String.format("path -c 4 -p T,*p3 -H no -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode())
                .as(log.getError())
                .isEqualTo(0);

        assertThat(log.getOutput())
                .contains("T,*p3")
                .contains(Messages.uniqueCount(9))
                .contains(Messages.minimalCount(8))
                .contains(Messages.noUseHold());
    }

    @Test
    void pattern2WithoutHold() throws Exception {
            /*
            XXXX______
            XXXX______
            XXXX______
            XXXX______
             */

        String tetfu = "m115@9gD8FeD8FeD8FeD8PeAgH";

        String command = String.format("path -c 4 -p *p7 -H no -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode())
                .as(log.getOutput())
                .isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.uniqueCount(245))
                .contains(Messages.minimalCount(199))
                .contains(Messages.noUseHold());
    }

    @Test
    void pattern3WithoutHold() throws Exception {
            /*
            XXX_______
            XXX______X
            XXX_____XX
            XXX______X
             */

        String tetfu = "d115@9gC8GeC8FeD8EeE8FeA8JeAgH";

        String command = String.format("path -c 4 -p *p7 -H no -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.uniqueCount(173))
                .contains(Messages.minimalCount(130))
                .contains(Messages.noUseHold());
    }

    @Test
    void pattern4() throws Exception {
            /*
            XXXXXX____
            XXXXXX____
            XXXXXX____
            XXXXXX____
            XXXXXX____
            XXXXXX____
             */

        String tetfu = "http://fumen.zui.jp/?v115@pgF8DeF8DeF8DeF8DeF8DeF8NeAgH";

        String command = String.format("path -c 6 -p *p7 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.uniqueCount(1002))
                .contains(Messages.minimalCount(573))
                .contains(Messages.useHold());
    }

    @Test
    void pattern5() throws Exception {
            /*
            XXXXXX____
            XXXXXX____
            XXXXXX____
            XXXXXX____
             */

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";

        String command = String.format("path -p *p7 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.uniqueCount(135))
                .contains(Messages.minimalCount(69))
                .contains(Messages.useHold());
    }

    @Test
    @LongTest
    void pattern6() throws Exception {
            /*
            __________
            __________
            __________
            __________
             */

        String tetfu = "v115@vhAAgH";

        String command = String.format("path -p J,Z,O,S,L,I,I,J,S,O,Z -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("J,Z,O,S,L,I,I,J,S,O,Z")
                .contains(Messages.uniqueCount(71))
                .contains(Messages.minimalCount(1))
                .contains(Messages.useHold());
    }

    @Test
    void pattern7() throws Exception {
            /*
            __XXXXXXXX
            __XXXXXXXX
            __XXXXXXXX
            __XXXXXXXX
            __XXXXXXXX
            __XXXXXXXX
            __XXXXXXXX
            __XXXXXXXX
             */

        String tetfu = "v115@XgH8BeH8BeH8BeH8BeH8BeH8BeH8BeH8JeAgH";

        String command = String.format("path -c 8 -p *,*p4 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*,*p4")
                .contains(Messages.uniqueCount(298))
                .contains(Messages.minimalCount(172))
                .contains(Messages.useHold());
    }

    @Test
    void pattern8() throws Exception {
            /*
            X_________
            XXXXXXXX__
            XXXXXXXXX_
            XXXXXX____
             */

        String tetfu = "http://harddrop.com/fumen/?v115@9gA8IeH8BeI8AeF8NeAgH";

        String command = String.format("path -p S,L,O,I,T -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("S,L,O,I,T")
                .contains(Messages.uniqueCount(3))
                .contains(Messages.minimalCount(1))
                .contains(Messages.useHold());
    }

    @Test
    void pattern9() throws Exception {
        // ミノを置いてライン消去される場合

            /*
            XXXXX_____
            XXXXXXXXXX
            XXXXXXX___
            XXXXXX____
             */

        String tetfu = "v115@9gE8EeF8DeG8CeF8NexHJ";

        String command = String.format("path -p *p3 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p3")
                .contains(Messages.uniqueCount(14))
                .contains(Messages.minimalCount(13))
                .contains(Messages.useHold());
    }

    @Test
    void pattern10() throws Exception {
        // ミノを置いてライン消去される場合

            /*
            _______XXX
            ________XX
            ______XXXX
            _______XXX
             */

        String tetfu = "v115@Ehi0HeQ4g0FeBtR4GeBtQ4JeAgH";

        String command = String.format("path -p L,I,T,O,Z,S,I -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("L,I,T,O,Z,S,I")
                .contains(Messages.uniqueCount(2))
                .contains(Messages.minimalCount(1))
                .contains(Messages.useHold());
    }
}
