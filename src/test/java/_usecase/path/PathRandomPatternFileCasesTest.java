package _usecase.path;

import _usecase.Log;
import _usecase.RunnerHelper;
import entry.EntryPointMain;
import module.LongTest;
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

class PathRandomPatternFileCasesTest extends PathUseCaseBaseTest {
    @ParameterizedTest
    @ArgumentsSource(PatternTestCase.class)
    @LongTest
    void pattern(String command, int unique, int success, int all) throws Exception {
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.pathCount(unique))
                .contains(Messages.success(success, all));
    }

    private static class PatternTestCase extends TestCase {
        public PatternTestCase() {
            super("usecase/path/pattern.csv", 4);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ReservedTestCase.class)
    @LongTest
    void reserved(String command, int unique, int success, int all) throws Exception {
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.pathCount(unique))
                .contains(Messages.success(success, all));
    }

    private static class ReservedTestCase extends TestCase {
        public ReservedTestCase() {
            super("usecase/path/reserved.csv", 6);
        }
    }

    private abstract static class TestCase implements ArgumentsProvider {
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
                    // GitHub Actionsの環境では、フィールドが高いとOutOfMemoryErrorになりやすいので暫くスキップする
                    .filter(line -> !(line.contains("-c 7") || line.contains("-c 8")))
                    .map(line -> {
                        int index = line.substring(1).indexOf('"');
                        String command = line.substring(1, index + 1);
                        String[] split = line.substring(index).split(",");
                        assert split.length == 4 : Arrays.toString(split);
                        return new TestData(command, Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
                    })
                    .collect(Collectors.toList());
            Collections.shuffle(testCases);
            return testCases;
        }

        private Arguments toArguments(TestData data) {
            return Arguments.of(data.command, data.unieque, data.success, data.all);
        }
    }

    private static class TestData {
        private final String command;
        private final int unieque;
        private final int success;
        private final int all;

        private TestData(String command, int path, int success, int all) {
            this.command = command;
            this.unieque = path;
            this.success = success;
            this.all = all;
        }

        @Override
        public String toString() {
            return String.format("TestData{%s (%d)(%d/%d)}", command, unieque, success, all);
        }
    }
}
