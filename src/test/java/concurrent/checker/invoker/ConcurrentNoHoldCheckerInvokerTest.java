package concurrent.checker.invoker;

import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.CheckerNoHoldThreadLocal;
import concurrent.checker.invoker.v2.ConcurrentNoHoldCheckerInvoker;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import misc.PiecesGenerator;
import misc.SafePieces;
import org.junit.Test;
import searcher.common.action.Action;
import tree.AnalyzeTree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConcurrentNoHoldCheckerInvokerTest {
    private AnalyzeTree runTestCase(String marks, PiecesGenerator piecesGenerator, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        Field field = FieldFactory.createField(marks);

        // 組み合わせの列挙
        List<List<Block>> searchingPieces = toBlocksList(piecesGenerator);

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CheckerNoHoldThreadLocal<Action> checkerThreadLocal = new CheckerNoHoldThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        ConcurrentNoHoldCheckerInvoker invoker = new ConcurrentNoHoldCheckerInvoker(executorService, candidateThreadLocal, checkerThreadLocal);

        List<Pair<List<Block>, Boolean>> resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // 結果を集計する
        AnalyzeTree tree = new AnalyzeTree();
        for (Pair<List<Block>, Boolean> resultPair : resultPairs) {
            List<Block> pieces = resultPair.getKey();
            Boolean result = resultPair.getValue();
            tree.set(result, pieces);
        }

        System.out.println(tree.show());

        return tree;
    }

    private List<List<Block>> toBlocksList(PiecesGenerator piecesGenerator) {
        List<List<Block>> searchingPieces = new ArrayList<>();
        for (SafePieces pieces : piecesGenerator)
            searchingPieces.add(pieces.getBlocks());
        return searchingPieces;
    }

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

        // 結果を集計する
        AnalyzeTree tree = runTestCase(marks, piecesGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent(), is(1730 / 5040.0));
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

        // 結果を集計する
        AnalyzeTree tree = runTestCase(marks, piecesGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent(), is(2380 / 5040.0));
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

        // 結果を集計する
        AnalyzeTree tree = runTestCase(marks, piecesGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent(), is(727 / 5040.0));
    }

    @Test
    public void testSearch4() throws Exception {
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p4");
        int maxClearLine = 3;
        int maxDepth = 3;

        String marks = "" +
                "XXX____XXX" +
                "XXX_____XX" +
                "XXXX___XXX" +
                "";

        AnalyzeTree tree = runTestCase(marks, piecesGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent(), is(120 / 840.0));
    }

    @Test
    public void testSearch5() throws Exception {
        PiecesGenerator piecesGenerator = new PiecesGenerator("J, I, Z, *p4");
        int maxClearLine = 4;
        int maxDepth = 6;

        String marks = "" +
                "________XX" +
                "XX_____XXX" +
                "XXX_____XX" +
                "XXX______X" +
                "";

        AnalyzeTree tree = runTestCase(marks, piecesGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent(), is(180 / 840.0));
    }

    @Test
    public void testSearch6() throws Exception {
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p5");
        int maxClearLine = 4;
        int maxDepth = 6;

        String marks = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "";

        AnalyzeTree tree = runTestCase(marks, piecesGenerator, maxClearLine, maxDepth);

        // Source: twitter by @???
        assertThat(tree.getSuccessPercent(), is(516 / 2520.0));
    }

    @Test
    public void testSearch7() throws Exception {
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p5");
        int maxClearLine = 4;
        int maxDepth = 6;

        String marks = "" +
                "XXXXX_____" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";

        AnalyzeTree tree = runTestCase(marks, piecesGenerator, maxClearLine, maxDepth);

        // Source: twitter by @???
        assertThat(tree.getSuccessPercent(), is(396 / 2520.0));
    }

    @Test
    public void testSearch8() throws Exception {
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p5");
        int maxClearLine = 4;
        int maxDepth = 6;

        String marks = "" +
                "XXXXXX____" +
                "XXXXX_____" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "";

        AnalyzeTree tree = runTestCase(marks, piecesGenerator, maxClearLine, maxDepth);

        // Source: twitter by @???
        assertThat(tree.getSuccessPercent(), is(609 / 2520.0));
    }

    @Test
    public void testSearch9() throws Exception {
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p5");
        int maxClearLine = 4;
        int maxDepth = 6;

        String marks = "" +
                "XXXXXX____" +
                "XXXXX_____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";

        AnalyzeTree tree = runTestCase(marks, piecesGenerator, maxClearLine, maxDepth);

        // Source: twitter by @???
        assertThat(tree.getSuccessPercent(), is(324 / 2520.0));
    }

    @Test
    public void testSearch10() throws Exception {
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p7");
        int maxClearLine = 4;
        int maxDepth = 6;

        // Field
        String marks = "" +
                "X________X" +
                "X________X" +
                "XX______XX" +
                "XXXXXX__XX" +
                "";

        AnalyzeTree tree = runTestCase(marks, piecesGenerator, maxClearLine, maxDepth);

        // Source: reply in twitter from @fullfool_14
        assertThat(tree.getSuccessPercent(), is(1439 / 5040.0));
    }

    @Test
    public void testCase11() throws Exception {
        // Invoker
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p4");
        int maxClearLine = 5;
        int maxDepth = 4;

        // Field
        String marks = "" +
                "____XXXXXX" +
                "____XXXXXX" +
                "___XXXXXXX" +
                "__XXXXXXXX" +
                "___XXXXXXX" +
                "";
        AnalyzeTree tree = runTestCase(marks, piecesGenerator, maxClearLine, maxDepth);

        // Source: reply in twitter from @fullfool_14
        assertThat(tree.getSuccessPercent(), is(477 / 2520.0));
    }

    @Test
    public void testCase12() throws Exception {
        // Invoker
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p7");
        int maxClearLine = 4;
        int maxDepth = 6;

        // Field
        String marks = "" +
                "X_________" +
                "X___X_____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";
        AnalyzeTree tree = runTestCase(marks, piecesGenerator, maxClearLine, maxDepth);

        // Source: reply in twitter from @fullfool_14
        assertThat(tree.getSuccessPercent(), is(727 / 5040.0));
    }

    @Test
    public void testCase13() throws Exception {
        // Invoker
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p7");
        int maxClearLine = 4;
        int maxDepth = 6;

        // Field
        String marks = "" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "";
        AnalyzeTree tree = runTestCase(marks, piecesGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent(), is(1902 / 5040.0));
    }
}