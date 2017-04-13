package action.reachable;

import action.candidate.Candidate;
import action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.*;
import core.srs.MinoRotation;
import core.srs.Rotate;
import org.junit.Test;
import searcher.common.action.Action;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class LockedReachableTest {
    @Test
    public void checks1() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "XX________" +
                "X_________" +
                "X_XXXXXXXX" +
                "X_XXXXXXXX" +
                "X_XXXXXXXX";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.I, Rotate.Left), 1, 1, 8), is(false));
        assertThat(reachable.checks(field, new Mino(Block.I, Rotate.Right), 1, 2, 8), is(true));
    }

    @Test
    public void checks2() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "XXX_______" +
                "XXX_______" +
                "XXX_XXXXXX" +
                "XXX____XXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.I, Rotate.Left), 4, 0, 8), is(true));
        assertThat(reachable.checks(field, new Mino(Block.I, Rotate.Right), 5, 0, 8), is(false));
    }

    @Test
    public void checks3() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "XXXX______" +
                "XXX_______" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "XXX_XXXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.T, Rotate.Right), 3, 1, 8), is(true));
    }

    @Test
    public void checks4false() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "__________" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "XXXX_XXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.S, Rotate.Left), 4, 1, 8), is(false));
    }

    @Test
    public void checks4true() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "_____X____" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "XXXX_XXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.S, Rotate.Left), 4, 1, 8), is(true));
    }

    @Test
    public void checks5false() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "__________" +
                "XXX_XXXXXX" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.L, Rotate.Right), 3, 1, 8), is(false));
    }

    @Test
    public void checks5true() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "XXX_______" +
                "XX________" +
                "XX________" +
                "XXX_XXXXXX" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.L, Rotate.Right), 3, 1, 8), is(true));
    }
}