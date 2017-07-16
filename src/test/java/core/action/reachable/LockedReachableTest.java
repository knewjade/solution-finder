package core.action.reachable;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.*;
import core.srs.MinoRotation;
import core.srs.Rotate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static core.mino.Block.*;
import static core.srs.Rotate.*;
import static org.assertj.core.api.Assertions.assertThat;

class LockedReachableTest {
    private final LockedReachable reachable = createLockedReachable();

    private LockedReachable createLockedReachable() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        return new LockedReachable(minoFactory, minoShifter, minoRotation, 8);
    }

    private void success(String marks, Block block, Rotate rotate, int x, int y) {
        Field field = FieldFactory.createField(marks);
        Mino mino = new Mino(block, rotate);
        assert field.canPutMino(mino, x, y);
        assertThat(reachable.checks(field, mino, x, y, 8)).isTrue();
    }

    private void fail(String marks, Block block, Rotate rotate, int x, int y) {
        Field field = FieldFactory.createField(marks);
        Mino mino = new Mino(block, rotate);
        assert field.canPutMino(mino, x, y);
        assertThat(reachable.checks(field, mino, x, y, 8)).isFalse();
    }

    @Nested
    class WithI {
        @Test
        void checks1ok1() throws Exception {
            String marks = "" +
                    "XX________" +
                    "X_________" +
                    "X_XXXXXXXX" +
                    "X_XXXXXXXX" +
                    "X_XXXXXXXX";
            fail(marks, I, Left, 1, 1);
            success(marks, I, Right, 1, 2);
        }

        @Test
        void checks1ng1() throws Exception {
            String marks = "" +
                    "XX________" +
                    "X_________" +
                    "X_X_XXXXXX" +
                    "X_X_XXXXXX" +
                    "X_XXXXXXXX";
            fail(marks, I, Left, 1, 1);
            fail(marks, I, Right, 1, 2);
        }

        @Test
        void checks1ng2() throws Exception {
            String marks = "" +
                    "XX________" +
                    "X_________" +
                    "X_XX_XXXXX" +
                    "X_XX_XXXXX" +
                    "X_XXXXXXXX";
            fail(marks, I, Left, 1, 1);
            fail(marks, I, Right, 1, 2);
        }

        @Test
        void checks2ok1() throws Exception {
            String marks = "" +
                    "________XX" +
                    "_________X" +
                    "XXXXXXXX_X" +
                    "XXXXXXXX_X" +
                    "XXXXXXXX_X";
            success(marks, I, Right, 8, 2);
            fail(marks, I, Left, 8, 1);
        }

        @Test
        void checks2ng1() throws Exception {
            String marks = "" +
                    "________XX" +
                    "_________X" +
                    "XXXXXXX__X" +
                    "XXXXXXXX_X" +
                    "XXXXXXXX_X";
            fail(marks, I, Right, 8, 2);
            fail(marks, I, Left, 8, 1);
        }

        @Test
        void checks2ng2() throws Exception {
            String marks = "" +
                    "________XX" +
                    "_________X" +
                    "XXXXX_XX_X" +
                    "XXXXXXXX_X" +
                    "XXXXXXXX_X";
            fail(marks, I, Right, 8, 2);
            fail(marks, I, Left, 8, 1);
        }

        @Test
        void checks3ok1() throws Exception {
            String marks = "" +
                    "XXX_______" +
                    "XXX_______" +
                    "XXX_XXXXXX" +
                    "XXX____XXX" +
                    "";
            success(marks, I, Reverse, 5, 0);
            fail(marks, I, Spawn, 4, 0);
        }

        @Test
        void checks3ng1() throws Exception {
            String marks = "" +
                    "__________" +
                    "XXX_______" +
                    "XXX_XXXXXX" +
                    "XXX____XXX" +
                    "";
            fail(marks, I, Reverse, 5, 0);
            fail(marks, I, Spawn, 4, 0);
        }

        @Test
        void checks3ok2() throws Exception {
            String marks = "" +
                    "__________" +
                    "XXX_______" +
                    "X____XXXXX" +
                    "XXX____XXX" +
                    "";
            success(marks, I, Reverse, 3, 1);
            fail(marks, I, Spawn, 2, 1);

            fail(marks, I, Reverse, 5, 0);
            fail(marks, I, Spawn, 4, 0);
        }

        @Test
        void checks3ok3() throws Exception {
            String marks = "" +
                    "X_________" +
                    "XXX___XXXX" +
                    "XXX_XXXXXX" +
                    "XXX____XXX" +
                    "";
            success(marks, I, Reverse, 5, 0);
            success(marks, I, Spawn, 4, 0);
        }

        @Test
        void checks3ok4() throws Exception {
            String marks = "" +
                    "__________" +
                    "XXX___XXXX" +
                    "XXX_XXXXXX" +
                    "XXX____XXX" +
                    "";
            fail(marks, I, Reverse, 5, 0);
            success(marks, I, Spawn, 4, 0);
        }

        @Test
        void checks4ok1() throws Exception {
            String marks = "" +
                    "_______XXX" +
                    "_______XXX" +
                    "XXXXXX_XXX" +
                    "XXX____XXX" +
                    "";
            success(marks, I, Reverse, 5, 0);
            fail(marks, I, Spawn, 4, 0);
        }

        @Test
        void checks4ok2() throws Exception {
            String marks = "" +
                    "__________" +
                    "_______XXX" +
                    "XXXXXX_XXX" +
                    "XXX____XXX" +
                    "";
            success(marks, I, Reverse, 5, 0);
            fail(marks, I, Spawn, 4, 0);
        }

        @Test
        void checks4ok3() throws Exception {
            String marks = "" +
                    "__________" +
                    "_______XXX" +
                    "XXXXX____X" +
                    "XXX____XXX" +
                    "";
            success(marks, I, Reverse, 7, 1);
            fail(marks, I, Spawn, 6, 1);

            fail(marks, I, Reverse, 5, 0);
            fail(marks, I, Spawn, 4, 0);
        }

        @Test
        void checks4ok4() throws Exception {
            String marks = "" +
                    "_______XXX" +
                    "XXXX___XXX" +
                    "XXXXXX_XXX" +
                    "XXX____XXX" +
                    "";
            success(marks, I, Reverse, 5, 0);
            success(marks, I, Spawn, 4, 0);
        }

        @Test
        void checks4ok5() throws Exception {
            String marks = "" +
                    "_________X" +
                    "XXXX___XXX" +
                    "XXXXXX_XXX" +
                    "XXX____XXX" +
                    "";
            success(marks, I, Reverse, 5, 0);
            success(marks, I, Spawn, 4, 0);
        }
    }

    @Nested
    class WithO {
        @Test
        void checks1ok1() throws Exception {
            String marks = "" +
                    "X__XXXXXXX" +
                    "X___XXXXXX" +
                    "XX__XXXXXX";
            success(marks, O, Spawn, 1, 1);
            fail(marks, O, Spawn, 2, 0);
            fail(marks, O, Right, 2, 1);
            fail(marks, O, Reverse, 3, 1);
            fail(marks, O, Left, 3, 0);
        }
    }


    @Test
    void checks3() throws Exception {
        String marks = "" +
                "XXXX______" +
                "XXX_______" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "XXX_XXXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(T, Right), 3, 1, 8)).isTrue();
    }

    @Test
    void checks4false() throws Exception {
        String marks = "" +
                "__________" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "XXXX_XXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(S, Left), 4, 1, 8)).isFalse();
    }

    @Test
    void checks4true() throws Exception {
        String marks = "" +
                "_____X____" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "XXXX_XXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(S, Left), 4, 1, 8)).isTrue();
    }

    @Test
    void checks5false() throws Exception {
        String marks = "" +
                "__________" +
                "XXX_XXXXXX" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(L, Right), 3, 1, 8)).isFalse();
    }

    @Test
    void checks5true() throws Exception {
        String marks = "" +
                "XXX_______" +
                "XX________" +
                "XX________" +
                "XXX_XXXXXX" +
                "XXX_XXXXXX" +
                "XXX__XXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(L, Right), 3, 1, 8)).isTrue();
    }

    @Test
    void checks6() throws Exception {
        String marks = "" +
                "XX_XXXXXXX" +
                "X__XXXXXXX" +
                "___XXXXXXX" +
                "___XXXXXXX" +
                "__XXXXXXXX" +
                "_XXXXXXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        assertThat(reachable.checks(field, new Mino(T, Right), 1, 2, 6)).isFalse();
    }
}