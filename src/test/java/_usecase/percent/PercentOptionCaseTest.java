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

class PercentOptionCaseTest extends PercentUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void withoutHold1() throws Exception {
        // ホールドなしコマンド

        Field field = FieldFactory.createField("" +
                "XXXXXXXXX_" +
                "XXXXXXXXX_" +
                "__XXXXXXX_" +
                "__XXXXXXX_"
        );

        ConfigFileHelper.createFieldFile(field, 4);

        String command = "percent -p [IO]p2 --hold avoid";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.noUseHold())
                .contains(Messages.success(1, 2))
                .contains("[IO]p2")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(2))
                .contains(Messages.treeHeadSize(2))
                .contains(Messages.tree("*", 50.00))
                .contains(Messages.tree("I", 100.00))
                .contains(Messages.tree("O", 0.0))
                .contains(Messages.failPatternSize(100))
                .contains("[O, I]");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void page() throws Exception {
        // ページの指定

            /*
            comment: 4 -p [JT]!,*p4
            XXX_______
            XXXXX_____
            XXXXXX____
            XXXXXX____
             */
        String tetfu = "v115@IhA8HeB8HeA8SeRPYMAkQnGE5VrGEtIReEJhRpHeRp?ZevrB9gi0Geg0meAAPaA0no2ANI98AwXfzBqeEHBEoA6AFL?/iAQfAAA";

        String command = String.format("percent -t %s -P 3", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(1582, 1680))
                .contains("[JT]!,*p4")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(1680))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("*", 94.17))
                .contains(Messages.failPatternSize(100))
                .contains("[T, J, O, S, T, Z]")
                .contains("[T, J, S, Z, O, T]");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void treeDepth() throws Exception {
        // ツリーの深さを変更

            /*
            comment: 4 -p *!
            XXX_______
            XX_______X
            XX_______X
            XXXXX___XX
             */
        String tetfu = "v115@9gC8GeB8GeC8GeB8GeB8JeRPYNA0no2ANI98AQf78A?RAAAA";

        String command = String.format("percent -t %s -td 4", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(4992, 5040))
                .contains("*!")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(5040))
                .contains(Messages.treeHeadSize(4))
                .contains(Messages.tree("*", 99.05))
                .contains(Messages.tree("S", 98.33))
                .contains(Messages.tree("ZT", 98.33))
                .contains(Messages.tree("TSJL", 66.67))
                .contains(Messages.failPatternSize(100))
                .contains("[Z, T, S, L, O, J, I]")
                .contains("[T, L, J, S, Z, I, O]");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void failedCount() throws Exception {
        // ページの指定

            /*
            comment: 4 -p *!
            XXX_______
            XX________
            XX______XX
            XXXXX___XX
             */
        String tetfu = "v115@9gC8GeB8HeB8FeC8GeB8JeRPYNA0no2ANI98AQf78A?RAAAA";

        String command = String.format("percent -t %s -fc -1", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(4716, 5040))
                .contains("*!")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(5040))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("*", 93.57))
                .contains(Messages.failPatternAllSize())
                .contains("[J, L, S, Z, O, I, T]")
                .contains("[S, I, J, L, Z, T, O]")
                .contains("[L, S, O, Z, J, I, T]")
                .contains("[J, T, L, S, Z, I, O]")
                .contains("[S, L, I, O, Z, J, T]")
                .contains("[L, I, S, O, Z, J, T]")
                .contains("[S, T, Z, I, L, J, O]")
                .contains("[T, S, Z, I, L, J, O]")
                .contains("[J, I, S, Z, L, T, O]")
                .contains("[S, J, L, O, I, Z, T]");

        assertThat(log.getError()).isEmpty();
    }
}
