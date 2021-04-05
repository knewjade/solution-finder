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
            String command = "util seq -p ZJSTL -distinct no";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(1)
                    .contains("ZJSTL");

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void pattern() throws Exception {
            // パターン
            String command = "util seq -p *p2 -distinct no";
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
            String command = "util seq -p *p2 *p2 -distinct no";
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
            String command = "util seq -p *p2;*p2 -distinct no";
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
        void length1() throws Exception {
            // サイズ1でカットする
            String command = "util seq -p *p2 *p2 -l 1";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(7)
                    .contains("I", "T", "O", "S", "Z", "L", "J");

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void overLength() throws Exception {
            // lengthが大きい
            String command = "util seq -p *! -l 10";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(5040);

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void S2() throws Exception {
            String command = "util seq -p *p3,*p3 -n S=2";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(8100);  // 90 * 90

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void ST2() throws Exception {
            String command = "util seq -p *p3,*p3 -n S=2 T=2";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(900);  // 30 * 30

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void ST2comma() throws Exception {
            String command = "util seq -p *p3,*p3 -n S=2,T==2";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(900);  // 30 * 30

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void notEqualTo0() throws Exception {
            String command = "util seq -p *p3,*p3 -n O!=1";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(14400 + 8100);

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void lessThan1() throws Exception {
            {
                String command = "util seq -p *p3 -n Z<1";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(120);  // 6 * 5 * 4

                assertThat(log.getError()).isEmpty();
            }
            {
                String command = "util seq -p *p3 -n 1>Z";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(120);  // 6 * 5 * 4

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void lessThanOrEqualTo1() throws Exception {
            {
                String command = "util seq -p *p3,*p3 -n Z<=1";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(36000);  // 7*6*5 * 7*6*5 - 6*5*3 * 6*5*3

                assertThat(log.getError()).isEmpty();
            }
            {
                String command = "util seq -p *p3,*p3 -n 1>=Z";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(36000);  // 7*6*5 * 7*6*5 - 6*5*3 * 6*5*3

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void greaterThan1() throws Exception {
            {
                String command = "util seq -p *p3 -n Z>0";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(90);  // 6 * 5 * 3

                assertThat(log.getError()).isEmpty();
            }
            {
                String command = "util seq -p *p3 -n 0<Z";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(90);  // 6 * 5 * 3

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void greaterThanOrEqualTo1() throws Exception {
            {
                String command = "util seq -p *p3,*p3 -n Z>=2";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(8100);  // 7*6*5 * 7*6*5 - 36000

                assertThat(log.getError()).isEmpty();
            }
            {
                String command = "util seq -p *p3,*p3 -n 2<=Z";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(8100);  // 7*6*5 * 7*6*5 - 36000

                assertThat(log.getError()).isEmpty();
            }
        }
    }

    @Nested
    class InvalidCount {
        @Test
        void negative() throws Exception {
            String command = "util seq -p *p3,*p3 -n Z=-1";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getReturnCode()).isNotEqualTo(0);
            assertThat(log.getError()).contains("Negative value is unsupported");
        }

        @Test
        void noPiece() throws Exception {
            String command = "util seq -p *p3,*p3 -n 1=1";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getReturnCode()).isNotEqualTo(0);
            assertThat(log.getError()).contains("Invalid operand");
        }

        @Test
        void duplicatePiece() throws Exception {
            String command = "util seq -p *p3,*p3 -n Z=Z";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getReturnCode()).isNotEqualTo(0);
            assertThat(log.getError()).contains("Invalid operand");
        }

        @Test
        void noLeft() throws Exception {
            String command = "util seq -p *p3,*p3 -n =1";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getReturnCode()).isNotEqualTo(0);
            assertThat(log.getError()).contains("Left operand is blank");
        }

        @Test
        void noRight() throws Exception {
            String command = "util seq -p *p3,*p3 -n T=";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getReturnCode()).isNotEqualTo(0);
            assertThat(log.getError()).contains("Right operand is blank");
        }
    }

    @Nested
    class Expression {
        @Test
        void SZ() throws Exception {
            String command = "util seq -p *! -e SZ";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(720);  // 6!

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void SxZ() throws Exception {
            String command = "util seq -p *! -e S.Z";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(600);  // 6!-5!

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void SxxZ() throws Exception {
            String command = "util seq -p *! -e S..Z";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(480);  // 6!-5!*2

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void StoZ() throws Exception {
            String command = "util seq -p *! -e S.*Z";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(2520);  // 7!/2

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void Sat2() throws Exception {
            String command = "util seq -p *! -e ^.S";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(720);  // 6!

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void SatLast2() throws Exception {
            String command = "util seq -p *! -e S.$";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(720);  // 6!

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void Sat1or2() throws Exception {
            String command = "util seq -p *! -e ^.{0,1}?S";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(1440);

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void notSZ() throws Exception {
            String command = "util seq -p *! -ne SZ";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(5040 - 720);  // 7! - 6!

            assertThat(log.getError()).isEmpty();
        }
    }

    @Nested
    class Forward {
        @Test
        void singleSequence() throws Exception {
            // サイズ4でカットする
            String command = "util seq -p ZJSTL -M forward -d no";
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
        void length5() throws Exception {
            // 固定のシーケンス。同じミノを含まない
            String command = "util seq -p ZJSTL -M forward -l 5 -d no";
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
        void samePiece() throws Exception {
            // 固定のシーケンス。同じミノを含む
            String command = "util seq -p ZZJST -M forward -l 4 -d no";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(16);

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void distinct() throws Exception {
            // distinctを有効にする
            String command = "util seq -p ZZJST -M forward -l 4 -d yes";
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
            String command = "util seq -p ZZJST ZJSTZ -M forward -l 4";
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
            {
                String command = "util seq -p [SLJI]p3 -M forward";
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
            {
                String command = "util seq -p [SLJI]p3 -l 3 -M forward";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(24)
                        .contains(
                                "SJI",
                                "IJS",
                                "SIJ",
                                "ISJ",
                                "JSI",
                                "JIS",
                                "SJL",
                                "LJS",
                                "SLJ",
                                "LSJ",
                                "JSL",
                                "JLS",
                                "IJL",
                                "LJI",
                                "ILJ",
                                "LIJ",
                                "JIL",
                                "JLI",
                                "ISL",
                                "LSI",
                                "ILS",
                                "LIS",
                                "SIL",
                                "SLI"
                        );

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void overCutting() throws Exception {
            // lengthが大きい
            String command = "util seq -p *! -M forward -l 10";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(5040);

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void SZ() throws Exception {
            String command = "util seq -p SZOJT -M forward -l 4 -n S=1 Z=1";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(14)
                    .contains(
                            "ZSJT",
                            "SZJT",
                            "ZOST",
                            "SOZT",
                            "ZSOT",
                            "SZOT",
                            "ZOJS",
                            "SOJZ",
                            "ZSJO",
                            "SZJO",
                            "ZOSJ",
                            "SOZJ",
                            "ZSOJ",
                            "SZOJ"
                    );

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void hold0() throws Exception {
            {
                // holdByHead = yes
                String command = "util seq -p SZOJT -M forward -n hold=0";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(1)
                        .contains(
                                "ZOJT"
                        );

                assertThat(log.getError()).isEmpty();
            }
            {
                // holdByHead = no
                String command = "util seq -p SZOJT -M forward -hh no -n hold=0";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(1)
                        .contains(
                                "SZOJ"
                        );

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void hold0l5() throws Exception {
            {
                // holdByHead = yes
                String command = "util seq -p SZOJT -M forward -l 5 -n hold=0";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput()).isEmpty();

                assertThat(log.getError()).isEmpty();
            }
            {
                // holdByHead = no
                String command = "util seq -p SZOJT -M forward -l 5 -hh no -n hold=0";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(1)
                        .contains(
                                "SZOJT"
                        );

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void hold1() throws Exception {
            {
                // holdByHead = yes
                String command = "util seq -p SZOJT -M forward -n hold=1";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(4)
                        .contains(
                                "SOJT",
                                "ZSJT",
                                "ZOST",
                                "ZOJS"
                        );

                assertThat(log.getError()).isEmpty();
            }
            {
                // holdByHead = no
                String command = "util seq -p SZOJT -M forward -hh no -n hold=1";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(4)
                        .contains(
                                "SOJT",
                                "ZOJT",
                                "SZJT",
                                "SZOT"
                        );

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void hold1l5() throws Exception {
            {
                // holdByHead = yes
                String command = "util seq -p SZOJT -M forward -l 5 -n hold=1";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(1)
                        .contains(
                                "ZOJTS"
                        );

                assertThat(log.getError()).isEmpty();
            }
            {
                // holdByHead = no
                String command = "util seq -p SZOJT -M forward -l 5 -hh no -n hold=1";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput()).isEmpty();

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void hold1l3() throws Exception {
            {
                // holdByHead = yes
                String command = "util seq -p SZOJT -M forward -l 3 -n hold=1";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(3)
                        .contains(
                                "SOJ",
                                "ZSJ",
                                "ZOS"
                        );

                assertThat(log.getError()).isEmpty();
            }
            {
                // holdByHead = no
                String command = "util seq -p SZOJT -M forward -l 3 -hh no -n hold=1";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(3)
                        .contains(
                                "SOJ",
                                "ZOJ",
                                "SZJ"
                        );

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void hold1greaterThan1() throws Exception {
            {
                // holdByHead = yes
                String command = "util seq -p SZOJT -M forward -l 3 -n hold>1";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(4)
                        .contains(
                                "SZJ",
                                "SOZ",
                                "ZSO",
                                "SZO"
                        );

                assertThat(log.getError()).isEmpty();
            }
            {
                // holdByHead = yes
                String command = "util seq -p SZOJT -M forward -l 3 -n 1<hold";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(4)
                        .contains(
                                "SZJ",
                                "SOZ",
                                "ZSO",
                                "SZO"
                        );

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void hold1greaterThanOrEqualTo1() throws Exception {
            {
                // holdByHead = yes
                String command = "util seq -p SZOJT -M forward -l 3 -n hold>=1";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(7)
                        .contains(
                                "SOJ",
                                "ZSJ",
                                "SZJ",
                                "ZOS",
                                "SOZ",
                                "ZSO",
                                "SZO"
                        );

                assertThat(log.getError()).isEmpty();
            }
            {
                // holdByHead = yes
                String command = "util seq -p SZOJT -M forward -l 3 -n 1<=hold";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(7)
                        .contains(
                                "SOJ",
                                "ZSJ",
                                "SZJ",
                                "ZOS",
                                "SOZ",
                                "ZSO",
                                "SZO"
                        );

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void hold1notEqualTo1() throws Exception {
            {
                // holdByHead = yes
                String command = "util seq -p SZOJT -M forward -l 3 -n hold!=1";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(5)
                        .contains(
                                "ZOJ",
                                "SZJ",
                                "SOZ",
                                "ZSO",
                                "SZO"
                        );

                assertThat(log.getError()).isEmpty();
            }
        }
    }

    @Nested
    class Backward {
        @Test
        void singleSequence() throws Exception {
            String command = "util seq -p ZJST -M backward";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(16)
                    .contains(
                            "*ZJST",
                            "Z*JST",
                            "JZ*ST",
                            "ZJ*ST",
                            "SZJ*T",
                            "ZSJ*T",
                            "JZS*T",
                            "ZJS*T",
                            "TZJS*",
                            "ZTJS*",
                            "JZTS*",
                            "ZJTS*",
                            "SZJT*",
                            "ZSJT*",
                            "JZST*",
                            "ZJST*"
                    );

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void l5() throws Exception {
            {
                // holdByHead = yes
                String command = "util seq -p ZJST -M backward -l 5";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(16)
                        .contains(
                                "*ZJST",
                                "Z*JST",
                                "JZ*ST",
                                "ZJ*ST",
                                "SZJ*T",
                                "ZSJ*T",
                                "JZS*T",
                                "ZJS*T",
                                "TZJS*",
                                "ZTJS*",
                                "JZTS*",
                                "ZJTS*",
                                "SZJT*",
                                "ZSJT*",
                                "JZST*",
                                "ZJST*"
                        );

                assertThat(log.getError()).isEmpty();
            }
            {
                // holdByHead = no
                String command = "util seq -p ZJST -M backward -hh no -l 5";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(16)
                        .contains(
                                "*ZJST",
                                "Z*JST",
                                "JZ*ST",
                                "ZJ*ST",
                                "SZJ*T",
                                "ZSJ*T",
                                "JZS*T",
                                "ZJS*T",
                                "TZJS*",
                                "ZTJS*",
                                "JZTS*",
                                "ZJTS*",
                                "SZJT*",
                                "ZSJT*",
                                "JZST*",
                                "ZJST*"
                        );

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void hold1l5() throws Exception {
            {
                // holdByHead = yes
                String command = "util seq -p ZJST -M backward -n hold=1 -l 5";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(4)
                        .contains(
                                "Z*JST",
                                "JZ*ST",
                                "SZJ*T",
                                "TZJS*"
                        );

                assertThat(log.getError()).isEmpty();
            }
            {
                // holdByHead = no
                String command = "util seq -p ZJST -M backward -hh no -n hold=1 -l 5";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(4)
                        .contains(
                                "*ZJST",
                                "Z*JST",
                                "ZJ*ST",
                                "ZJS*T"
                        );

                assertThat(log.getError()).isEmpty();
            }
        }
    }

    @Nested
    class BackwardAndPass {
        @Test
        void l5() throws Exception {
            {
                // holdByHead = yes
                String command = "util seq -p ZJST -M backward-and-pass -l 5";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(97);

                assertThat(log.getError()).isEmpty();
            }
            {
                // holdByHead = no
                String command = "util seq -p ZJST -M backward-pass -hh no -l 5";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(97);

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void hold1l5() throws Exception {
            {
                // holdByHead = yes
                String command = "util seq -p ZJST -M backward-and-pass -n hold=1 -l 5";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(28)
                        .contains(
                                "ZTJST",
                                "ZIJST",
                                "ZLJST",
                                "ZJJST",
                                "ZSJST",
                                "ZZJST",
                                "ZOJST",
                                "JZTST",
                                "JZIST",
                                "JZLST",
                                "JZJST",
                                "JZSST",
                                "JZZST",
                                "JZOST",
                                "SZJTT",
                                "SZJIT",
                                "SZJLT",
                                "SZJJT",
                                "SZJST",
                                "SZJZT",
                                "SZJOT",
                                "TZJST",
                                "TZJSI",
                                "TZJSL",
                                "TZJSJ",
                                "TZJSS",
                                "TZJSZ",
                                "TZJSO"
                        );

                assertThat(log.getError()).isEmpty();
            }
            {
                // holdByHead = no
                String command = "util seq -p ZJST -M backward-pass -hh no -n hold=1 -l 5";
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput().split(LINE_SEPARATOR))
                        .hasSize(25)
                        .contains(
                                "TZJST",
                                "IZJST",
                                "LZJST",
                                "JZJST",
                                "SZJST",
                                "ZZJST",
                                "OZJST",
                                "ZTJST",
                                "ZIJST",
                                "ZLJST",
                                "ZJJST",
                                "ZSJST",
                                "ZOJST",
                                "ZJTST",
                                "ZJIST",
                                "ZJLST",
                                "ZJSST",
                                "ZJZST",
                                "ZJOST",
                                "ZJSTT",
                                "ZJSIT",
                                "ZJSLT",
                                "ZJSJT",
                                "ZJSZT",
                                "ZJSOT"
                        );

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void tiolj() throws Exception {
            String command = "util seq -M backward -p TIOLJ -n HOLD<=1";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(6)
                    .contains(
                            "*TIOLJ",
                            "T*IOLJ",
                            "IT*OLJ",
                            "OTI*LJ",
                            "LTIO*J",
                            "JTIOL*"
                    );

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void TIOLJl5() throws Exception {
            String command = "util seq -M backward -p TIOLJ -l 5 -n HOLD<=1";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(1)
                    .contains(
                            "JTIOL"
                    );

            assertThat(log.getError()).isEmpty();
        }

        @Test
        void TIOLJl5hhNo() throws Exception {
            String command = "util seq -M backward -p TIOLJ -l 5 -hh no -n HOLD<=1";
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().split(LINE_SEPARATOR))
                    .hasSize(1)
                    .contains(
                            "TIOLJ"
                    );

            assertThat(log.getError()).isEmpty();
        }
    }
}