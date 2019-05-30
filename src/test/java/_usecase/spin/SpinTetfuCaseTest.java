package _usecase.spin;

import _usecase.Log;
import _usecase.RunnerHelper;
import _usecase.spin.files.OutputFileHelper;
import _usecase.spin.files.SpinHTML;
import entry.EntryPointMain;
import module.LongTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SpinTetfuCaseTest {
    @Nested
    class FumenTest extends SpinUseCaseBaseTest {
        private String buildCommand(String fumen, String options) {
            return String.format("spin -t %s %s", fumen, options);
        }

        @Override
        @BeforeEach
        void setUp() throws IOException {
            super.setUp();
        }

        @Test
        void case1() throws Exception {
            String fumen = "v115@zgD8FeF8DeG8CeH8BeI8KeAgH";
            String command = buildCommand(fumen, "-p TSLI -ft 5 -c 2");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(5));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens())
                    .hasSize(5)
                    .contains("zgD8DehlF8ywglG8wwQ4glH8R4I8Q4JeAgWDAzOkBA?")
                    .contains("vgglBewhD8ilBewhF8ywwhG8wwQ4whH8R4I8Q4JeAg?WEAze9VC")
                    .contains("kgglIeglCewhD8AehlBewhF8ywwhG8wwQ4whH8R4I8?Q4JeAgWEAze9VC")
                    .contains("xgQ4AeD8DeR4F8ywQ4G8wwhlH8AeglI8glJeAgWDAs?+zBA")
                    .contains("ighlCeQ4EeglCeR4D8glzhQ4F8DeG8BewwH8xwI8ww?JeAgWEA0SltC");
        }

        @Test
        void case2() throws Exception {
            String fumen = "v115@CgA8HeD8BeA8AeE8DeF8DeF8DeF8DeF8DeF8DeF8De?F8DeC8JeAgH";
            String command = buildCommand(fumen, "-p TSZLJI -fb 8 -ft 10 -c 2");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(1));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens())
                    .hasSize(1)
                    .contains("ufAtFeQ4AeBtBehlBeR4AtA8i0glywQ4D8g0glA8ww?E8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeC8JeAgWFAqCmFDz?AAAA");
        }

        @Test
        @LongTest
        void case3() throws Exception {
            String fumen = "v115@PhA8IeA8BeA8AeB8BeC8JeAgH";
            String command = buildCommand(fumen, "-p *! -fb 0 -ft 4 -c 3");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(2));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens())
                    .hasSize(2)
                    .contains("1gBtEewhCeBtDewhh0wwRpilA8whg0xwRpglR4A8wh?g0A8wwB8R4C8JeAgWHAq+bgCMeDCA")
                    .contains("rgQ4IeR4EewhCeQ4Eewhh0wwilRpA8whg0xwglBtRp?A8whg0A8wwB8BtC8JeAgWHAqujWCPuzBA");
        }

        @Test
        @LongTest
        void case4() throws Exception {
            String fumen = "v115@ThB8EeA8CeB8DeA8JeAgH";
            String command = buildCommand(fumen, "-p *! -fb 0 -ft 4 -c 3");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(0));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens()).isEmpty();
        }

        @Test
        @LongTest
        void case5() throws Exception {
            String fumen = "v115@zgD8FeE8EeF8DeG8CeH8LeAgH";
            String command = buildCommand(fumen, "-p *! -mr -1");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(1713));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens())
                    .hasSize(1709)
                    .contains("kgg0Ieg0DeD8h0DeE8ywR4F8wwR4glG8ilH8LeAgWE?As+jPC")
                    .contains("ogwhDeilBewhD8glQ4ywwhE8R4wwAtwhF8Q4Btg0G8?AtAeg0H8h0JeAgWGAKtjxCpCBAA")
                    .contains("qgzhCeQ4AeD8ywAtR4E8wwBtg0Q4F8AtAeg0AeG8h0?AeH8LeAgWFA03ntCpAAAA")
                    .contains("lgilGeglQ4AewhD8ywR4whE8wwRpQ4whF8RpAewhG8?i0H8Aeg0JeAgWGAq+ytCpCBAA");
        }

        @Test
        @LongTest
        void case6() throws Exception {
            String fumen = "v115@zgD8FeE8EeF8DeG8CeH8LeAgH";
            String command = buildCommand(fumen, "-p *! -r no -mr 1");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(16005));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens())
                    .hasSize(15938)
                    .contains("xgh0D8BeR4g0whE8R4wwg0whF8xwglwhG8wwglwhH8?hlJeAgWFAseltCqAAAA")
                    .contains("rgi0glRpBeD8g0glRpR4E8hlR4wwF8BtxwG8BtwwH8?LeAgWGA6OstCKHBAA")
                    .contains("tgilg0BeD8glRpi0E8RpR4whF8R4wwwhG8xwwhH8ww?whJeAgWGAUubgCs/AAA")
                    .contains("mgAtg0EeQ4AeBtg0AeD8R4Ath0wwE8Q4RpxwF8Rpgl?wwG8AeglAeH8hlJeAgWGAMH+tC6/AAA")
                    .contains("mgh0EeRpAeg0hlD8Rpwwg0AtglE8xwBtglF8wwAtR4?G8R4AeH8LeAgWGA0HUWCv/AAA");
        }

        @Test
        @LongTest
        void case7_1() throws Exception {
            String fumen = "v115@zgD8FeE8EeF8DeG8CeH8LeAgH";
            String command = buildCommand(fumen, "-p *! -mr 0");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(1524));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens())
                    .hasSize(1520)
                    .contains("kgwhglHewhglCeD8AewhhlBeE8whQ4ywF8R4wwAtG8?Q4BtH8AtKeAgWFAzXOMCsAAAA")
                    .contains("lgQ4BewhFeR4AewhD8ywQ4glwhE8wwRpglwhF8Rphl?G8i0H8Aeg0JeAgWGAKHExCJNBAA")
                    .contains("tgRpBeh0D8RpBeg0AeE8ywg0whF8wwhlwhG8Aeglwh?H8glwhJeAgWFAM+lPCvAAAA")
                    .contains("lgg0Iei0whD8ywR4whE8wwR4AtwhF8AeBtwhG8AtRp?H8RpJeAgWGAvX+tCp/AAA")

                    .doesNotContain("igzhFeBtEeD8Btwwi0E8xwBeg0F8wwilG8glBeH8Le?AgWFA0izPCpAAAA")
                    .doesNotContain("lgi0Ieg0AeD8BewwwhRpE8xwwhRpF8wwwhhlG8whAe?glH8AeglJeAgWFAU+LgCqAAAA")
                    .doesNotContain("kgzhIeR4D8BewwR4AeE8xwi0F8wwRpg0G8RpAeH8Le?AgWFAUnntCpAAAA")
                    .doesNotContain("mgR4GeR4h0D8Deg0whE8Bewwg0whF8xwglwhG8wwgl?whH8hlJeAgWFAseNPCzAAAA");
        }

        @Test
        @LongTest
        void case7_2() throws Exception {
            String fumen = "v115@zgD8FeE8EeF8DeG8CeH8LeAgH";
            String command = buildCommand(fumen, "-p *! -mr 1");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(1684));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens())
                    .hasSize(1680)

                    // -mr 0の結果が含まれている
                    .contains("kgwhglHewhglCeD8AewhhlBeE8whQ4ywF8R4wwAtG8?Q4BtH8AtKeAgWFAzXOMCsAAAA")
                    .contains("lgQ4BewhFeR4AewhD8ywQ4glwhE8wwRpglwhF8Rphl?G8i0H8Aeg0JeAgWGAKHExCJNBAA")
                    .contains("tgRpBeh0D8RpBeg0AeE8ywg0whF8wwhlwhG8Aeglwh?H8glwhJeAgWFAM+lPCvAAAA")
                    .contains("lgg0Iei0whD8ywR4whE8wwR4AtwhF8AeBtwhG8AtRp?H8RpJeAgWGAvX+tCp/AAA")

                    // -mr 1の結果が含まれている
                    .contains("igzhFeBtEeD8Btwwi0E8xwBeg0F8wwilG8glBeH8Le?AgWFA0izPCpAAAA")
                    .contains("lgi0Ieg0AeD8BewwwhRpE8xwwhRpF8wwwhhlG8whAe?glH8AeglJeAgWFAU+LgCqAAAA")
                    .contains("kgzhIeR4D8BewwR4AeE8xwi0F8wwRpg0G8RpAeH8Le?AgWFAUnntCpAAAA")
                    .contains("mgR4GeR4h0D8Deg0whE8Bewwg0whF8xwglwhG8wwgl?whH8hlJeAgWFAseNPCzAAAA");
        }

        @Test
        void case8_1() throws Exception {
            String fumen = "v115@HhB8AeH8BeI8AeG8JeAgH";
            String command = buildCommand(fumen, "-p *! -mr 0");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(0));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens()).isEmpty();
        }

        @Test
        void case8_2() throws Exception {
            String fumen = "v115@HhB8AeH8BeI8AeG8JeAgH";
            String command = buildCommand(fumen, "-p *! -mr 1");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(2));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens())
                    .hasSize(2)
                    .contains("1gBtIeBtEeB8wwH8xwI8wwG8JeAgWCA0XBAA")
                    .contains("1gi0Ieg0EeB8wwH8xwI8wwG8JeAgWCA0/AAA");
        }

        @Test
        void case8_3() throws Exception {
            String fumen = "v115@HhB8AeH8BeI8AeG8JeAgH";
            String command = buildCommand(fumen, "-p *! -r no");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(1));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens())
                    .hasSize(1)
                    .contains("HhB8wwH8xwI8wwG8JeAgWBA0AAAA");
        }

        @Test
        void case9_1() throws Exception {
            // 空中に浮いたTスピンが見つかる不具合に対するテスト
            String fumen = "v115@XgA8IeB8IeA8AeB8FeG8CeG8CeB8DeB8BeB8AeF8Ae?B8BeC8JeAgH";
            String command = buildCommand(fumen, "-p JT -ft 7");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(0));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens()).isEmpty();
        }

        @Test
        void case9_2() throws Exception {
            String fumen = "v115@XgA8IeB8IeA8AeB8FeG8CeG8CeB8DeB8BeB8AeF8Ae?B8BeC8JeAgH";
            String command = buildCommand(fumen, "-p JTS -ft 7");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(1));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens())
                    .hasSize(1)
                    .contains("XgA8IeB8IeA8AeB8Ceh0wwG8g0xwG8g0Q4wwB8DeB8?R4B8AeF8Q4B8BeC8JeAgWDAq+zBA");
        }

        @Test
        void case10() throws Exception {
            // ライン消去の上の段に屋根を作る
            String fumen = "v115@HhE8CeF8DeG8AeD8JeAgH";
            String command = buildCommand(fumen, "-p JTO -ft 3 -m 5");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(4));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens())
                    .hasSize(4)
                    .contains("4gi0Ieg0BeE8wwRpF8xwRpG8wwD8JeAgWDAUnfBA")
                    .contains("Ehh0AeE8Beg0F8ywg0G8wwD8JeAgWCA0/AAA")
                    .contains("5gRpHeRpBeE8i0F8ywg0G8wwD8JeAgWDA0vqBA")
                    .contains("2gRpHeRpEeE8i0F8ywg0G8wwD8JeAgWDA0vqBA");
        }

        @Test
        void case12() throws Exception {
            // ライン消去の上の段に屋根を作る
            String fumen = "v115@FhF8CeH8AeH8DeB8JeAgH";
            String command = buildCommand(fumen, "-p ZLT -ft 3 -m 6");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getOutput()).contains(Messages.foundSolutions(5));
            assertThat(log.getError()).isEmpty();

            // HTML
            SpinHTML html = OutputFileHelper.loadSpinHTML();

            assertThat(html.getFumens())
                    .hasSize(5)
                    .contains("sgglIeglIehlCeF8ywH8wwH8DeB8JeAgWCA0CBAA")
                    .contains("3gglGeilCeF8ywH8wwH8DeB8JeAgWCA0CBAA")
                    .contains("vgglIeglIehlF8ywH8wwH8DeB8JeAgWCA0CBAA")
                    .contains("5gilGeglAeF8ywH8wwH8DeB8JeAgWCA0CBAA")
                    .contains("1gBtIeBtCeF8ywH8wwH8DeB8JeAgWCA0XBAA");
        }
    }

    @Nested
    class ErrorTest extends SpinUseCaseBaseTest {
        private String buildCommand(String fumen, String options) {
            return String.format("spin -t %s %s", fumen, options);
        }

        @Override
        @BeforeEach
        void setUp() throws IOException {
            super.setUp();
        }

        @Test
        void minusFB() throws Exception {
            // -fb が0より小さい
            String fumen = "v115@9gzhFezhFezhFezhPeAgH";
            String command = buildCommand(fumen, "-p IOT -fb -1");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getError()).contains("Fill-bottom should be 0 <= y < 24");
        }

        @Test
        void ftLessThanFB() throws Exception {
            // -ft が -fb より小さい
            String fumen = "v115@9gzhFezhFezhFezhPeAgH";
            String command = buildCommand(fumen, "-p IOT -fb 4 -ft 3");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getError()).contains("Fill-top should be greater than or equal to fill-bottom");
        }

        @Test
        void clearLineLessThan0() throws Exception {
            // 消去ラインが0以下
            String fumen = "v115@9gzhFezhFezhFezhPeAgH";
            String command = buildCommand(fumen, "-p IOT -c 0");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getError()).contains("Required-clear-line should be 1 <= line <= 3");
        }

        @Test
        void clearLineGreaterThan4() throws Exception {
            // 消去ラインが4以上
            String fumen = "v115@9gzhFezhFezhFezhPeAgH";
            String command = buildCommand(fumen, "-p IOT -c 4");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            assertThat(log.getError()).contains("Required-clear-line should be 1 <= line <= 3");
        }
    }
}