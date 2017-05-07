package searcher.checkmate;

import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import common.Stopwatch;
import org.junit.Test;
import common.datastore.Result;
import common.ResultHelper;
import common.datastore.action.Action;
import searcher.common.validator.PerfectValidator;

import java.util.*;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

public class CheckmateNoHoldReuseTest {
    @Test
    public void testCheckmateWhenSwappingJustBlock() throws Exception {
        // Invoker
        List<Block> blocks = new ArrayList<>(Arrays.asList(I, S, Z, T, J, L, O));
        int maxClearLine = 4;
        int maxDepth = 7;

        // Field
        String marks = "" +
                "XXX_______" +
                "XXX_______" +
                "XXX_______" +
                "XXX_______" +
                "";
        Field field = FieldFactory.createField(marks);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();

        CheckmateNoHold<Action> checkmate = new CheckmateNoHold<>(minoFactory, validator);
        CheckmateNoHoldReuse<Action> CheckmateReuse = new CheckmateNoHoldReuse<>(minoFactory, validator);
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

        Stopwatch stopwatchNoUse = Stopwatch.createStoppedStopwatch();
        Stopwatch stopwatchReuse = Stopwatch.createStoppedStopwatch();

        Random random = new Random();

        for (int count = 0; count < 500; count++) {
            int index = random.nextInt(blocks.size());
            Block pop = blocks.remove(index);
            blocks.add(pop);

            stopwatchNoUse.start();
            List<Result> result1 = checkmate.search(field, blocks, candidate, maxClearLine, maxDepth);
            stopwatchNoUse.stop();

            stopwatchReuse.start();
            List<Result> result2 = CheckmateReuse.search(field, blocks, candidate, maxClearLine, maxDepth);
            stopwatchReuse.stop();

            assertResult(ResultHelper.uniquify(result1), ResultHelper.uniquify(result2));
        }

        assertThat(stopwatchReuse.getNanoAverageTime(), is(lessThan(stopwatchNoUse.getNanoAverageTime())));
    }

    @Test
    public void testCheckmateWhenSwappingOverBlock() throws Exception {
        // Invoker
        List<Block> blocks = new ArrayList<>(Arrays.asList(I, S, Z, T, J, L, O));
        int maxClearLine = 4;
        int maxDepth = 6;

        // Field
        String marks = "" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "";
        Field field = FieldFactory.createField(marks);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();

        CheckmateNoHold<Action> checkmate = new CheckmateNoHold<>(minoFactory, validator);
        CheckmateNoHoldReuse<Action> CheckmateReuse = new CheckmateNoHoldReuse<>(minoFactory, validator);
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

        Stopwatch stopwatchNoUse = Stopwatch.createStoppedStopwatch();
        Stopwatch stopwatchReuse = Stopwatch.createStoppedStopwatch();

        Random random = new Random();

        for (int count = 0; count < 500; count++) {
            int index = random.nextInt(blocks.size());
            Block pop = blocks.remove(index);
            blocks.add(pop);

            stopwatchNoUse.start();
            List<Result> result1 = checkmate.search(field, blocks, candidate, maxClearLine, maxDepth);
            stopwatchNoUse.stop();

            stopwatchReuse.start();
            List<Result> result2 = CheckmateReuse.search(field, blocks, candidate, maxClearLine, maxDepth);
            stopwatchReuse.stop();

            assertResult(ResultHelper.uniquify(result1), ResultHelper.uniquify(result2));
        }

        assertThat(stopwatchReuse.getNanoAverageTime(), is(lessThan(stopwatchNoUse.getNanoAverageTime())));
    }

    @Test
    public void testCheckmateWhenSameSwappingOverBlock() throws Exception {
        // Invoker
        List<Block> blocks = new ArrayList<>(Arrays.asList(I, S, Z, T, J, L, O));
        int maxClearLine = 4;
        int maxDepth = 6;

        // Field
        String marks = "" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "";
        Field field = FieldFactory.createField(marks);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();

        CheckmateNoHold<Action> checkmate = new CheckmateNoHold<>(minoFactory, validator);
        CheckmateNoHoldReuse<Action> CheckmateReuse = new CheckmateNoHoldReuse<>(minoFactory, validator);
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

        Stopwatch stopwatchNoUse = Stopwatch.createStoppedStopwatch();
        Stopwatch stopwatchReuse = Stopwatch.createStoppedStopwatch();

        Random random = new Random();

        for (int count = 0; count < 500; count++) {
            int index = random.nextInt(blocks.size());
            Block pop = blocks.remove(index);
            blocks.add(pop);

            stopwatchNoUse.start();
            List<Result> result1 = checkmate.search(field, blocks, candidate, maxClearLine, maxDepth);
            stopwatchNoUse.stop();

            stopwatchReuse.start();
            List<Result> result2 = CheckmateReuse.search(field, blocks, candidate, maxClearLine, maxDepth);
            List<Result> result3 = CheckmateReuse.search(field, blocks, candidate, maxClearLine, maxDepth);
            stopwatchReuse.stop();

            assertResult(ResultHelper.uniquify(result1), ResultHelper.uniquify(result2));
            assertResult(ResultHelper.uniquify(result1), ResultHelper.uniquify(result3));
        }

        assertThat(stopwatchReuse.getNanoAverageTime(), is(lessThan(stopwatchNoUse.getNanoAverageTime())));
    }

    @Test
    public void testCheckmateWhenSwappingOverMoreBlock() throws Exception {
        // Invoker
        List<Block> blocks = new ArrayList<>(Arrays.asList(I, S, Z, T, J, L, O, I, S, Z, T, J, L, O));
        int maxClearLine = 4;
        int maxDepth = 6;

        // Field
        String marks = "" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "";
        Field field = FieldFactory.createField(marks);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();

        CheckmateNoHold<Action> checkmate = new CheckmateNoHold<>(minoFactory, validator);
        CheckmateNoHoldReuse<Action> CheckmateReuse = new CheckmateNoHoldReuse<>(minoFactory, validator);
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

        Stopwatch stopwatchNoUse = Stopwatch.createStoppedStopwatch();
        Stopwatch stopwatchReuse = Stopwatch.createStoppedStopwatch();

        Random random = new Random();

        for (int count = 0; count < 500; count++) {
            int index = random.nextInt(blocks.size());
            Block pop = blocks.remove(index);
            blocks.add(pop);

            stopwatchNoUse.start();
            List<Result> result1 = checkmate.search(field, blocks, candidate, maxClearLine, maxDepth);
            stopwatchNoUse.stop();

            stopwatchReuse.start();
            List<Result> result2 = CheckmateReuse.search(field, blocks, candidate, maxClearLine, maxDepth);
            stopwatchReuse.stop();

            assertResult(ResultHelper.uniquify(result1), ResultHelper.uniquify(result2));
        }

        assertThat(stopwatchReuse.getNanoAverageTime(), is(lessThan(stopwatchNoUse.getNanoAverageTime())));
    }

    private void assertResult(List<Result> left, List<Result> right) {
        assertThat(left.size(), is(right.size()));

        HashSet<Result> commons = new HashSet<>(left);
        commons.retainAll(right);
        assertThat(commons.size(), is(right.size()));
    }
}
