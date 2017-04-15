package searcher.checker;

import action.candidate.Candidate;
import action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import misc.PiecesGenerator;
import misc.SafePieces;
import org.junit.Test;
import searcher.common.action.Action;
import searcher.common.validator.PerfectValidator;
import tree.AnalyzeTree;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CheckerNoHoldCountTest {
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

        String show = tree.show();
        System.out.println(show);
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

        // 1902が真に正しいかは不明。デグレしていないことの確認
        assertThat(tree.getSuccessPercent(), is(1902 / 5040.0));
    }
}
