package searcher.checker;

import common.ResultHelper;
import common.buildup.BuildUp;
import common.datastore.*;
import common.datastore.action.Action;
import common.datastore.blocks.LongPieces;
import common.order.OrderLookup;
import common.order.StackOrder;
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
import lib.Randoms;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.mino.Piece.*;
import static org.assertj.core.api.Assertions.assertThat;

class CheckerUsingHoldTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
    private final PerfectValidator validator = new PerfectValidator();
    private final CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);

    private List<Piece> parseToBlocks(Result result) {
        return ResultHelper.createOperationStream(result)
                .map(Operation::getPiece)
                .collect(Collectors.toList());
    }

    private Operations parseToOperations(Result result) {
        return new Operations(ResultHelper.createOperationStream(result));
    }

    private void assertResult(Field field, int maxClearLine, ILockedReachable reachable, List<Piece> blocks) {
        Result result = checker.getResult();

        // Check blocks is same
        List<Piece> resultPieces = parseToBlocks(result);
        Piece lastHoldPiece = result.getLastHold();
        HashSet<LongPieces> pieces = OrderLookup.reverseBlocks(resultPieces, blocks.size()).stream()
                .map(StackOrder::toStream)
                .map(stream -> stream.map(block -> block != null ? block : lastHoldPiece))
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));

        assertThat(pieces).contains(new LongPieces(blocks));

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
                add(new Pair<>(Arrays.asList(T, S, Z, T), true));
                add(new Pair<>(Arrays.asList(J, S, Z, L), false));
                add(new Pair<>(Arrays.asList(Z, I, O, T), false));
                add(new Pair<>(Arrays.asList(I, J, J, O), true));
                add(new Pair<>(Arrays.asList(T, S, Z, J), true));
                add(new Pair<>(Arrays.asList(L, S, Z, T), true));
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
            Boolean expectedCheckFlag = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCheckFlag);

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
            Boolean expectedCheckFlag = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCheckFlag);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, pieces);
        }
    }

    @Test
    @LongTest
    void testPossiblePerfect() throws IOException {
        // Field
        Field field = FieldFactory.createSmallField();
        int maxClearLine = 4;
        int maxDepth = 10;

        // Set to check No Possible Perfect
        String noPerfectPath = ClassLoader.getSystemResource("orders/noperfect.txt").getPath();
        HashSet<LongPieces> noPerfectSet;
        try (Stream<String> lines = Files.lines(Paths.get(noPerfectPath))) {
            noPerfectSet = lines
                    .map(BlockInterpreter::parse)
                    .map(LongPieces::new)
                    .collect(Collectors.toCollection(HashSet::new));
        }

        // Initialize
        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        Randoms randoms = new Randoms();
        for (int count = 0; count < 75; count++) {
            // Set test case
            int cycle = randoms.nextIntClosed(0, 8);
            List<Piece> pieces = randoms.block11InCycle(cycle);

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            boolean expectedFlag = !noPerfectSet.contains(new LongPieces(pieces));
            assertThat(isSucceed).isEqualTo(expectedFlag);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, pieces);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(TestCase.class)
    @LongTest
    void testNoPossiblePerfect(LongPieces pieces) {
        // Field
        Field field = FieldFactory.createSmallField();
        int maxClearLine = 4;
        int maxDepth = 10;

        // Initialize
        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        // Set test case
        List<Piece> blocks = pieces.getPieces();

        // Execute
        boolean isSucceed = checker.check(field, blocks, candidate, maxClearLine, maxDepth);
        assertThat(isSucceed).isFalse();
    }

    private static class TestCase implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
            List<LongPieces> testCases = loadTestCases();
            return testCases.stream().map(Arguments::of).limit(8L);
        }

        private List<LongPieces> loadTestCases() throws IOException {
            String resultPath = ClassLoader.getSystemResource("orders/noperfect.txt").getPath();
            List<LongPieces> testCases;
            try (Stream<String> lines = Files.lines(Paths.get(resultPath))) {
                testCases = lines
                        .map(BlockInterpreter::parse)
                        .map(LongPieces::new)
                        .distinct()
                        .collect(Collectors.toList());
            }
            Collections.shuffle(testCases);
            return testCases;
        }
    }
}
