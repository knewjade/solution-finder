package searcher.checkmate;

import concurrent.LockedCandidateThreadLocal;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import main.CheckmateInvoker;
import misc.Stopwatch;
import org.junit.Test;
import searcher.common.Result;
import searcher.common.ResultHelper;
import searcher.common.action.Action;
import searcher.common.validator.PerfectValidator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConcurrentCheckmateTest {
    @Test(timeout = 20000L)
    public void testLong10() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(I, S, Z, T, J, I, S, Z, S, Z, T);
        int maxClearLine = 4;
        int maxDepth = 10;

        LockedCandidateThreadLocal threadLocal = new LockedCandidateThreadLocal(maxClearLine);
        MinoFactory minoFactory = new MinoFactory();
        PerfectValidator validator = new PerfectValidator();
        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);

        ConcurrentCheckmate<Action> multiThread = new ConcurrentCheckmate<>(threadLocal, minoFactory, validator, executorService);

        // Field
        Field field = FieldFactory.createField(maxClearLine);

        // Measure
        List<Result> results = multiThread.search(field, blocks, maxClearLine, maxDepth);

        assertThat(ResultHelper.uniquify(results).size(), is(44));
    }
}
