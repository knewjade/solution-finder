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
import core.action.candidate.LockedCandidate;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import lib.Randoms;
import module.LongTest;
import org.junit.jupiter.api.Test;
import searcher.common.validator.PerfectValidator;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static core.mino.Piece.*;
import static org.assertj.core.api.Assertions.assertThat;

class CheckerUsingHoldTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = new MinoRotation();
    private final PerfectValidator validator = new PerfectValidator();
    private final CheckerUsingHold checker = new CheckerUsingHold(minoFactory, validator);

    private List<Piece> parseToBlocks(Result result) {
        return ResultHelper.createOperationStream(result)
                .map(Operation::getPiece)
                .collect(Collectors.toList());
    }

    private Operations parseToOperations(Result result) {
        return new Operations(ResultHelper.createOperationStream(result));
    }

    private void assertResult(Field field, int maxClearLine, LockedReachable reachable, List<Piece> blocks) {
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
    void testGraceSystem() throws Exception {
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
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

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
    void testCaseFilledLine() throws Exception {
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
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

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
    void testNoPossiblePerfect() throws Exception {
        // Field
        Field field = FieldFactory.createSmallField();
        int maxClearLine = 4;
        int maxDepth = 10;

        // Piece
        URL noPerfect = ClassLoader.getSystemResource("orders/noperfect.txt");
        List<LongPieces> testCases = Files.lines(Paths.get(noPerfect.toURI()))
                .map(BlockInterpreter::parse)
                .map(LongPieces::new)
                .collect(Collectors.toList());
        Collections.shuffle(testCases);

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (LongPieces pieces : testCases.subList(0, 10)) {
            // Set test case
            List<Piece> blocks = pieces.getPieces();

            // Execute
            boolean isSucceed = checker.check(field, blocks, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isFalse();

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, blocks);
        }
    }

    @Test
    @LongTest
    void testPossiblePerfect() throws Exception {
        // Field
        Field field = FieldFactory.createSmallField();
        int maxClearLine = 4;
        int maxDepth = 10;

        // Set to check No Possible Perfect
        URL noPerfect = ClassLoader.getSystemResource("orders/noperfect.txt");
        HashSet<LongPieces> noPerfectSet = Files.lines(Paths.get(noPerfect.toURI()))
                .map(BlockInterpreter::parse)
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        Randoms randoms = new Randoms();
        for (int count = 0; count < 100; count++) {
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
}
