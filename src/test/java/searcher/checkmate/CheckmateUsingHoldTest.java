package searcher.checkmate;

import common.datastore.Pair;
import core.mino.Block;
import core.field.Field;
import core.field.FieldFactory;
import _experimental.main.CheckmateInvoker;
import org.junit.Test;
import searcher.common.Result;
import searcher.common.ResultHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CheckmateUsingHoldTest {
    @Test
    public void testLong9() throws Exception {
        // Invoker
        List<Pair<List<Block>, Integer>> testCases = new ArrayList<Pair<List<Block>, Integer>>() {
            {
                add(new Pair<>(Arrays.asList(I, S, Z, T, J, I, S, Z, S, Z), 29));
                add(new Pair<>(Arrays.asList(T, S, L, I, Z, J, L, O, O, S), 49));
                add(new Pair<>(Arrays.asList(L, Z, S, J, Z, Z, Z, I, T, T), 40));
                // add(new Pair<>(Arrays.asList(T, T, S, S, Z, Z, L, L, J, J), 106)); myself: 107　TODO: 要調査
                add(new Pair<>(Arrays.asList(O, S, O, S, Z, L, Z, L, I, I), 3));
//                add(new Pair<>(Arrays.asList(J, I, T, O, L, S, I, T, Z, O), 112));  myself: 113　TODO: 要調査
                add(new Pair<>(Arrays.asList(S, T, J, L, O, O, T, S, L, L), 82));
                add(new Pair<>(Arrays.asList(S, T, J, L, O, O, T, S, L), 36));
//                add(new Pair<>(Arrays.asList(Z, S, T, I, O, J, L, Z, S), 16)); myself: 17　TODO: 要調査
                add(new Pair<>(Arrays.asList(S, T, J, L, O, O, T, S, L), 36));
            }
        };

        int maxClearLine = 4;
        CheckmateInvoker invoker = CheckmateInvoker.createPerfectCheckmateUsingHold(maxClearLine);

        // Field
        String marks = "" +
                "__________" +
                "X_________" +
                "X_________" +
                "XX________" +
                "";
        Field field = FieldFactory.createField(marks);

        // Measure
        for (Pair<List<Block>, Integer> testCase : testCases) {
            List<Block> blocks = testCase.getKey();
            invoker.measure(field, blocks, 1);
            invoker.show(false);

            List<Result> results = invoker.getLastResults();
            Integer expectBlock = testCase.getValue();
            assertThat(ResultHelper.uniquify(results).size(), is(expectBlock));
        }
    }

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
        CheckmateInvoker invoker = CheckmateInvoker.createPerfectCheckmateUsingHold(maxClearLine);

        // Field
        Field field = FieldFactory.createField(maxClearLine);

        // Measure
        for (Pair<List<Block>, Integer> testCase : testCases) {
            List<Block> blocks = testCase.getKey();
            invoker.measure(field, blocks, 1);
            invoker.show(false);

            List<Result> results = invoker.getLastResults();
            Integer expectBlock = testCase.getValue();
            assertThat(ResultHelper.uniquify(results).size(), is(expectBlock));
        }
    }

    @Test
    public void testMultiPath1() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(J, L, S, Z);
        int maxClearLine = 3;
        CheckmateInvoker invoker = CheckmateInvoker.createPerfectCheckmateUsingHold(maxClearLine);

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

    @Test
    public void testMultiPath2() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(S, Z, O);
        int maxClearLine = 5;
        CheckmateInvoker invoker = CheckmateInvoker.createPerfectCheckmateUsingHold(maxClearLine);

        // Field
        String marks = "" +
                "X____XXXXX" +
                "XX__XXXXXX" +
                "XX__XXXXXX" +
                "XXXXXX__XX" +
                "XXXXXX__XX" +
                "";
        Field field = FieldFactory.createField(marks);

        // Measure
        invoker.measure(field, blocks, 1);
        invoker.show(true);

        List<Result> results = invoker.getLastResults();
        assertThat(ResultHelper.uniquify(results).size(), is(1));
    }

    @Test
    public void test() throws Exception {
        int maxClearLine = 4;
        CheckmateInvoker invoker = CheckmateInvoker.createPerfectCheckmateUsingHold(maxClearLine);

        // Field
        Field field = FieldFactory.createField(maxClearLine);

        // Measure
        List<Block> blocks = Arrays.asList(I, O, O, T, T, L, J, S, Z, Z);
        invoker.measure(field, blocks, 1);
        invoker.show(false);

        List<Result> results = invoker.getLastResults();
        for (Result result : results) {
            System.out.println(result);
        }
    }
}
