package searcher.checkmate;

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

public class CheckmateUsingHoldTest {
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

    @Test
    public void testMultiPath() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(J, L, S, Z);
        int maxClearLine = 3;
        CheckmateInvoker invoker = CheckmateInvoker.createPerfectCheckmate(maxClearLine);

        // Field
        String marks = "" +
                "X________X" +
                "XX__XX__XX" +
                "XX__XX__XX" +
                "";
        Field field = FieldFactory.createField(marks);

        // Measure
        invoker.measure(field, blocks, 1);
        invoker.show(true);

        List<Result> results = invoker.getLastResults();
        assertThat(ResultHelper.uniquify(results).size(), is(4));
    }
}
