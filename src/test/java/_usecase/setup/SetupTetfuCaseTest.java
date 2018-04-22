package _usecase.setup;

import _usecase.Log;
import _usecase.RunnerHelper;
import _usecase.setup.files.OutputFileHelper;
import _usecase.setup.files.SetupHTML;
import entry.EntryPointMain;
import module.LongTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SetupTetfuCaseTest {
    @Nested
    class CombinationTest extends SetupUseCaseBaseTest {
        private String buildCommand(String fumen, String options) {
            return String.format("setup -t %s --co yes %s", fumen, options);
        }

        @Override
        @BeforeEach
        void setUp() throws IOException {
            super.setUp();
        }

        @Test
        @LongTest
        void case1() throws Exception {
            // Iがふたをする形
            String fumen = "v115@zgcpwhVpyhCe3hAe0hZpJeAgH";
            String command = buildCommand(fumen, "-p [^T]! --fill i --margin o");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(24));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getHtml()).contains("2342113323");

            assertThat(html.getFumens())
                    .hasSize(24)
                    .contains("+gzhCeQ4AeilCeBtR4gli0AeRpBtQ4Ceg0AeRpMeAg?WGAzHDMCqCBAA");
        }

        @Test
        @LongTest
        void case2() throws Exception {
            // Iがふたをする形
            String fumen = "v115@zgdpwhUpxhCe3hAe1hZpJeAgH";
            String command = buildCommand(fumen, "-p [^T]! --fill i --margin o");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(14));
            assertThat(log.getError()).isEmpty();
        }

        @Test
        void case3() throws Exception {
            // case4の対称系
            String fumen = "v115@zgTpwhYpAeUpzhAe3hQpAeQpzhTpAeUpJeAgH";
            String command = buildCommand(fumen, "-p [^T]! --fill i --margin o");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(7));
            assertThat(log.getError()).isEmpty();
        }

        @Test
        @LongTest
        void case4() throws Exception {
            // case3の対称系
            String fumen = "v115@zgUpwhYpAeTp0hAe3hQpAeQpyhUpAeTpJeAgH";
            String command = buildCommand(fumen, "-p [^T]! --fill i --margin o");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(7));
            assertThat(log.getError()).isEmpty();
        }

        @Test
        void case5() throws Exception {
            // 高さ4
            String fumen = "v115@9gTpwhUpxhCe3hAe1hZpJeAgH";
            String command = buildCommand(fumen, "-p [^T]! --fill i --margin o");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(11));
            assertThat(log.getError()).isEmpty();
        }

        @Test
        void case6() throws Exception {
            // 高さ3
            String fumen = "v115@HhUpxhBeA81hCeA8yhD8AeB8JeAgH";
            String command = buildCommand(fumen, "-p *! --fill i --margin o");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(13));
            assertThat(log.getError()).isEmpty();
        }

        @Test
        void case7() throws Exception {
            // Large T
            // with pages, combination
            String fumen = "v115@vhKAgHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApgY4Ae?Y4AeY4DeS4GeS4GeS4NeAAA";
            String command = buildCommand(fumen, "-p *!,*! --fill s --page 12");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(274));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getHtml()).contains("3336663330");

            assertThat(html.getFumens())
                    .hasSize(274)
                    .contains("pgwwzhBthlAexwR4h0BtglAewwR4whg0jlDewhg0gl?GewhRpGewhRpNeAgWJAMnzPCMuLMC0AAAA");
        }

        @Test
        void case8() throws Exception {
            // Harddrop is impossible
            String fumen = "v115@zg0hEewhj0Eewhj0Eewhj0EewhzwOeAgH";
            String command = buildCommand(fumen, "-p *! -f t -m j --drop harddrop");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(0));
            assertThat(log.getError()).isEmpty();
        }

        @Test
        void case9() throws Exception {
            // If `--line` is enable, Harddrop is possible
            String fumen = "v115@zg0hEewhj0Eewhj0Eewhj0EewhzwOeAgH";
            String command = buildCommand(fumen, "-p *! -f t -m j --drop harddrop -l 4");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(38));
            assertThat(log.getError()).isEmpty();
        }

        @Test
        void case10() throws Exception {
            // Harddrop is less than softdrop
            // with dummy comment
            String fumen = "v115@HhHthlBtA8CtA8EtglCtglCtJeAgWQAooMDEvoo2A3?XaDEkoA6A";

            // Harddrop
            {
                String command = buildCommand(fumen, "-p *!,*! -f Z -m L --drop harddrop -l 4");
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                // Log
                assertThat(log.getOutput()).contains(Messages.foundSolutions(6));
                assertThat(log.getError()).isEmpty();
            }

            // Softdrop
            {
                String command = buildCommand(fumen, "-p *!,*! -f Z -m L --drop softdrop -l 4");
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput()).contains(Messages.foundSolutions(168));

                assertThat(log.getError()).isEmpty();
            }
        }

        @Test
        void case11() throws Exception {
            // フィールドの大部分が埋まっている
            String fumen = "v115@Bg0hEe0hEe0hEe0hEe78JeAgH";
            String command = buildCommand(fumen, "-p *! -f i");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(8));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getHtml()).contains("+++++66666");

            assertThat(html.getFumens())
                    .hasSize(8)
                    .contains("Bgg0zhEei0R4EeRpR4glEeRpilEe78JeAgWFAzyaPC?pAAAA");
        }

        @Test
        void case12() throws Exception {
            // 4x4
            String fumen = "v115@9gTpFeTpFeTpFezhPeAgH";
            String command = buildCommand(fumen, "-p *! -f i -m o");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(38));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getHtml())
                    .contains("3221000000")
                    .contains("2123000000");

            assertThat(html.getFumens())
                    .hasSize(38)
                    .contains("Hhh0Heg0AewwGeg0ywPeAgWCAqOBAA")
                    .contains("JhhlFeg0BeglFei0glPeAgWCAs/AAA");
        }

        @Test
        void case12WithoutHoles() throws Exception {
            // 4x4
            String fumen = "v115@9gTpFeTpFeTpFezhPeAgH";
            String command = buildCommand(fumen, "-p *! -f i -m o --exclude holes --drop hard");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(34));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getHtml())
                    .contains("3221000000")
                    .contains("2123000000");

            assertThat(html.getFumens())
                    .hasSize(34)
                    .doesNotContain("Hhh0Heg0AewwGeg0ywPeAgWCAqOBAA")
                    .doesNotContain("JhhlFeg0BeglFei0glPeAgWCAs/AAA");
        }

        @Test
        void case13() throws Exception {
            // 空中TSD  // アルバトロス
            String fumen = "v115@9gQpBewhVpwhCe3hAe2hZpJeAgH";
            String command = buildCommand(fumen, "-p [^T]! -f i -m o");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(12));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getHtml())
                    .contains("2203432224");

            assertThat(html.getFumens())
                    .hasSize(12)
                    .contains("AhBtDewhQ4CeBti0whR4AeRpilg0whAeQ4AeRpglCe?whJeAgWGApvaFDMNBAA");
        }

        @Test
        void case13WithoutHoles() throws Exception {
            // 空中TSD  // アルバトロス
            // ホールを除外する
            String fumen = "v115@9gQpBewhVpwhCe3hAe2hZpJeAgH";
            String command = buildCommand(fumen, "-p [^T]! -f i -m o --exclude holes");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(0));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getFumens()).hasSize(0);
        }

        @Test
        void case13WithoutHolesAfterOperation() throws Exception {
            // 空中TSD  // アルバトロス
            // 操作した後、ホールを除外する
            String fumen = "v115@9gQpBewhVpwhCe3hAe2hZpJeAgH";
            String command = buildCommand(fumen, "-p [^T]! -f i -m o --operate T-Reverse(2,2) --exclude holes");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(2));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getFumens())
                    .hasSize(2)
                    .contains("AhBtDewhQ4CeBti0whR4AeRpilg0whAeQ4AeRpglCe?whJeAgWGApvaFDMNBAA")
                    .contains("AhBtCeglAeQ4CeBtilg0R4AeRpzhg0AeQ4AeRpCeh0?JeAgWGAqyaFDJNBAA");
        }

        @Test
        void case14() throws Exception {
            // 空中TSS
            String fumen = "v115@2gQpFeSpwhBeWpCeTpzhAe0hA8RpB8UpJeAgl";
            String command = buildCommand(fumen, "-p [^T]! -f i -m o");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(202));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getFumens())
                    .hasSize(202)
                    .contains("2gAtHeBtEewhRpAtDeglg0whRpR4Aeilg0whA8R4B8?Beh0whJeAgWGAp/TFDTHBAA");
        }

        @Test
        void case14WithoutHolesAfterOperation() throws Exception {
            // 空中TSS
            // 操作した後、ホールを除外する
            String fumen = "v115@2gQpFeSpwhBeWpCeTpzhAe0hA8RpB8UpJeAgl";
            String command = buildCommand(fumen, "-p [^T]! -f i -m o -op T-2(4,2) -e holes");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(7));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getFumens())
                    .hasSize(7)
                    .doesNotContain("2gAtFeglAeBtFeglAeAtFeg0hlR4Aezhg0A8R4B8Ce?h0JeAgWFAKeLuCsAAAA")
                    .doesNotContain("2gAtHeBtEewhRpAtDeglg0whRpR4Aeilg0whA8R4B8?Beh0whJeAgWGAp/TFDTHBAA")
                    .doesNotContain("2gAtFeglAeBtFeglAeAtGehlR4Aei0RpA8R4B8Beg0?RpJeAgWFAvfLuCsAAAA");
        }

        @Test
        void case14WithoutHolesAfterOperationAndAssumeFilled() throws Exception {
            // 空中TSS
            // 操作した後、ラインが揃ったとみなして、ホールを除外する
            String fumen = "v115@2gQpFeSpwhBeWpCeTpzhAe0hA8RpB8UpJeAgl";
            String command = buildCommand(fumen, "-p [^T]! -f i -m o -op T-2(4,2) clear() row(1) -e holes");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(39));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getFumens())
                    .hasSize(39)
                    .contains("2gAtFeglAeBtFeglAeAtFeg0hlR4Aezhg0A8R4B8Ce?h0JeAgWFAKeLuCsAAAA")
                    .contains("2gAtHeBtEewhRpAtDeglg0whRpR4Aeilg0whA8R4B8?Beh0whJeAgWGAp/TFDTHBAA");
        }

        @Test
        void case14WithoutHolesAfterOperationAndSetBlock() throws Exception {
            // 空中TSS
            // ミノを操作した後、1ブロック追加して、ホールを除外する
            String fumen = "v115@2gQpFeSpwhBeWpCeTpzhAe0hA8RpB8UpJeAgl";
            String command = buildCommand(fumen, "-p [^T]! -f i -m o -op T-2(4,2) block(6,2) -e holes");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(7));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getFumens())
                    .hasSize(7)
                    .doesNotContain("2gAtFeglAeBtFeglAeAtFeg0hlR4Aezhg0A8R4B8Ce?h0JeAgWFAKeLuCsAAAA")
                    .contains("2gAtHeBtEewhRpAtDeglg0whRpR4Aeilg0whA8R4B8?Beh0whJeAgWGAp/TFDTHBAA");
        }

        @Test
        void case14WithoutStrictHolesAfterOperationAndSetBlock() throws Exception {
            // 空中TSS
            // ミノを操作した後、厳密なホールを除外する
            String fumen = "v115@2gQpFeSpwhBeWpCeTpzhAe0hA8RpB8UpJeAgl";
            String command = buildCommand(fumen, "-p [^T]! -f i -m o -op T-2(4,2) -e strict-holes");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(33));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getFumens())
                    .hasSize(33)
                    .doesNotContain("2gAtHeBtDeglCeAtCeR4glAezhAeR4g0hlA8BeB8Be?i0JeAgWFAsvjFDpAAAA")
                    .contains("2gAtFeglAeBtFeglAeAtFeg0hlR4Aezhg0A8R4B8Ce?h0JeAgWFAKeLuCsAAAA")
                    .contains("2gAtHeBtEewhRpAtDeglg0whRpR4Aeilg0whA8R4B8?Beh0whJeAgWGAp/TFDTHBAA")
                    .contains("2gAtFeglAeBtFeglAeAtGehlR4Aei0RpA8R4B8Beg0?RpJeAgWFAvfLuCsAAAA")
                    .contains("2gAtHeBtDeg0CeAtEeg0AezhAeRph0glA8BeB8Rpil?JeAgWFAsvaFDpAAAA");
        }

        @Test
        void case15() throws Exception {
            // 4ミノ固定  // 空間がぴったり
            String fumen = "v115@9gTpFeTpFeTpFezhPeAgl";
            String command = buildCommand(fumen, "-p *! -f i -m o --n-pieces 4");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput())
                    .contains(Messages.foundSolutions(15))
                    .contains(Messages.foundSubSolutions(24));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getHtml())
                    .contains("1111000000")
                    .doesNotContain("4444000000");

            assertThat(html.getFumens())
                    .hasSize(24);
        }

        @Test
        void case16() throws Exception {
            // 4ミノ固定  // 空間が広め
            String fumen = "v115@9gwhVpCewhVpCewhVpCewhVpMeAgl";
            String command = buildCommand(fumen, "-p *! -f i -m o --n-pieces 4");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));
            System.out.println(log.getOutput());

            // Log
            assertThat(log.getOutput())
                    .contains(Messages.foundSolutions(1828))
                    .contains(Messages.foundSubSolutions(6888));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getHtml())
                    .contains("4444000000");

            assertThat(html.getFumens())
                    .hasSize(6888);
        }
    }

    @Nested
    class OrderTest extends SetupUseCaseBaseTest {
        private String buildCommand(String fumen, String options) {
            return String.format("setup -t %s %s", fumen, options);
        }

        @Override
        @BeforeEach
        void setUp() throws IOException {
            super.setUp();
        }

        @Test
        void case1WithHold() throws Exception {
            // 4x4
            String fumen = "v115@9gzhFezhFezhFezhPeAgH";
            String command = buildCommand(fumen, "-p IOOI --fill i --hold yes");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));
            System.out.println(log.getOutput());

            // Log
            assertThat(log.getOutput())
                    .contains(Messages.foundSolutions(5))
                    .contains(Messages.foundSolutions(5));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getHtml()).contains("4444000000");

            assertThat(html.getFumens())
                    .hasSize(5)
                    .contains("9gzhFezhFeTpFeTpPeAgWEAPX1LC")
                    .contains("9gxhRpFexhRpFexhRpFexhRpPeAgWEAPX1LC")
                    .contains("9gzhFeTpFeTpFezhPeAgWEAPX1LC")
                    .contains("9gwhRpwhFewhRpwhFewhRpwhFewhRpwhPeAgWEAJ3C?MC")
                    .contains("9gRpxhFeRpxhFeRpxhFeRpxhPeAgWEAJ3CMC");
        }

        @Test
        void case1WithoutHold() throws Exception {
            // 4x4
            String fumen = "v115@9gzhFezhFezhFezhPeAgH";
            String command = buildCommand(fumen, "-p IOOI --fill i --hold no");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput())
                    .contains(Messages.foundSolutions(4))
                    .contains(Messages.foundSolutions(4));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getHtml()).contains("4444000000");

            assertThat(html.getFumens())
                    .hasSize(4)
                    .doesNotContain("9gzhFezhFeTpFeTpPeAgWEAPX1LC")
                    .contains("9gxhRpFexhRpFexhRpFexhRpPeAgWEAPX1LC")
                    .contains("9gzhFeTpFeTpFezhPeAgWEAPX1LC")
                    .contains("9gwhRpwhFewhRpwhFewhRpwhFewhRpwhPeAgWEAJ3C?MC")
                    .contains("9gRpxhFeRpxhFeRpxhFeRpxhPeAgWEAJ3CMC");
        }

        @Test
        void case1WithoutHoldN3() throws Exception {
            // 4x4
            String fumen = "v115@9gAtywFeAtywFeAtywFeAtywPeAgH";
            String command = buildCommand(fumen, "-p JSOI,*! -f Z -m t --hold no -np 3");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput())
                    .contains(Messages.foundSolutions(2))
                    .contains(Messages.foundSubSolutions(3));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getHtml()).contains("4310000000");

            assertThat(html.getFumens())
                    .hasSize(3)
                    .contains("9gRpHeRpQ4Geg0AeR4Fei0Q4PeAgWDAvvzBA")
                    .contains("9gQ4IeR4RpFeg0Q4RpFei0QeAgWDAzvqBA")
                    .contains("9gRpHeRpR4Feg0R4Gei0QeAgWDAvvzBA");
        }

        @Test
        void case1WithoutHoldN3ExcludeStrictHoles() throws Exception {
            // 4x4
            String fumen = "v115@9gAtywFeAtywFeAtywFeAtywPeAgH";
            String command = buildCommand(fumen, "-p JSOI,*! -f Z -m t --hold no -np 3 -e strict-holes");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput())
                    .contains(Messages.foundSolutions(2))
                    .contains(Messages.foundSubSolutions(2));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getHtml()).contains("4310000000");

            assertThat(html.getFumens())
                    .hasSize(2)
                    .doesNotContain("9gRpHeRpQ4Geg0AeR4Fei0Q4PeAgWDAvvzBA")
                    .contains("9gQ4IeR4RpFeg0Q4RpFei0QeAgWDAzvqBA")
                    .contains("9gRpHeRpR4Feg0R4Gei0QeAgWDAvvzBA");
        }
    }

    @Nested
    class ErrorTest extends SetupUseCaseBaseTest {
        private String buildCommand(String fumen, String options) {
            return String.format("setup -t %s %s", fumen, options);
        }

        @Override
        @BeforeEach
        void setUp() throws IOException {
            super.setUp();
        }

        @Test
        void case1WithHold() throws Exception {
            // 必要なミノが足りないケース
            String fumen = "v115@9gzhFezhFezhFezhPeAgH";
            String command = buildCommand(fumen, "-p IOT --fill i");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getError()).contains("Should specify equal to or more than 4 pieces");
        }
    }
}