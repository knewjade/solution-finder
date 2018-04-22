package _usecase.setup;

import _usecase.Log;
import _usecase.RunnerHelper;
import _usecase.setup.files.OutputFileHelper;
import _usecase.setup.files.SetupHTML;
import entry.EntryPointMain;
import module.LongTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
            String command = buildCommand(fumen, "-p *! -f i -m o --holes avoid --drop hard");
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
            // 空中Tスピン  // アルバトロス
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
            // 空中Tスピン  // アルバトロス
            // ホールを除外する
            String fumen = "v115@9gQpBewhVpwhCe3hAe2hZpJeAgH";
            String command = buildCommand(fumen, "-p [^T]! -f i -m o --holes avoid");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(0));
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadSetupHTML();
            assertThat(html.getFumens()).hasSize(0);
        }
    }
}