package _usecase.path;

import _usecase.Log;
import _usecase.RunnerHelper;
import entry.EntryPointMain;
import module.LongTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PathRandomFileCaseTest extends PathUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @ParameterizedTest
    @ArgumentsSource(Hold1TestCase.class)
    @LongTest
    void hold1(String command, int unique) throws Exception {
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.uniqueCount(unique));
    }

    private static class Hold1TestCase extends TestCase {
        public Hold1TestCase() {
            super("usecase/path/hold1.csv", 5);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(Hold2TestCase.class)
    @LongTest
    void hold2(String command, int unique) throws Exception {
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.useHold())
                .contains(Messages.uniqueCount(unique));
    }

    private static class Hold2TestCase extends TestCase {
        public Hold2TestCase() {
            super("usecase/path/hold2.csv", 5);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(NoHold1TestCase.class)
    @LongTest
    void noHold1(String command, int unique) throws Exception {
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.noUseHold())
                .contains(Messages.uniqueCount(unique));
    }

    private static class NoHold1TestCase extends TestCase {
        public NoHold1TestCase() {
            super("usecase/path/nohold1.csv", 10);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(NoHold2TestCase.class)
    @LongTest
    void noHold2(String command, int unique) throws Exception {
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.noUseHold())
                .contains(Messages.uniqueCount(unique));
    }

    private static class NoHold2TestCase extends TestCase {
        public NoHold2TestCase() {
            super("usecase/path/nohold2.csv", 10);
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
                        assert split.length == 2 : Arrays.toString(split);
                        return new TestData(command, Integer.parseInt(split[1]));
                    })
                    .collect(Collectors.toList());
            Collections.shuffle(testCases);
            return testCases;
        }

        private Arguments toArguments(TestData data) {
            return Arguments.of(data.command, data.unieque);
        }
    }

    private static class TestData {
        private final String command;
        private final int unieque;

        private TestData(String command, int unieque) {
            this.command = command;
            this.unieque = unieque;
        }

        @Override
        public String toString() {
            return String.format("TestData{%s (%d)}", command, unieque);
        }
    }
}
