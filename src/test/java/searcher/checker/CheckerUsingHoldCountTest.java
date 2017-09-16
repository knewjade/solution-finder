package searcher.checker;

import common.datastore.action.Action;
import common.iterable.PermutationIterable;
import common.pattern.BlocksGenerator;
import common.tree.AnalyzeTree;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import searcher.common.validator.PerfectValidator;

import java.util.Arrays;
import java.util.List;

import static core.mino.Block.*;
import static org.assertj.core.api.Assertions.assertThat;

class CheckerUsingHoldCountTest {
    private AnalyzeTree runTestCase(List<Block> blocks, int popCount, int maxClearLine, int maxDepth, String marks) {
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
        return tree;
    }

    @Test
    void testGraceSystem() throws Exception {
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

        AnalyzeTree tree = runTestCase(blocks, popCount, maxClearLine, maxDepth, marks);

        // Source: Nilgiri: https://docs.google.com/spreadsheets/d/1bVY3t_X96xRmUL0qdgB9tViSIGenu6RMKX4RW7qWg8Y/edit#gid=0
        assertThat(tree.getSuccessPercent()).isEqualTo(744 / 840.0);
    }

    @Test
    void testTemplate() throws Exception {
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

        // Source: Nilgiri: https://docs.google.com/spreadsheets/d/1bVY3t_X96xRmUL0qdgB9tViSIGenu6RMKX4RW7qWg8Y/edit#gid=0
        assertThat(tree.getSuccessPercent()).isEqualTo(514 / 840.0);
    }

    @Test
    void testTemplateWithHoldI() throws Exception {
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

        BlocksGenerator generator = new BlocksGenerator(pattern);
        generator.blocksStream()
                .forEach(blocks -> {
                    List<Block> blockList = blocks.getBlocks();
                    boolean result = checker.check(field, blockList, candidate, maxClearLine, maxDepth);
                    tree.set(result, blockList);
                });

        // Source: Nilgiri: https://docs.google.com/spreadsheets/d/1bVY3t_X96xRmUL0qdgB9tViSIGenu6RMKX4RW7qWg8Y/edit#gid=0
        assertThat(tree.getSuccessPercent()).isEqualTo(711 / 840.0);
    }

    @Test
    @Tag("long")
    void testAfter4Line() throws Exception {
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

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent()).isEqualTo(5040 / 5040.0);
    }

    @Test
    @Tag("long")
    void testBT4_5() throws Exception {
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

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent()).isEqualTo(5038 / 5040.0);
    }
}
