package _usecase.ren;

import _usecase.Log;
import _usecase.RunnerHelper;
import _usecase.ren.files.OutputFileHelper;
import _usecase.ren.files.SetupHTML;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class RenTetfuCaseTest {
    @Nested
    class FumenTest extends RenUseCaseBaseTest {
        @Override
        @BeforeEach
        void setUp() throws IOException {
            super.setUp();
        }

        @Test
        void case1() throws Exception {
            String fumen = "v115@neF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8De?F8DeF8DeF8DeF8DeF8DeF8DeF8DeF8AeL8KeAgH";
            String command = String.format("ren -t %s -p tilsojztilsojz", fumen);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput())
                    .contains(Messages.foundSolutions(410))
                    .contains(Messages.maxRen(13))
                    .contains("TILSOJZTILSOJZ");
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadRenHTML();
            assertThat(html.getHtml())
                    .contains("13 Ren")
                    .doesNotContain("14 Ren");

            assertThat(html.getFumens())
                    .hasSize(410)
                    .contains("neF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8De?F8DeF8DeF8DeF8DeF8DeF8DeF8DeF8AeL8KeZCYhAFLDmCl?cJSAVDEHBEooRBJoAVB0yjPCvubMCTnPFDsAAAAvhMVtB6t?B/nBWoBToBNnBUoBxnB/oBTnBGoBMnBKoB");
        }

        @Test
        void case2() throws Exception {
            String fumen = "v115@DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8De?F8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8A?eI8JeAgH";
            String command = String.format("ren -t %s -p tilsojztilsojztilsojztilsojz", fumen);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput())
                    .contains(Messages.foundSolutions(263))
                    .contains(Messages.maxRen(22))
                    .contains("TILSOJZTILSOJZ");
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadRenHTML();
            assertThat(html.getHtml())
                    .contains("22 Ren");

            assertThat(html.getFumens())
                    .hasSize(263)
                    .contains("DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8De?F8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8A?eI8JeZEYqAFLDmClcJSAVDEHBEooRBJoAVBMNmPCaX9VCz+?aPCa+DxCPt/wCpCBAAvhVSvBXqBlpBGqBMpBTqBZkBSvBXq?B9pBTqBOpBUlBxkBSvBNpBTqB/pB+qBlpBxpBiqB");
        }

        @Test
        void quizTetfu() throws Exception {
            /*
            comment: #Q=[T](I)SZJLOSLT
             */

            String fumen = "v115@FfH8AeI8AeH8AeI8AeD8AeI8AeI8AeO8AeI8AeI8Ae?D8AeN8AeI8AeC8AeM8AeH8AeD8JeAgWdAFLDmClcJSAVztS?AVG88A4N88A5szPCM3TWC0AAAA";
            String command = String.format("ren -t %s", fumen);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput())
                    .contains(Messages.foundSolutions(5))
                    .contains(Messages.maxRen(1))
                    .contains("TISZJLOSLT");
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadRenHTML();
            assertThat(html.getHtml())
                    .contains("1 Ren")
                    .doesNotContain("2 Ren");

            assertThat(html.getFumens())
                    .hasSize(5)
                    .contains("FfH8AeI8AeH8AeI8AeD8AeI8AeI8AeO8AeI8AeI8Ae?D8AeN8AeI8AeC8AeM8AeH8AeD8JedCXUAFLDmClcJSAVDEH?BEooRBUoAVB");
        }

        @Test
        void quizTetfuWithPatterns() throws Exception {
            /*
            comment: #Q=[T](I)SZJLOSLT
             */

            String fumen = "v115@FfH8AeI8AeH8AeI8AeD8AeI8AeI8AeO8AeI8AeI8Ae?D8AeN8AeI8AeC8AeM8AeH8AeD8JeAgWdAFLDmClcJSAVztS?AVG88A4N88A5szPCM3TWC0AAAA";
            String command = String.format("ren -t %s -p #Q=[I](I)JIILJIII", fumen);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput())
                    .contains(Messages.foundSolutions(4))
                    .contains(Messages.maxRen(2))
                    .contains("IIJIILJIII");
            assertThat(log.getError()).isEmpty();

            // HTML
            SetupHTML html = OutputFileHelper.loadRenHTML();
            assertThat(html.getHtml())
                    .contains("2 Ren")
                    .doesNotContain("3 Ren");

            assertThat(html.getFumens())
                    .hasSize(4)
                    .contains("FfH8AeI8AeH8AeI8AeD8AeI8AeI8AeO8AeI8AeI8Ae?D8AeN8AeI8AeC8AeM8AeH8AeD8JeZCXWAFLDmClcJSAVDEH?BEooRBJoAVBJ+AAAvhB5rAZ4A");
        }
    }

    @Nested
    class ErrorTest extends RenUseCaseBaseTest {
        @Override
        @BeforeEach
        void setUp() throws IOException {
            super.setUp();
        }

        @Test
        void caseAsterrisk() throws Exception {
            // * が含まれるケース
            String fumen = "v115@9gzhFezhFezhFezhPeAgH";
            String command = String.format("ren -t %s -p *", fumen);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getError()).contains("Should specify one sequence, not allow pattern(*) for multi sequences");
        }
    }
}