package concurrent.invoker;

import concurrent.CheckerNoHoldThreadLocal;
import concurrent.LockedCandidateThreadLocal;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import misc.PiecesGenerator;
import misc.SafePieces;
import misc.iterable.AllPermutationIterable;
import misc.iterable.CombinationIterable;
import org.junit.Ignore;
import org.junit.Test;
import searcher.common.action.Action;
import tree.AnalyzeTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConcurrentCheckerInvokerV2Test {
    @Test
    public void testSearch1() throws Exception {
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p7");
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
        for (SafePieces pieces : piecesGenerator)
            searchingPieces.add(pieces.getBlocks());

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CheckerNoHoldThreadLocal<Action> checkerThreadLocal = new CheckerNoHoldThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        ConcurrentCheckerInvokerV2 invoker = new ConcurrentCheckerInvokerV2(executorService, candidateThreadLocal, checkerThreadLocal);

        List<Pair<List<Block>, Boolean>> resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // 結果を集計する
        AnalyzeTree tree = new AnalyzeTree();
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
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p7");
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
        for (SafePieces pieces : piecesGenerator)
            searchingPieces.add(pieces.getBlocks());

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CheckerNoHoldThreadLocal<Action> checkerThreadLocal = new CheckerNoHoldThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        ConcurrentCheckerInvokerV2 invoker = new ConcurrentCheckerInvokerV2(executorService, candidateThreadLocal, checkerThreadLocal);

        List<Pair<List<Block>, Boolean>> resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // 結果を集計する
        AnalyzeTree tree = new AnalyzeTree();
        for (Pair<List<Block>, Boolean> resultPair : resultPairs) {
            List<Block> pieces = resultPair.getKey();
            Boolean result = resultPair.getValue();
            tree.set(result, pieces);
        }

        // 5034が真に正しいかは不明。デグレしていないことの確認
        assertThat(tree.getSuccessPercent(), is(5038 / 5040.0));
    }

    @Test
    public void testSearch3() throws Exception {
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p7");
        int maxClearLine = 4;
        int maxDepth = 7;

        String marks = "" +
                "X_________" +
                "X___X_____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";
        Field field = FieldFactory.createField(marks);

        // 組み合わせの列挙
        List<List<Block>> searchingPieces = new ArrayList<>();
        for (SafePieces pieces : piecesGenerator)
            searchingPieces.add(pieces.getBlocks());

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CheckerNoHoldThreadLocal<Action> checkerThreadLocal = new CheckerNoHoldThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        ConcurrentCheckerInvokerV2 invoker = new ConcurrentCheckerInvokerV2(executorService, candidateThreadLocal, checkerThreadLocal);

        List<Pair<List<Block>, Boolean>> resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // 結果を集計する
        AnalyzeTree tree = new AnalyzeTree();
        for (Pair<List<Block>, Boolean> resultPair : resultPairs) {
            List<Block> pieces = resultPair.getKey();
            Boolean result = resultPair.getValue();
            tree.set(result, pieces);
        }

        // 4736が真に正しいかは不明。デグレしていないことの確認
        assertThat(tree.getSuccessPercent(), is(4736 / 5040.0));
    }
}