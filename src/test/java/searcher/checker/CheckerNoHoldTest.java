package searcher.checker;

import common.ResultHelper;
import common.buildup.BuildUp;
import common.datastore.*;
import common.datastore.action.Action;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.parser.BlockInterpreter;
import common.parser.OperationTransform;
import core.action.candidate.Candidate;
import core.action.candidate.CandidateFacade;
import core.action.reachable.ILockedReachable;
import core.action.reachable.ReachableFacade;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import module.LongTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import searcher.common.validator.PerfectValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.mino.Piece.*;
import static org.assertj.core.api.Assertions.assertThat;

class CheckerNoHoldTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
    private final PerfectValidator validator = new PerfectValidator();
    private final CheckerNoHold<Action> checker = new CheckerNoHold<>(minoFactory, validator);

    private List<Piece> parseToBlocks(Result result) {
        return ResultHelper.createOperationStream(result)
                .map(Operation::getPiece)
                .collect(Collectors.toList());
    }

    private Operations parseToOperations(Result result) {
        return new Operations(ResultHelper.createOperationStream(result));
    }

    private void assertResult(Field field, int maxClearLine, ILockedReachable reachable, List<Piece> pieces) {
        Result result = checker.getResult();

        // Check pieces is same
        List<Piece> resultPieces = parseToBlocks(result);
        assertThat(resultPieces).isEqualTo(pieces.subList(0, resultPieces.size()));

        // Check can build result
        Operations operations = parseToOperations(result);
        List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, maxClearLine);
        boolean cansBuild = BuildUp.cansBuild(field, operationWithKeys, maxClearLine, reachable);
        assertThat(cansBuild).isTrue();
    }

    @Test
    void testGraceSystem() {
        List<Pair<List<Piece>, Boolean>> testCases = new ArrayList<Pair<List<Piece>, Boolean>>() {
            {
                add(new Pair<>(Arrays.asList(T, S, O, J), false));
                add(new Pair<>(Arrays.asList(T, O, J, S), false));
                add(new Pair<>(Arrays.asList(T, T, L, J), true));
                add(new Pair<>(Arrays.asList(T, T, S, Z), true));
                add(new Pair<>(Arrays.asList(T, S, Z, T), false));
                add(new Pair<>(Arrays.asList(J, S, Z, L), false));
                add(new Pair<>(Arrays.asList(Z, I, O, T), false));
                add(new Pair<>(Arrays.asList(I, J, J, O), true));
                add(new Pair<>(Arrays.asList(T, S, Z, J), false));
                add(new Pair<>(Arrays.asList(L, S, Z, T), false));
            }
        };

        // Field
        String marks = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 4;
        int maxDepth = 4;

        // Initialize
        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (Pair<List<Piece>, Boolean> testCase : testCases) {
            // Set test case
            List<Piece> pieces = testCase.getKey();
            Boolean expectedCount = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCount);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, pieces);
        }
    }

    @Test
    void testCase1() {
        List<Pair<List<Piece>, Boolean>> testCases = new ArrayList<Pair<List<Piece>, Boolean>>() {
            {
                add(new Pair<>(Arrays.asList(J, I, O, L, S, Z, T), true));
                add(new Pair<>(Arrays.asList(J, O, I, L, Z, S, T), true));
                add(new Pair<>(Arrays.asList(O, J, I, L, Z, S, T), true));
            }
        };

        // Field
        String marks = "" +
                "X________X" +
                "X________X" +
                "XX______XX" +
                "XXXXXX__XX" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 4;
        int maxDepth = 6;

        // Initialize
        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (Pair<List<Piece>, Boolean> testCase : testCases) {
            // Set test case
            List<Piece> pieces = testCase.getKey();
            Boolean expectedCount = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCount);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, pieces);
        }
    }

    @Test
    void testCase2() {
        List<Pair<List<Piece>, Boolean>> testCases = new ArrayList<Pair<List<Piece>, Boolean>>() {
            {
                add(new Pair<>(Arrays.asList(I, L, T, S, Z), true));
            }
        };

        // Field
        String marks = "" +
                "XX______XX" +
                "X______XXX" +
                "X______XXX" +
                "XX_XXX_XXX" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 4;
        int maxDepth = 5;

        // Initialize
        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (Pair<List<Piece>, Boolean> testCase : testCases) {
            // Set test case
            List<Piece> pieces = testCase.getKey();
            Boolean expectedCount = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCount);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, pieces);
        }
    }

    @Test
    void testCase3() {
        List<Pair<List<Piece>, Boolean>> testCases = new ArrayList<Pair<List<Piece>, Boolean>>() {
            {
                add(new Pair<>(Arrays.asList(T, I, L, S, O, Z, J), false));
                add(new Pair<>(Arrays.asList(O, J, I, L, T, S, Z), false));
                add(new Pair<>(Arrays.asList(O, J, L, T, I, S, Z), true));
            }
        };

        // Field
        String marks = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXXXX__" +
                "XXXXXXXX__" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 8;
        int maxDepth = 7;

        // Initialize
        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (Pair<List<Piece>, Boolean> testCase : testCases) {
            // Set test case
            List<Piece> pieces = testCase.getKey();
            Boolean expectedCount = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCount);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, pieces);
        }
    }

    @Test
    void testCaseFilledLine() {
        List<Pair<List<Piece>, Boolean>> testCases = new ArrayList<Pair<List<Piece>, Boolean>>() {
            {
                add(new Pair<>(Arrays.asList(I, Z, L, I), true));
            }
        };

        // Field
        String marks = "" +
                "XXXXX_____" +
                "XXXXXXXXXX" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 5;
        int maxDepth = 4;

        // Initialize
        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (Pair<List<Piece>, Boolean> testCase : testCases) {
            // Set test case
            List<Piece> pieces = testCase.getKey();
            Boolean expectedCount = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCount);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, pieces);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(TestCase.class)
    @LongTest
    void testCaseList(Pieces pieces, boolean expectedCount) {
        int maxDepth = 10;
        int maxClearLine = 4;
        Field field = FieldFactory.createField(maxClearLine);

        // Initialize
        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        // Set test case
        List<Piece> piecesList = pieces.getPieces();

        // Execute
        boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
        assertThat(isSucceed).isEqualTo(expectedCount);

        // Check result
        if (isSucceed)
            assertResult(field, maxClearLine, reachable, piecesList);
    }

    private static class TestCase implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
            List<Pair<Pieces, Boolean>> testCases = loadTestCases();
            return testCases.stream().limit(50L).map(this::toArguments);
        }

        private List<Pair<Pieces, Boolean>> loadTestCases() throws IOException {
            String resultPath = ClassLoader.getSystemResource("perfects/checker_avoidhold.txt").getPath();
            List<Pair<Pieces, Boolean>> testCases;
            try (Stream<String> lines = Files.lines(Paths.get(resultPath))) {
                testCases = lines
                        .filter(line -> !line.startsWith("//"))
                        .map(line -> line.split("="))
                        .map(split -> {
                            Stream<Piece> blocks = BlockInterpreter.parse(split[0]);
                            LongPieces pieces = new LongPieces(blocks);
                            boolean isSucceed = "o".equals(split[1]);
                            return new Pair<Pieces, Boolean>(pieces, isSucceed);
                        })
                        .collect(Collectors.toList());
            }
            Collections.shuffle(testCases);
            return testCases;
        }

        private Arguments toArguments(Pair<?, ?> pair) {
            return Arguments.of(pair.getKey(), pair.getValue());
        }
    }
}
