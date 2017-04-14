package searcher.checker;

import action.candidate.Candidate;
import action.candidate.LockedCandidate;
import misc.PiecesGenerator;
import misc.SafePieces;
import tree.AnalyzeTree;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import misc.iterable.PermutationIterable;
import org.junit.Test;
import searcher.common.action.Action;
import searcher.common.validator.PerfectValidator;

import java.util.Arrays;
import java.util.List;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CheckerUsingHoldCountTest {
    @Test
    public void testGraceSystem() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(I, T, S, Z, J, L, O);
        int popCount = 4;
        int maxClearLine = 4;
        int maxDepth = 4;

        // Field
        String marks = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "";
        Field field = FieldFactory.createField(marks);

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);

        // Measure
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        AnalyzeTree tree = new AnalyzeTree();

        Iterable<List<Block>> combinations = new PermutationIterable<>(blocks, popCount);
        for (List<Block> combination : combinations) {
            combination.add(0, Block.T);  // Hold分の追加
            boolean result = checker.check(field, combination, candidate, maxClearLine, maxDepth);
            tree.set(result, combination);
        }

        assertThat(tree.getSuccessPercent(), is(744 / 840.0));
    }

    @Test
    public void testTemplate() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(I, T, S, Z, J, L, O);
        int popCount = 4;
        int maxClearLine = 4;
        int maxDepth = 3;

        // Field
        String marks = "" +
                "XXXXX____X" +
                "XXXXXX___X" +
                "XXXXXXX__X" +
                "XXXXXX___X" +
                "";
        Field field = FieldFactory.createField(marks);

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);

        // Measure
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        AnalyzeTree tree = new AnalyzeTree();
        Iterable<List<Block>> combinations = new PermutationIterable<>(blocks, popCount);
        for (List<Block> combination : combinations) {
            boolean result = checker.check(field, combination, candidate, maxClearLine, maxDepth);
            tree.set(result, combination);
        }

        assertThat(tree.getSuccessPercent(), is(514 / 840.0));
    }

    @Test
    public void testTemplateWithHoldI() throws Exception {
        // Invoker
        String pattern = "I, *p4";
        int maxClearLine = 4;
        int maxDepth = 4;

        // Field
        String marks = "" +
                "XXXXX_____" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";
        Field field = FieldFactory.createField(marks);

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);

        // Measure
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        AnalyzeTree tree = new AnalyzeTree();
        
        Iterable<SafePieces> combinations = new PiecesGenerator(pattern);
        for (SafePieces pieces : combinations) {
            List<Block> blocks = pieces.getBlocks();
            boolean result = checker.check(field, blocks, candidate, maxClearLine, maxDepth);
            tree.set(result, blocks);
        }

        assertThat(tree.getSuccessPercent(), is(711 / 840.0));
    }

    @Test
    public void testAfter4Line() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(I, T, S, Z, J, L, O);
        int popCount = 7;
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
        CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);

        // Measure
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        AnalyzeTree tree = new AnalyzeTree();
        Iterable<List<Block>> combinations = new PermutationIterable<>(blocks, popCount);
        for (List<Block> combination : combinations) {
            boolean result = checker.check(field, combination, candidate, maxClearLine, maxDepth);
            tree.set(result, combination);
        }

        assertThat(tree.getSuccessPercent(), is(5040 / 5040.0));
    }

    @Test
    public void testBT4_5() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(I, T, S, Z, J, L, O);
        int popCount = 7;
        int maxClearLine = 6;
        int maxDepth = 7;

        // Field
        String marks = "" +
                "XX________" +
                "XX________" +
                "XXX______X" +
                "XXXXXXX__X" +
                "XXXXXX___X" +
                "XXXXXXX_XX" +
                "";
        Field field = FieldFactory.createField(marks);

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);

        // Measure
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        AnalyzeTree tree = new AnalyzeTree();
        Iterable<List<Block>> combinations = new PermutationIterable<>(blocks, popCount);
        for (List<Block> combination : combinations) {
            boolean result = checker.check(field, combination, candidate, maxClearLine, maxDepth);
            tree.set(result, combination);
        }

        // 5034が真に正しいかは不明。デグレしていないことの確認
        assertThat(tree.getSuccessPercent(), is(5038 / 5040.0));
    }
}
