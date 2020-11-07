package _usecase.util.fumen;

import _usecase.Log;
import _usecase.RunnerHelper;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class UtilFumenTetfuCaseTest {
    @Nested
    class FumenTest extends UtilFumenUseCaseBaseTest {
        private String buildCommand(String subCommand, String fumen, String options) {
            return String.format("util fumen -M %s -t %s %s", subCommand, fumen, options);
        }

        @Override
        @BeforeEach
        void setUp() throws IOException {
            super.setUp();
        }

        @Test
        void case1() throws Exception {
            String fumen = "v115@vhdXKJNJJ0/ISSJzHJGDJJHJSGJvLJ0KJJEJzJJ+NJ?tMJFDJz/IyQJsGJOGJpFJ+NJ3MJXDJULJzGJxBJiJJtKJyJ?J0JJ";
            String command = buildCommand("reduce", fumen, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().trim()).isEqualTo("v115@tfBtglwwzhR4ilxwRpR4g0ilBtRpR4g0glBtwwBtR4?h0whRpwhh0AtywwhRpwhg0Btxwg0whRpwhg0Atglxwg0whR?pwhilwwh0DtQ4glwhi0wwBtilwhRpg0xwT4whRpglwwR4Bt?Q4whilJeAgH");
        }

        @Test
        void case2Over() throws Exception {
            // フィールドの高さが24以上必要なケース
            String fumen = "v115@xeI8AeI8AeI8AeI8AeI8AeI8AeI8AeI8AeI8AeI8Ae?I8AeI8AeI8AeI8AeI8AeI8AeI8AeI8Key0HvhSTqHmlH0yH?9zHXpHprH3sH22Hx2HJqHzvHS2HXsHzlHCnH0yHvzHNyHGz?H";
            String command = buildCommand("reduce", fumen, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getReturnCode()).isGreaterThan(0);
            assertThat(log.getOutput().trim()).isEqualTo("");
        }

        @Test
        void case3() throws Exception {
            String fumens = "v115@vhFXKJNJJ0/IWSJTIJCDJ v115@vhFRPJTFJvLJMJJi/IGBJ";
            String command = buildCommand("reduce", fumens, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains("v115@9gBtEeilwwBtDeglRpxwR4Ceg0RpwwR4Dei0JeAgH")
                    .contains("v115@9gili0DeglAtRpQ4g0DeBtRpR4DeAtzhQ4NeAgH");
        }

        @Test
        void case4CommentAtHead() throws Exception {
            // 1ページ目のコメントが残る
            String fumens = "v115@vhFXKYZAxXHDBQGDSA1d0ACDYHDBQzuRA1Dq9BlAAA?ANJYZAyXHDBQGDSA1d0ACDYHDBQzuRA1Dq9BlAAAA0/XZAz?XHDBQGDSA1d0ACDYHDBQzuRA1Dq9BlAAAAWSYZA0XHDBQGD?SA1d0ACDYHDBQzuRA1Dq9BlAAAATIYZA1XHDBQGDSA1d0AC?DYHDBQzuRA1Dq9BlAAAACDYZA2XHDBQGDSA1d0ACDYHDBQz?uRA1Dq9BlAAAA";
            String command = buildCommand("reduce", fumens, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains("v115@9gBtEeilwwBtDeglRpxwR4Ceg0RpwwR4Dei0JeAgWZ?AxXHDBQGDSA1d0ACDYHDBQzuRA1Dq9BlAAAA");
        }
    }
}