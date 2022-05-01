package searcher.checkmate;

import common.ResultHelper;
import common.buildup.BuildUp;
import common.datastore.*;
import common.datastore.action.Action;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.parser.BlockInterpreter;
import common.parser.OperationTransform;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
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

class CheckmateNoHoldTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = MinoRotation.create();
    private final PerfectValidator validator = new PerfectValidator();
    private final Checkmate<Action> checkmate = new CheckmateNoHold<>(minoFactory, validator);

    private List<Piece> parseToBlocks(Result result) {
        return ResultHelper.createOperationStream(result)
                .map(Operation::getPiece)
                .collect(Collectors.toList());
    }

    private Operations parseToOperations(Result result) {
        return new Operations(ResultHelper.createOperationStream(result));
    }

    private void assertResult(Result result, Field field, int maxClearLine, LockedReachable reachable, List<Piece> pieces) {
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
    void testLong9() {
        List<Pair<List<Piece>, Integer>> testCases = new ArrayList<Pair<List<Piece>, Integer>>() {
            {
                add(new Pair<>(Arrays.asList(I, S, Z, T, J, I, S, Z, S, Z), 0));
                add(new Pair<>(Arrays.asList(T, S, L, I, Z, J, L, O, O, S), 5));
                add(new Pair<>(Arrays.asList(L, Z, S, J, Z, Z, Z, I, T, T), 0));
                add(new Pair<>(Arrays.asList(T, T, S, S, Z, Z, L, L, J, J), 5));
                add(new Pair<>(Arrays.asList(O, S, O, S, Z, L, Z, L, I, I), 0));
                add(new Pair<>(Arrays.asList(J, I, T, O, L, S, I, T, Z, O), 7));
                add(new Pair<>(Arrays.asList(S, T, J, L, O, O, T, S, L, L), 6));
                add(new Pair<>(Arrays.asList(S, T, J, L, O, O, T, S, L), 6));
                add(new Pair<>(Arrays.asList(Z, S, T, I, O, J, L, Z, S), 1));
                add(new Pair<>(Arrays.asList(S, T, J, L, O, O, T, S, L), 6));
            }
        };

        // Field
        String marks = "" +
                "__________" +
                "X_________" +
                "X_________" +
                "XX________" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 4;
        int maxDepth = 9;

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (Pair<List<Piece>, Integer> testCase : testCases) {
            // Set test case
            List<Piece> pieces = testCase.getKey();
            int expectedCount = testCase.getValue();

            // Execute
            List<Result> results = checkmate.search(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(results)
                    .as(pieces.toString())
                    .hasSize(expectedCount);

            // Check result
            for (Result result : results)
                assertResult(result, field, maxClearLine, reachable, pieces);
        }
    }

    @Test
    @LongTest
    void testLong10() {
        List<Pair<List<Piece>, Integer>> testCases = new ArrayList<Pair<List<Piece>, Integer>>() {
            {
                add(new Pair<>(Arrays.asList(I, S, Z, T, J, I, S, Z, S, Z, T), 3));
                add(new Pair<>(Arrays.asList(S, Z, T, L, J, I, O, S, Z, T, L), 6));
                add(new Pair<>(Arrays.asList(T, L, J, Z, S, O, O, T, J, L, I), 21));
                add(new Pair<>(Arrays.asList(T, L, J, Z, S, O, O, T, J, L, T), 21));
                add(new Pair<>(Arrays.asList(T, L, J, Z, S, O, O, T, J, L), 21));
                add(new Pair<>(Arrays.asList(I, S, Z, T, J, I, S, Z, S, Z), 3));
                add(new Pair<>(Arrays.asList(T, S, L, I, Z, J, L, O, O, S), 2));
                add(new Pair<>(Arrays.asList(L, Z, S, J, Z, Z, Z, I, T, T), 7));
                add(new Pair<>(Arrays.asList(T, T, S, S, Z, Z, L, L, J, J), 18));
                add(new Pair<>(Arrays.asList(O, S, O, S, Z, L, Z, L, I, I), 4));
                add(new Pair<>(Arrays.asList(J, I, T, O, L, S, I, T, Z, O), 9));
                add(new Pair<>(Arrays.asList(S, T, J, L, O, O, T, S, L, L), 16));
            }
        };

        // Field
        int maxClearLine = 4;
        int maxDepth = 10;
        Field field = FieldFactory.createField(maxClearLine);

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (Pair<List<Piece>, Integer> testCase : testCases) {
            // Set test case
            List<Piece> pieces = testCase.getKey();
            int expectedCount = testCase.getValue();

            // Execute
            List<Result> results = checkmate.search(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(results)
                    .as(pieces.toString())
                    .hasSize(expectedCount);

            // Check result
            for (Result result : results)
                assertResult(result, field, maxClearLine, reachable, pieces);
        }
    }

    @Test
    void testMultiPath1() {
        // Field
        String marks = "" +
                "X________X" +
                "XX__XX__XX" +
                "XX__XX__XX" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 3;
        int maxDepth = 4;

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        // Set test case
        List<Piece> pieces = Arrays.asList(J, L, S, Z);
        int expectedCount = 2;

        // Execute
        List<Result> results = checkmate.search(field, pieces, candidate, maxClearLine, maxDepth);
        assertThat(results)
                .as(pieces.toString())
                .hasSize(expectedCount);

        // Check result
        for (Result result : results)
            assertResult(result, field, maxClearLine, reachable, pieces);
    }

    @Test
    void testMultiPath2() {
        // Field
        String marks = "" +
                "X____XXXXX" +
                "XX__XXXXXX" +
                "XX__XXXXXX" +
                "XXXXXX__XX" +
                "XXXXXX__XX" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 5;
        int maxDepth = 3;

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        // Set test case
        List<Piece> pieces = Arrays.asList(S, Z, O);
        int expectedCount = 1;

        // Execute
        List<Result> results = checkmate.search(field, pieces, candidate, maxClearLine, maxDepth);
        assertThat(results)
                .as(pieces.toString())
                .hasSize(expectedCount);

        // Check result
        for (Result result : results)
            assertResult(result, field, maxClearLine, reachable, pieces);
    }

    @Test
    void testFilledLine() {
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
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        // Set test case
        List<Piece> pieces = Arrays.asList(I, Z, L, I);
        int expectedCount = 1;

        // Execute
        List<Result> results = checkmate.search(field, pieces, candidate, maxClearLine, maxDepth);
        assertThat(results)
                .as(pieces.toString())
                .hasSize(expectedCount);

        // Check result
        for (Result result : results)
            assertResult(result, field, maxClearLine, reachable, pieces);
    }

    @ParameterizedTest
    @ArgumentsSource(TestCase.class)
    @LongTest
    void testCaseList(Pieces pieces, int expectedCount) {
        // Field
        int maxClearLine = 4;
        int maxDepth = 10;
        Field field = FieldFactory.createField(maxClearLine);

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        // Set test case
        List<Piece> piecesList = pieces.getPieces();

        // Execute
        List<Result> results = checkmate.search(field, piecesList, candidate, maxClearLine, maxDepth);
        assertThat(results)
                .as(piecesList.toString())
                .hasSize(expectedCount);

        // Check result
        for (Result result : results)
            assertResult(result, field, maxClearLine, reachable, piecesList);
    }

    private static class TestCase implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
            List<Pair<Pieces, Integer>> testCases = loadTestCases();
            return testCases.stream().map(this::toArguments).limit(15L);
        }

        private List<Pair<Pieces, Integer>> loadTestCases() throws IOException {
            String resultPath = ClassLoader.getSystemResource("perfects/checkmate_nohold.txt").getPath();
            List<Pair<Pieces, Integer>> testCases = Files.lines(Paths.get(resultPath))
                    .map(line -> line.split("//")[0])
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> line.split("="))
                    .map(split -> {
                        Stream<Piece> blocks = BlockInterpreter.parse(split[0]);
                        LongPieces pieces = new LongPieces(blocks);
                        int count = Integer.parseInt(split[1]);
                        return new Pair<Pieces, Integer>(pieces, count);
                    })
                    .collect(Collectors.toList());
            Collections.shuffle(testCases);
            return testCases;
        }

        private Arguments toArguments(Pair<?, ?> pair) {
            return Arguments.of(pair.getKey(), pair.getValue());
        }
    }
}
