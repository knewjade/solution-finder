package core.action.reachable;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.*;
import core.srs.MinoRotation;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LockedReachableTest {
    @Test
    void checks1() throws Exception {
        LockedReachable reachable = createLockedReachable();

        String marks = "" +
                "XX________" +
                "X_________" +
                "X_XXXXXXXX" +
                "X_XXXXXXXX" +
                "X_XXXXXXXX";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.I, Rotate.Left), 1, 1, 8)).isFalse();
        assertThat(reachable.checks(field, new Mino(Block.I, Rotate.Right), 1, 2, 8)).isTrue();
    }

    private LockedReachable createLockedReachable() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        return new LockedReachable(minoFactory, minoShifter, minoRotation, 4);
    }

    @Test
    void checks2() throws Exception {
        LockedReachable reachable = createLockedReachable();

        String marks = "" +
                "XXX_______" +
                "XXX_______" +
                "XXX_XXXXXX" +
                "XXX____XXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.I, Rotate.Left), 4, 0, 8)).isTrue();
        assertThat(reachable.checks(field, new Mino(Block.I, Rotate.Right), 5, 0, 8)).isFalse();
    }

    @Test
    void checks3() throws Exception {
        LockedReachable reachable = createLockedReachable();

        String marks = "" +
                "XXXX______" +
                "XXX_______" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "XXX_XXXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.T, Rotate.Right), 3, 1, 8)).isTrue();
    }

    @Test
    void checks4false() throws Exception {
        LockedReachable reachable = createLockedReachable();

        String marks = "" +
                "__________" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "XXXX_XXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.S, Rotate.Left), 4, 1, 8)).isFalse();
    }

    @Test
    void checks4true() throws Exception {
        LockedReachable reachable = createLockedReachable();

        String marks = "" +
                "_____X____" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "XXXX_XXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.S, Rotate.Left), 4, 1, 8)).isTrue();
    }

    @Test
    void checks5false() throws Exception {
        LockedReachable reachable = createLockedReachable();

        String marks = "" +
                "__________" +
                "XXX_XXXXXX" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.L, Rotate.Right), 3, 1, 8)).isFalse();
    }

    @Test
    void checks5true() throws Exception {
        LockedReachable reachable = createLockedReachable();

        String marks = "" +
                "XXX_______" +
                "XX________" +
                "XX________" +
                "XXX_XXXXXX" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.L, Rotate.Right), 3, 1, 8)).isTrue();
    }

    @Test
    void checks6() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, 6);

        String marks = "" +
                "XX_XXXXXXX" +
                "X__XXXXXXX" +
                "___XXXXXXX" +
                "___XXXXXXX" +
                "__XXXXXXXX" +
                "_XXXXXXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(Block.T, Rotate.Right), 1, 2, 6)).isFalse();
    }
}