package searcher.checker;

import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import concurrent.checker.invoker.Pair;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import org.junit.Test;
import searcher.common.action.Action;
import searcher.common.validator.PerfectValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CheckerUsingHoldTest {
    @Test
    public void testGraceSystem() throws Exception {
        List<Pair<List<Block>, Boolean>> testCases = new ArrayList<Pair<List<Block>, Boolean>>(){
            {
                add(new Pair<>(Arrays.asList(T, S, O, J), false));
                add(new Pair<>(Arrays.asList(T, O, J, S), false));
                add(new Pair<>(Arrays.asList(T, T, L, J), true));
                add(new Pair<>(Arrays.asList(T, T, S, Z), true));
                add(new Pair<>(Arrays.asList(T, S, Z, T), true));
                add(new Pair<>(Arrays.asList(J, S, Z, L), false));
                add(new Pair<>(Arrays.asList(Z, I, O, T), false));
                add(new Pair<>(Arrays.asList(I, J, J, O), true));
                add(new Pair<>(Arrays.asList(T, S, Z, J), true));
                add(new Pair<>(Arrays.asList(L, S, Z, T), true));
            }
        };

        // Field
        String marks = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "";
        Field field = FieldFactory.createField(marks);
        int popCount = 4;
        int maxClearLine = 4;
        int maxDepth = 4;

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);

        // Measure
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

        for (Pair<List<Block>, Boolean> testCase : testCases) {
            List<Block> blocks = testCase.getKey();
            assertThat(checker.check(field, blocks, candidate, maxClearLine, maxDepth), is(testCase.getValue()));
        }
    }
}
