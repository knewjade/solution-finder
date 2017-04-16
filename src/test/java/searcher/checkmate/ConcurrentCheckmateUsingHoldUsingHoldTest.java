package searcher.checkmate;

import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.invoker.Pair;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.Test;
import searcher.common.Result;
import searcher.common.ResultHelper;
import searcher.common.action.Action;
import searcher.common.validator.PerfectValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConcurrentCheckmateUsingHoldUsingHoldTest {
    @Test
    public void testLong10() throws Exception {
        // Invoker
        List<Pair<List<Block>, Integer>> testCases = new ArrayList<Pair<List<Block>, Integer>>() {
            {
                add(new Pair<>(Arrays.asList(I, S, Z, T, J, I, S, Z, S, Z, T), 44));
//                add(new Pair<>(Arrays.asList(S, Z, T, L, J, I, O, S, Z, T, L), 161)); myself: 163　TODO: 要調査
                add(new Pair<>(Arrays.asList(T, L, J, Z, S, O, O, T, J, L, I), 161));
                add(new Pair<>(Arrays.asList(T, L, J, Z, S, O, O, T, J, L, T), 121));
                add(new Pair<>(Arrays.asList(T, L, J, Z, S, O, O, T, J, L), 81));
            }
        };
        int maxClearLine = 4;
        int maxDepth = 10;

        LockedCandidateThreadLocal threadLocal = new LockedCandidateThreadLocal(maxClearLine);
        MinoFactory minoFactory = new MinoFactory();
        PerfectValidator validator = new PerfectValidator();
        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);

        ConcurrentCheckmateUsingHold<Action> multiThread = new ConcurrentCheckmateUsingHold<>(threadLocal, minoFactory, validator, executorService);

        // Field
        Field field = FieldFactory.createField(maxClearLine);

        // Measure
        for (Pair<List<Block>, Integer> testCase : testCases) {
            List<Block> blocks = testCase.getKey();
            List<Result> results = multiThread.search(field, blocks, maxClearLine, maxDepth);
            assertThat(ResultHelper.uniquify(results).size(), is(testCase.getValue()));
        }

        executorService.shutdown();
    }
}
