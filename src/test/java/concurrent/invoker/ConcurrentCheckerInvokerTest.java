package concurrent.invoker;

import concurrent.CheckerThreadLocal;
import concurrent.LockedCandidateThreadLocal;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import misc.iterable.AllPermutationIterable;
import misc.iterable.CombinationIterable;
import org.junit.Test;
import searcher.common.action.Action;
import tree.CheckerTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConcurrentCheckerInvokerTest {
    @Test
    public void testSearch1() throws Exception {
        List<Block> usingBlocks = Arrays.asList(T, I, S, Z, J, L, O);
        int combinationPopCount = 7;
        int maxClearLine = 8;
        int maxDepth = 7;

        String marks = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXXXX__" +
                "XXXXXXXX__" +
                "";
        Field field = FieldFactory.createField(marks);


        // 組み合わせの列挙
        List<List<Block>> searchingPieces = new ArrayList<>();
        Iterable<List<Block>> combinationIterable = new CombinationIterable<>(usingBlocks, combinationPopCount);
        for (List<Block> combination : combinationIterable) {
            // 組み合わせから、順列を列挙
            Iterable<List<Block>> permutationIterable = new AllPermutationIterable<>(combination);
            for (List<Block> permutation : permutationIterable) {
                searchingPieces.add(permutation);
            }
        }

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CheckerThreadLocal<Action> checkerThreadLocal = new CheckerThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        ConcurrentCheckerInvoker invoker = new ConcurrentCheckerInvoker(executorService, candidateThreadLocal, checkerThreadLocal);

        List<Pair<List<Block>, Boolean>> resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // 結果を集計する
        CheckerTree tree = new CheckerTree();
        for (Pair<List<Block>, Boolean> resultPair : resultPairs) {
            List<Block> pieces = resultPair.getKey();
            Boolean result = resultPair.getValue();
            tree.set(result, pieces);
        }

        // 5032が真に正しいかは不明。デグレしていないことの確認
        assertThat(tree.getSuccessPercent(), is(5032 / 5040.0));
    }

    @Test
    public void testSearch2BT4_5() throws Exception {
        List<Block> usingBlocks = Arrays.asList(T, I, S, Z, J, L, O);
        int combinationPopCount = 7;
        int maxClearLine = 6;
        int maxDepth = 7;

        String marks = "" +
                "XX________" +
                "XX________" +
                "XXX______X" +
                "XXXXXXX__X" +
                "XXXXXX___X" +
                "XXXXXXX_XX" +
                "";
        Field field = FieldFactory.createField(marks);

        // 組み合わせの列挙
        List<List<Block>> searchingPieces = new ArrayList<>();
        Iterable<List<Block>> combinationIterable = new CombinationIterable<>(usingBlocks, combinationPopCount);
        for (List<Block> combination : combinationIterable) {
            // 組み合わせから、順列を列挙
            Iterable<List<Block>> permutationIterable = new AllPermutationIterable<>(combination);
            for (List<Block> permutation : permutationIterable) {
                searchingPieces.add(permutation);
            }
        }

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CheckerThreadLocal<Action> checkerThreadLocal = new CheckerThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        ConcurrentCheckerInvoker invoker = new ConcurrentCheckerInvoker(executorService, candidateThreadLocal, checkerThreadLocal);

        List<Pair<List<Block>, Boolean>> resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // 結果を集計する
        CheckerTree tree = new CheckerTree();
        for (Pair<List<Block>, Boolean> resultPair : resultPairs) {
            List<Block> pieces = resultPair.getKey();
            Boolean result = resultPair.getValue();
            tree.set(result, pieces);
        }

        // 5034が真に正しいかは不明。デグレしていないことの確認
        assertThat(tree.getSuccessPercent(), is(5034 / 5040.0));
    }
}