import core.mino.Block;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import main.CheckmateInvoker;
import misc.Stopwatch;
import concurrent.LockedCandidateThreadLocal;
import org.junit.Test;
import searcher.checkmate.ConcurrentCheckmate;
import searcher.common.action.Action;
import searcher.common.Result;
import searcher.common.ResultHelper;
import searcher.common.validator.PerfectValidator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CheckmateLongTest {
    @Test
    public void testLong9() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(I, S, Z, T, J, I, S, Z, S, Z);
        int maxClearLine = 4;
        CheckmateInvoker invoker = CheckmateInvoker.createPerfectCheckmate(maxClearLine);

        // Field
        String marks = "" +
                "__________" +
                "X_________" +
                "X_________" +
                "XX________" +
                "";
        Field field = FieldFactory.createField(marks);

        // Measure
        invoker.measure(field, blocks, 1);
        invoker.show(true);

        List<Result> results = invoker.getLastResults();
        assertThat(ResultHelper.uniquify(results).size(), is(29));
    }

    @Test
    public void testLong10() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(I, S, Z, T, J, I, S, Z, S, Z, T);
        int maxClearLine = 4;
        CheckmateInvoker invoker = CheckmateInvoker.createPerfectCheckmate(maxClearLine);

        // Field
        Field field = FieldFactory.createField(maxClearLine);

        // Measure
        invoker.measure(field, blocks, 1);
        invoker.show(true);

        List<Result> results = invoker.getLastResults();
        assertThat(ResultHelper.uniquify(results).size(), is(44));
    }

    @Test(timeout = 20000L)
    public void testLong10ForMultiThread() throws Exception {
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
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();
        List<Result> results = multiThread.search(field, blocks, maxClearLine, maxDepth);
        stopwatch.stop();

        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));

        assertThat(ResultHelper.uniquify(results).size(), is(44));
    }
}
