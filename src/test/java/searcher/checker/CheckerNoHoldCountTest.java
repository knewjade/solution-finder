package searcher.checker;

import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import misc.pattern.PiecesGenerator;
import misc.pieces.SafePieces;
import org.junit.Test;
import searcher.common.action.Action;
import searcher.common.validator.PerfectValidator;
import misc.tree.AnalyzeTree;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CheckerNoHoldCountTest {
    private AnalyzeTree runTestCase(PiecesGenerator piecesGenerator, int maxClearLine, int maxDepth, String marks) {
        Field field = FieldFactory.createField(marks);

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        CheckerNoHold<Action> checker = new CheckerNoHold<>(minoFactory, validator);

        // Measure
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        AnalyzeTree tree = new AnalyzeTree();

        for (SafePieces pieces : piecesGenerator) {
            List<Block> blocks = pieces.getBlocks();
            boolean result = checker.check(field, blocks, candidate, maxClearLine, maxDepth);
            tree.set(result, blocks);
        }

        return tree;
    }

    @Test
    public void testCase1() throws Exception {
        // Invoker
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

        AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);

        // Source: reply in twitter from @fullfool_14
        assertThat(tree.getSuccessPercent(), is(1439 / 5040.0));
    }

    @Test
    public void testCase2() throws Exception {
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
        AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);

        // Source: reply in twitter from @fullfool_14
        assertThat(tree.getSuccessPercent(), is(477 / 2520.0));
    }

    @Test
    public void testCase3() throws Exception {
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
        AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);

        // Source: reply in twitter from @fullfool_14
        assertThat(tree.getSuccessPercent(), is(727 / 5040.0));
    }

    @Test
    public void testCase4() throws Exception {
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
        AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent(), is(1902 / 5040.0));
    }
}
