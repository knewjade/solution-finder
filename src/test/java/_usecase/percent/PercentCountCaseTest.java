package _usecase.percent;

import _usecase.Log;
import _usecase.RunnerHelper;
import entry.EntryPointMain;
import module.LongTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PercentCountCaseTest extends PercentUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void pattern1() throws Exception {
            /*
            comment: <Empty>
            _______XXX
            ________XX
            XX____XXXX
            XX_____XXX
             */

        String tetfu = "m115@EhC8HeD8DeF8EeC8JeAgH";

        String command = String.format("percent -p *p7 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(4374, 5040))
                .contains("*p7");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void pattern2() throws Exception {
            /*
            comment: <Empty>
            X____X____
            XX__XX____
            XX__XX____
            XXXXXX____
             */

        String tetfu = "http://harddrop.com/fumen/?v115@9gA8DeA8DeB8BeB8DeB8BeB8DeF8NeAgH";

        String command = String.format("percent -p T,I,O,*p4 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(742, 840))
                .contains("T,I,O,*p4");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void pattern3() throws Exception {
            /*
            comment: <Empty>
            __________
            __________
            _________X
            ___XX__XXX
             */

        String tetfu = "http://harddrop.com/fumen/?d115@ahA8CeB8BeC8JeAgH";

        String command = String.format("percent -c 3 -p *p7 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.clearLine(3))
                .contains(Messages.useHold())
                .contains(Messages.success(2368, 5040))
                .contains("*p7");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void pattern4() throws Exception {
            /*
            comment: <Empty>
            __________
            __________
            X________X
            X______XXX
             */

        String tetfu = "http://harddrop.com/fumen/?m115@RhA8HeB8FeC8JeAgH";

        String command = String.format("percent -c 3 -p *p7 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.clearLine(3))
                .contains(Messages.useHold())
                .contains(Messages.success(5028, 5040))
                .contains("*p7");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void pattern5() throws Exception {
            /*
            comment: <Empty>
            XXXX_____X
            XX______XX
            XXX____XXX
            XXX_____XX
             */

        String tetfu = "http://fumen.zui.jp/?m115@9gzhEewwRpFexwRpglDeBtwwilEeBtJeAgH";

        String command = String.format("percent -p [OSZTLJ]p6 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.clearLine(4))
                .contains(Messages.useHold())
                .contains(Messages.success(702, 720))
                .contains("[OSZTLJ]p6");

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void pattern6() throws Exception {
        // ミノを置いてライン消去される場合

            /*
            XXXXX_____
            XXXXXXXXXX
            XXXXXXX___
            XXXXXX____
             */

        String tetfu = "v115@9gE8EeF8DeG8CeF8NexHJ";

        String command = String.format("percent -p *p3 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.clearLine(4))
                .contains(Messages.useHold())
                .contains(Messages.success(54, 210))
                .contains("*p3");

        assertThat(log.getError()).isEmpty();
    }

    @ParameterizedTest
    @ArgumentsSource(Hold1TestCase.class)
    @LongTest
    void hold1(String command, int success, int all) throws Exception {
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(success, all));
    }

    private static class Hold1TestCase extends TestCase {
        public Hold1TestCase() {
            super("usecase/percent/hold1.csv", 20);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(Hold2TestCase.class)
    @LongTest
    void hold2(String command, int success, int all) throws Exception {
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.success(success, all));
    }

    private static class Hold2TestCase extends TestCase {
        public Hold2TestCase() {
            super("usecase/percent/hold2.csv", 10);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(NoHold1TestCase.class)
    @LongTest
    void noHold1(String command, int success, int all) throws Exception {
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.noUseHold())
                .contains(Messages.success(success, all));
    }

    private static class NoHold1TestCase extends TestCase {
        public NoHold1TestCase() {
            super("usecase/percent/nohold1.csv", 40);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(NoHold2TestCase.class)
    @LongTest
    void noHold2(String command, int success, int all) throws Exception {
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.noUseHold())
                .contains(Messages.success(success, all));
    }

    private static class NoHold2TestCase extends TestCase {
        public NoHold2TestCase() {
            super("usecase/percent/nohold2.csv", 15);
        }
    }

    private static abstract class TestCase implements ArgumentsProvider {
        private final String resourcePath;
        private final long limit;

        TestCase(String resourcePath, long limit) {
            this.resourcePath = resourcePath;
            this.limit = limit;
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
            List<TestData> testCases = loadTestCases();
            Stream<Arguments> stream = testCases.stream().map(this::toArguments);
            if (limit < 0)
                return stream;
            return stream.limit(limit);
        }

        private List<TestData> loadTestCases() throws IOException {
            String resultPath = ClassLoader.getSystemResource(resourcePath).getPath();
            List<TestData> testCases = Files.lines(Paths.get(resultPath))
                    .filter(line -> !line.startsWith("//"))
                    .map(line -> {
                        int index = line.substring(1).indexOf('"');
                        String command = line.substring(1, index + 1);
                        String[] split = line.substring(index).split(",");
                        assert split.length == 3;
                        return new TestData(command, Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                    })
                    .collect(Collectors.toList());
            Collections.shuffle(testCases);
            return testCases;
        }

        private Arguments toArguments(TestData data) {
            return Arguments.of(data.command, data.success, data.all);
        }
    }

    private static class TestData {
        private final String command;
        private final int success;
        private final int all;

        private TestData(String command, int success, int all) {
            this.command = command;
            this.success = success;
            this.all = all;
        }

        @Override
        public String toString() {
            return String.format("TestData{%s (%d/%d)}", command, success, all);
        }
    }
}
