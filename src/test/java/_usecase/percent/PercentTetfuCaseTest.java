package _usecase.percent;

import _usecase.ConfigFileHelper;
import _usecase.Log;
import _usecase.RunnerHelper;
import core.field.Field;
import core.field.FieldFactory;
import core.field.SmallField;
import entry.EntryPointMain;
import module.LongTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PercentTetfuCaseTest extends PercentUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void useTetfuOnly1() throws Exception {
        // テト譜 + パターンコメント

            /*
            comment: 4 -p T,*p7
            __________
            ___X______
            XXXXXX____
            XXXXX_____
             */
        String tetfu = "v115@KhA8FeF8DeE8OeAgWQA0no2ANI98AQe88AjPcQB";

        String command = String.format("percent -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(5040, 5040))
                .contains("T,*p7")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(5040))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("T", 100.0))
                .contains(Messages.tree("TI", 100.0))
                .contains(Messages.tree("TSL", 100.0))
                .contains(Messages.failPatternSize(100))
                .contains(Messages.failNothing());

        assertThat(log.getError()).isEmpty();
    }

    @Test
    @LongTest
    void useTetfuAndCommand1() throws Exception {
        // テト譜 + パターンコマンド

           /*
            comment: 4
            __________
            __________
            __________
            __________
             */
        String tetfu = "v115@vhAAgWBAUAAAA";

        String command = String.format("percent -t %s -p %s", tetfu, "S,[TZ]p2,*p7");
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(10080, 10080))
                .contains("S,[TZ]p2,*p7")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(10080))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("S", 100.0))
                .contains(Messages.tree("ST", 100.0))
                .contains(Messages.tree("SZ", 100.0))
                .doesNotContain(Messages.tree("*"))
                .doesNotContain(Messages.tree("I"))
                .doesNotContain(Messages.tree("TO"))
                .contains(Messages.failPatternSize(100))
                .contains(Messages.failNothing());

        assertThat(log.getError()).isEmpty();
    }

    @Test
    @LongTest
    void useTetfuAndPatternsFile1() throws Exception {
        // テト譜 + パターンファイル

           /*
            comment: 4
            __________
            __________
            _XX_______
            XX________
             */
        String tetfu = "v115@ShB8GeB8ReAgWBAUAAAA";

        ConfigFileHelper.createPatternFile("[TZ]p2,*p7");

        String command = String.format("percent -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(10080, 10080))
                .contains("[TZ]p2,*p7")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(10080))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("*", 100.0))
                .contains(Messages.tree("T", 100.0))
                .contains(Messages.tree("ZT", 100.0))
                .contains(Messages.failPatternSize(100))
                .contains(Messages.failNothing());

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void useTetfuAndCommand2() throws Exception {
        // テト譜 + パターンコマンド (フィールドファイル・パターンファイル無視)

            /*
            comment: 4
            X_________
            XXXX______
            XXXXXX_____
            XXXXXXX___
             */

        String tetfu = "d115@9gA8IeD8FeE8EeF8NeAgWBAUAAAA";

        Field field = FieldFactory.createField("" +
                "X___XXX___" +
                "XX_X_XXX__" +
                "X__XX_XX__" +
                "XXX_X_X_X_"
        );

        ConfigFileHelper.createFieldFile(field, 4);
        ConfigFileHelper.createPatternFile("*p4");

        String command = String.format("percent -p *p7 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(4636, 5040))
                .contains("*p7")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(5040))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("*", 91.98))
                .contains(Messages.tree("T", 100.00))
                .contains(Messages.tree("OI", 98.33))
                .contains(Messages.tree("ILZ", 87.50))
                .contains(Messages.failPatternSize(100))
                .doesNotContain(Messages.failNothing());

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void useTetfuAndCommand3() throws Exception {
        // テト譜 (無関係なコメント付き) + パターンコマンド (パターンファイルを無視)

            /*
            comment: 1ページ目: 無関係なコメントです
            XX________
            XXX______X
            XXX_____XX
            XXXX_____X
             */

        String tetfu = "v115@9gB8HeC8FeD8EeF8EeA8JeAgWbBxXHDBQGDSA1d0AC?DYHDBQzuRA1Dq9BF4CwBFbcRA1zW9AxXXXB1RhRAV/d3ByX?HDBQxCSA1dUzBzXHDBwHfRA1d0ACzXHDBw0uRA1d0KB3XHD?Bwv4AA";

        ConfigFileHelper.createFieldFile(new SmallField(), 4);
        ConfigFileHelper.createPatternFile("*p7");

        String command = String.format("percent -c 4 -p *p7 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(5008, 5040))
                .contains("*p7")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(5040))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("*", 99.37))
                .contains(Messages.tree("S", 98.33))
                .contains(Messages.tree("OS", 95.83))
                .contains(Messages.tree("ZOS", 83.33))
                .contains(Messages.failPatternSize(100))
                .doesNotContain(Messages.failNothing());

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void useTetfuAndCommand4() throws Exception {
        // テト譜 (無関係なコメント付き) + パターンコマンド (パターンファイルを無視)

            /*
            comment: 日本語開始のコメント
            XXXXXX____
            XXXXXX____
            XXXXXX_X__
            XXXXXXXXX_
             */

        String tetfu = "v115@9gF8DeF8DeF8AeA8BeI8KeAgW8AlfrHBFwDfE2Cx2B?l/PwB5HEfE5fmzBlPJVBjDEfET4p9Blvs2ACtDfETor6Alv?s2AGtDfETIPSB";

        ConfigFileHelper.createFieldFile(new SmallField(), 4);
        ConfigFileHelper.createPatternFile("*p7");

        String command = String.format("percent -c 4 -p *p4 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.softdrop())
                .contains(Messages.success(312, 840))
                .contains("*p4")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(840))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("*", 37.14))
                .contains(Messages.tree("I", 40.00))
                .contains(Messages.tree("OS", 5.0))
                .contains(Messages.tree("IOS", 0.0))
                .contains(Messages.failPatternSize(100))
                .doesNotContain(Messages.failNothing());

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void useTetfuAndCommand5() throws Exception {
        // テト譜 + パターンコマンド

           /*
            comment:<Empty>
            XXXXXX____
            XXXXXX____
            XXXXXX____
            XXXXXX____
             */

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";

        String command = String.format("percent -c 4 -p *p4 -d hard -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.harddrop())
                .contains(Messages.success(314, 840))
                .contains("*p4")
                .contains(Messages.clearLine(4))
                .contains(Messages.tree("*", 37.38))
                .contains(Messages.tree("L", 47.50))
                .contains(Messages.tree("ST", 45.00))
                .contains(Messages.tree("IOL", 50.00));

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void useTetfuOnly2() throws Exception {
        // テト譜 (ホールドnoにする) + パターンコマンド (フィールドファイル・パターンファイル無視)

            /*
            comment: 4 -p *p2 -H no
            _______X__
            ______XX__
            ____XXXXXX
            ___XXXXXXX
             */

        String tetfu = "m115@EhA8HeB8FeF8CeG8JeAgWWA0no2ANI98AQPk/AFbcs?AIoo2Au3BAA";

        ConfigFileHelper.createFieldFile(FieldFactory.createField(""), 4);
        ConfigFileHelper.createPatternFile("*p2");

        String command = String.format("percent -p *p6 -t %s -fc 5", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.noUseHold())
                .contains(Messages.success(744, 5040))
                .contains("*p6")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(5040))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("*", 14.76))
                .contains(Messages.tree("T", 16.25))
                .contains(Messages.tree("IL", 13.33))
                .contains(Messages.tree("JOZ", 0.0))
                .contains(Messages.failPatternSize(5))
                .doesNotContain(Messages.failNothing());

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void colorTetfu() throws Exception {
        // テト譜 + パターンコマンド (フィールドファイル・パターンファイル無視)

            /*
            comment: <Empty>
            I_________
            I___Z_____
            IOOZZSS___
            IOOZSS____
             */

        String tetfu = "http://fumen.zui.jp/?d115@9gwhIewhIewhRpBeR4CewhRpAeR4NesKJ";

        String command = String.format("percent -p *p7 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(4736, 5040))
                .contains("*p7")
                .contains(Messages.clearLine(4))
                .contains(Messages.patternSize(5040))
                .contains(Messages.treeHeadSize(3))
                .contains(Messages.tree("*", 93.97))
                .contains(Messages.tree("T", 100.00))
                .contains(Messages.tree("SL", 85.83))
                .contains(Messages.tree("JLZ", 91.67))
                .contains(Messages.failPatternSize(100))
                .doesNotContain(Messages.failNothing());

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void quizTetfu() throws Exception {
        // テト譜 + Quizパターンコマンド (フィールドファイル・パターンファイル無視)

            /*
            comment: #Q=[](I)LOSZTJ
            __________
            L__S______
            LZZSS___OO
            LLZZS___OO
             */

        String tetfu = "http://fumen.zui.jp/?v115@HhglBeQ4FeglBtR4CeRphlBtQ4CeRpJeAgWaAFLDmC?lcJSAVDEHBEooRBJoAVBM3jFD0/AAA";

        String command = String.format("percent -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(1, 1))
                .contains("ILOSZTJ");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void quizTetfuWithPatterns() throws Exception {
        // テト譜 + Quizパターンコマンド + オプション (フィールドファイル・パターンファイル無視)

            /*
            comment: #Q=[](I)LOSZTJ
            __________
            L__S______
            LZZSS___OO
            LLZZS___OO
             */

        String tetfu = "http://fumen.zui.jp/?v115@HhglBeQ4FeglBtR4CeRphlBtQ4CeRpJeAgWaAFLDmC?lcJSAVDEHBEooRBJoAVBM3jFD0/AAA";

        String command = String.format("percent -t %s -p #Q=[S](Z)I*JOT", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(0, 7))
                .contains("#Q=[S](Z)I*JOT");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void use180Rotation() throws Exception {
        String tetfu = "v115@GhA8DeA8CeB8DeF8DeF8JeAgH";

        {
            String command = String.format("percent -t %s -p *! --kicks default", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains(Messages.useHold())
                    .contains(Messages.softdrop())
                    .contains(Messages.success(5028, 5040))
                    .contains("*!");

            assertThat(log.getError()).isEmpty();
        }
        {
            String command = String.format("percent -t %s -p *! --kicks +srs", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains(Messages.useHold())
                    .contains(Messages.softdrop())
                    .contains(Messages.success(5028, 5040))
                    .contains("*!");

            assertThat(log.getError()).isEmpty();
        }
        {
            String command = String.format("percent -t %s -p *! --drop 180 --kicks @nullpomino180", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains(Messages.useHold())
                    .contains(Messages.softdrop180())
                    .contains(Messages.success(5040, 5040))
                    .contains("*!");

            assertThat(log.getError()).isEmpty();
        }
    }

    @Test
    void test1Line() throws Exception {
        String tetfu = "v115@bhE8DeA8JeAgH";

        String command = String.format("percent -t %s -p I -c 1", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.success(1, 1));

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void noKicks() throws Exception {
        String tetfu = "v115@9gE8DeG8CeH8BeG8CeA8JeAgH";

        {
            String command = String.format("percent -t %s -p *p4", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains(Messages.success(514, 840));

            assertThat(log.getError()).isEmpty();
        }
        {
            String command = String.format("percent -t %s -p *p4 --kicks @nokicks", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains(Messages.success(346, 840));

            assertThat(log.getError()).isEmpty();
        }
    }
}
