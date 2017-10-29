package searcher.checker;

import common.ResultHelper;
import common.buildup.BuildUp;
import common.datastore.*;
import common.datastore.action.Action;
import common.datastore.blocks.LongBlocks;
import common.order.OrderLookup;
import common.order.StackOrder;
import common.parser.BlockInterpreter;
import common.parser.OperationTransform;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import lib.Randoms;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import searcher.common.validator.PerfectValidator;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static core.mino.Block.*;
import static org.assertj.core.api.Assertions.assertThat;

class CheckerUsingHoldTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = new MinoRotation();
    private final PerfectValidator validator = new PerfectValidator();
    private final CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);

    private List<Block> parseToBlocks(Result result) {
        return ResultHelper.createOperationStream(result)
                .map(Operation::getBlock)
                .collect(Collectors.toList());
    }

    private Operations parseToOperations(Result result) {
        return new Operations(ResultHelper.createOperationStream(result));
    }

    private void assertResult(Field field, int maxClearLine, LockedReachable reachable, List<Block> blocks) {
        Result result = checker.getResult();

        // Check blocks is same
        List<Block> resultBlocks = parseToBlocks(result);
        Block lastHoldBlock = result.getLastHold();
        HashSet<LongBlocks> pieces = OrderLookup.reverseBlocks(resultBlocks, blocks.size()).stream()
                .map(StackOrder::toStream)
                .map(stream -> stream.map(block -> block != null ? block : lastHoldBlock))
                .map(LongBlocks::new)
                .collect(Collectors.toCollection(HashSet::new));

        assertThat(pieces).contains(new LongBlocks(blocks));

        // Check can build result
        Operations operations = parseToOperations(result);
        List<OperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, maxClearLine);
        boolean cansBuild = BuildUp.cansBuild(field, operationWithKeys, maxClearLine, reachable);
        assertThat(cansBuild).isTrue();
    }

    @Test
    void testGraceSystem() throws Exception {
        List<Pair<List<Block>, Boolean>> testCases = new ArrayList<Pair<List<Block>, Boolean>>() {
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
        for (Pair<List<Block>, Boolean> testCase : testCases) {
            // Set test case
            List<Block> blocks = testCase.getKey();
            Boolean expectedCheckFlag = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, blocks, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCheckFlag);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, blocks);
        }
    }

    @Test
    void testCaseFilledLine() throws Exception {
        List<Pair<List<Block>, Boolean>> testCases = new ArrayList<Pair<List<Block>, Boolean>>() {
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
        for (Pair<List<Block>, Boolean> testCase : testCases) {
            // Set test case
            List<Block> blocks = testCase.getKey();
            Boolean expectedCheckFlag = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, blocks, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCheckFlag);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, blocks);
        }
    }

    @Test
    @Tag("long")
    void testNoPossiblePerfect() throws Exception {
        // Field
        Field field = FieldFactory.createSmallField();
        int maxClearLine = 4;
        int maxDepth = 10;

        // Block
        URL noPerfect = ClassLoader.getSystemResource("orders/noperfect.txt");
        List<LongBlocks> testCases = Files.lines(Paths.get(noPerfect.toURI()))
                .map(BlockInterpreter::parse)
                .map(LongBlocks::new)
                .collect(Collectors.toList());
        Collections.shuffle(testCases);

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (LongBlocks pieces : testCases.subList(0, 10)) {
            // Set test case
            List<Block> blocks = pieces.getBlocks();

            // Execute
            boolean isSucceed = checker.check(field, blocks, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isFalse();

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, blocks);
        }
    }

    @Test
    @Tag("long")
    void testPossiblePerfect() throws Exception {
        // Field
        Field field = FieldFactory.createSmallField();
        int maxClearLine = 4;
        int maxDepth = 10;

        // Set to check No Possible Perfect
        URL noPerfect = ClassLoader.getSystemResource("orders/noperfect.txt");
        HashSet<LongBlocks> noPerfectSet = Files.lines(Paths.get(noPerfect.toURI()))
                .map(BlockInterpreter::parse)
                .map(LongBlocks::new)
                .collect(Collectors.toCollection(HashSet::new));

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        Randoms randoms = new Randoms();
        for (int count = 0; count < 100; count++) {
            // Set test case
            int cycle = randoms.nextIntClosed(0, 8);
            List<Block> blocks = randoms.block11InCycle(cycle);

            // Execute
            boolean isSucceed = checker.check(field, blocks, candidate, maxClearLine, maxDepth);
            boolean expectedFlag = !noPerfectSet.contains(new LongBlocks(blocks));
            assertThat(isSucceed).isEqualTo(expectedFlag);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, blocks);
        }
    }
}
