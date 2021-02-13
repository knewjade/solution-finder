package _usecase.util.seq;

import _usecase.Log;
import _usecase.RunnerHelper;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class UtilSeqTetfuCaseTest extends UtilSeqUseCaseBaseTest {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Nested
    class Pass {
        @Test
        void singleSequence() throws Exception {
            // 固定のシーケンス
            String command = "util seq -p ZJSTL";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(1)
                    .contains("ZJSTL");

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void pattern() throws Exception {
            // パターン
            String command = "util seq -p *p2";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(42) // 7*6
                    .contains("TI")
                    .doesNotContain("TT");

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void duplicatedPattern() throws Exception {
            // 同一パターンが含まれる (スペース区切り)
            String command = "util seq -p *p2 *p2";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(84) // 42 * 2
                    .contains("TI")
                    .doesNotContain("TT");

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void duplicatedPattern2() throws Exception {
            // 同一パターンが含まれる (セミコロン区切り)
            String command = "util seq -p *p2;*p2";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(84) // 42 * 2
                    .contains("TI")
                    .doesNotContain("TT");

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void distinct() throws Exception {
            // distinctを有効にする
            String command = "util seq -p *p2 *p2 -d yes";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(42)
                    .contains("TI")
                    .doesNotContain("TT");

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void cutting1() throws Exception {
            // サイズ1でカットする
            String command = "util seq -p *p2 *p2 -d yes -c 1";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(7)
                    .contains("I", "T", "O", "S", "Z", "L", "J");

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void overCutting() throws Exception {
            // cuttingが大きい
            String command = "util seq -p *! -d yes -c 10";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(5040);

            assertThat(log.getError()).isEmpty();
        }
    }

    @Nested
    class Forward {
        @Test
        void singleSequence() throws Exception {
            // 固定のシーケンス。同じミノを含まない
            String command = "util seq -p ZJSTL -M forward";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(16)
                    .contains(
                            "ZSTLJ",
                            "JSTLZ",
                            "ZJTLS",
                            "JZTLS",
                            "ZSJLT",
                            "JSZLT",
                            "ZJSLT",
                            "JZSLT",
                            "ZSTJL",
                            "JSTZL",
                            "ZJTSL",
                            "JZTSL",
                            "ZSJTL",
                            "JSZTL",
                            "ZJSTL",
                            "JZSTL"
                    );

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void cutting4() throws Exception {
            // サイズ4でカットする
            String command = "util seq -p ZJSTL -M forward -c 4";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(16)
                    .contains(
                            "ZJST",
                            "ZJSL",
                            "ZJTS",
                            "ZJTL",
                            "ZSJT",
                            "ZSJL",
                            "ZSTJ",
                            "ZSTL",
                            "JZST",
                            "JZSL",
                            "JZTS",
                            "JZTL",
                            "JSZT",
                            "JSZL",
                            "JSTZ",
                            "JSTL"
                    );

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void samePiece() throws Exception {
            // 固定のシーケンス。同じミノを含む
            String command = "util seq -p ZZJST -M forward -c 4";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(16);

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void distinct() throws Exception {
            // distinctを有効にする
            String command = "util seq -p ZZJST -M forward -c 4 -d yes";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(8)
                    .contains(
                            "ZJST",
                            "ZZST",
                            "ZJZT",
                            "ZZJT",
                            "ZJSZ",
                            "ZZSJ",
                            "ZJZS",
                            "ZZJS"
                    );

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void twoSequences() throws Exception {
            // 固定のシーケンスが2つ
            String command = "util seq -p ZZJST ZJSTZ -M forward -c 4 -d yes";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(21)
                    .contains(
                            "ZJST",
                            "ZZST",
                            "ZJZT",
                            "ZZJT",
                            "ZJSZ",
                            "ZZSJ",
                            "ZJZS",
                            "ZZJS",
                            "ZSTZ",
                            "JSTZ",
                            "ZJTZ",
                            "JZTZ",
                            "ZSJZ",
                            "JSZZ",
                            "JZSZ",
                            "ZSTJ",
                            "ZJTS",
                            "JZTS",
                            "ZSJT",
                            "JSZT",
                            "JZST"
                    );

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void pattern() throws Exception {
            // 固定のシーケンスが2つ
            String command = "util seq -p [SLJI]p2 -M forward -d yes";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(12)
                    .contains(
                            "LJ",
                            "JL",
                            "LI",
                            "IL",
                            "LS",
                            "SL",
                            "JI",
                            "IJ",
                            "JS",
                            "SJ",
                            "IS",
                            "SI"
                    );

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void overCutting() throws Exception {
            // cuttingが大きい
            String command = "util seq -p *! -M forward -c 10 -d yes";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(5040);

            assertThat(log.getError()).isEmpty();
        }
    }
}