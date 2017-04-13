package action.candidate;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.PassedMinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;
import org.junit.Test;
import searcher.common.action.Action;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FixedPlaceLockedCandidateTest {
    @Test
    public void testSearch1() throws Exception {
        String fixedMarks = "" +
                "XXXXX_____" +
                "XXXXX_____" +
                "XXXXX_____" +
                "XXXXX_____" +
                "";
        Field fixed = FieldFactory.createField(fixedMarks);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new FixPlaceLockedCandidate(minoFactory, minoShifter, minoRotation, 4, fixed);

        String marks = "" +
                "__________" +
                "__________" +
                "____X_____" +
                "";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.T, 4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Spawn).toArray().length, is(4));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Right).toArray().length, is(5));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Reverse).toArray().length, is(4));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Left).toArray().length, is(4));
    }

    @Test
    public void testSearch2() throws Exception {
        String fixedMarks = "" +
                "XXXXX_____" +
                "XXXXX_____" +
                "__________" +
                "XXXXX_____" +
                "";
        Field fixed = FieldFactory.createField(fixedMarks);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new FixPlaceLockedCandidate(minoFactory, minoShifter, minoRotation, 4, fixed);

        String marks = "" +
                "__________" +
                "__________" +
                "____X_____" +
                "";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.T, 4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Spawn).toArray().length, is(2));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Right).toArray().length, is(1));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Reverse).toArray().length, is(1));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Left).toArray().length, is(1));
    }

    @Test
    public void testSearch3() throws Exception {
        String fixedMarks = "" +
                "__________" +
                "__________" +
                "_XX_______" +
                "__________" +
                "";
        Field fixed = FieldFactory.createField(fixedMarks);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new FixPlaceLockedCandidate(minoFactory, minoShifter, minoRotation, 4, fixed);

        String marks = "" +
                "X_________" +
                "XX__XXXXXX" +
                "X__XXXXXXX" +
                "X_XXXXXXXX";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.Z, 4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Spawn).toArray().length, is(0));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Right).toArray().length, is(1));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Reverse).toArray().length, is(0));
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Left).toArray().length, is(0));
    }
}