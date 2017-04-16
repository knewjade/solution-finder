package searcher.checkmate;

import action.candidate.Candidate;
import action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import misc.Stopwatch;
import org.junit.Test;
import searcher.common.Result;
import searcher.common.ResultHelper;
import searcher.common.action.Action;
import searcher.common.validator.PerfectValidator;

import java.util.*;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

public class CheckmateUsingHoldReuseTest {
    @Test
    public void testCheckmateWhenSwappingBlock() throws Exception {
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

        CheckmateUsingHold<Action> checkmate = new CheckmateUsingHold<>(minoFactory, minoShifter, validator);
        CheckmateUsingHoldReuse<Action> CheckmateReuse = new CheckmateUsingHoldReuse<>(validator, minoShifter, minoFactory);
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
