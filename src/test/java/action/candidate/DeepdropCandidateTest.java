package action.candidate;

import core.mino.Block;
import core.field.Field;
import core.srs.Rotate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.field.FieldFactory;
import org.junit.Test;
import searcher.common.action.Action;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeepdropCandidateTest {
    @Test
    public void testSearch1() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        DeepdropCandidate candidate = new DeepdropCandidate(minoFactory, minoShifter);

        String marks = "" +
                "__________" +
                "__________" +
                "____X_____";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.T, 4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Spawn).toArray().length, is(8));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Right).toArray().length, is(9));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Reverse).toArray().length, is(8));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Left).toArray().length, is(9));
    }

    @Test
    public void testSearch2() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        DeepdropCandidate candidate = new DeepdropCandidate(minoFactory, minoShifter);

        String marks = "" +
                "XXXX______" +
                "XXXXX_____" +
                "X___X_____" +
                "XX_XX_____";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.T, 4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Spawn).toArray().length, is(3));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Right).toArray().length, is(4));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Reverse).toArray().length, is(5));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Left).toArray().length, is(4));
    }
}