package core.action.reachable;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.*;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static core.mino.Piece.*;
import static core.srs.Rotate.*;
import static org.assertj.core.api.Assertions.assertThat;

class RotateReachableTest {
    private final RotateReachable reachable = createRotateReachable();

    private RotateReachable createRotateReachable() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        return new RotateReachable(minoFactory, minoShifter, minoRotation, 8);
    }

    private void success(String marks, Piece piece, Rotate rotate, int x, int y) {
        Field field = FieldFactory.createField(marks);
        Mino mino = new Mino(piece, rotate);
        assert field.canPut(mino, x, y);
        assertThat(reachable.checks(field, mino, x, y, 8)).isTrue();
    }

    private void fail(String marks, Piece piece, Rotate rotate, int x, int y) {
        Field field = FieldFactory.createField(marks);
        Mino mino = new Mino(piece, rotate);
        assert field.canPut(mino, x, y);
        assertThat(reachable.checks(field, mino, x, y, 8)).isFalse();
    }

    @Nested
    class WithI {
        @Test
        void checks1ok1() {
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
        void checks1ng1() {
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
        void checks1ng2() {
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
        void checks2ok1() {
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
        void checks2ng1() {
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
        void checks2ng2() {
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
        void checks3ok1() {
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
        void checks3ng1() {
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
        void checks3ok2() {
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
        void checks3ok3() {
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
        void checks3ok4() {
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
        void checks4ok1() {
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
        void checks4ok2() {
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
        void checks4ok3() {
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
        void checks4ok4() {
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
        void checks4ok5() {
            String marks = "" +
                    "_________X" +
                    "XXXX___XXX" +
                    "XXXXXX_XXX" +
                    "XXX____XXX" +
                    "";
            success(marks, I, Reverse, 5, 0);
            success(marks, I, Spawn, 4, 0);
        }

        @Test
        void checksEmpty() {
            String marks = "" +
                    "__________" +
                    "__________" +
                    "__________" +
                    "__________" +
                    "";
            success(marks, I, Right, 0, 2);
            success(marks, I, Right, 9, 2);

            success(marks, I, Left, 0, 1);
            success(marks, I, Left, 9, 1);

            fail(marks, I, Reverse, 5, 0);
            fail(marks, I, Spawn, 4, 0);
        }
    }

    @Nested
    class WithO {
        @Test
        void checks1ok1() {
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

    @Nested
    class WithS {
        @Test
        void checks1ok1() {
            String marks = "" +
                    "XX__XXXXXX" +
                    "X__XXXXXXX";
            success(marks, S, Reverse, 2, 1);
            fail(marks, S, Spawn, 2, 0);
        }

        @Test
        void checks2ok1() {
            String marks = "" +
                    "XX__XXXXXX" +
                    "XX__XXXXXX" +
                    "X__XXXXXXX";
            success(marks, S, Reverse, 2, 1);
            fail(marks, S, Spawn, 2, 0);
        }

        @Test
        void checks2ng1() {
            String marks = "" +
                    "XX________" +
                    "XX__XXXXXX" +
                    "X__XXXXXXX";
            fail(marks, S, Reverse, 2, 1);
            fail(marks, S, Spawn, 2, 0);
        }

        @Test
        void checks3ok1() {
            String marks = "" +
                    "___X______" +
                    "X_XXXXXXXX" +
                    "X__XXXXXXX" +
                    "XX_XXXXXXX";
            success(marks, S, Left, 2, 1);
            fail(marks, S, Right, 1, 1);
        }

        @Test
        void checks3ng1() {
            String marks = "" +
                    "__________" +
                    "X_XXXXXXXX" +
                    "X__XXXXXXX" +
                    "XX_XXXXXXX";
            fail(marks, S, Left, 2, 1);
            fail(marks, S, Right, 1, 1);
        }

        @Test
        void checks4ok1() {
            String marks = "" +
                    "XX________" +
                    "X_________" +
                    "X_XXXXXXXX" +
                    "X__XXXXXXX" +
                    "XX_XXXXXXX";
            success(marks, S, Right, 1, 1);
            fail(marks, S, Left, 2, 1);
        }

        @Test
        void checks4ok2() {
            String marks = "" +
                    "XX________" +
                    "X_________" +
                    "X_________" +
                    "X_XXXXXXXX" +
                    "X__XXXXXXX" +
                    "XX_XXXXXXX";
            success(marks, S, Right, 1, 1);
            fail(marks, S, Left, 2, 1);
        }

        @Test
        void checks4ok3() {
            String marks = "" +
                    "X_________" +
                    "__________" +
                    "X_XXXXXXXX" +
                    "X__XXXXXXX" +
                    "XX_XXXXXXX";
            success(marks, S, Right, 1, 1);
            fail(marks, S, Left, 2, 1);
        }

        @Test
        void checks4ng1() {
            String marks = "" +
                    "X_________" +
                    "__________" +
                    "__________" +
                    "X_XXXXXXXX" +
                    "X__XXXXXXX" +
                    "XX_XXXXXXX";
            fail(marks, S, Right, 1, 1);
            fail(marks, S, Left, 2, 1);
        }

        @Test
        void checksEmpty() {
            String marks = "" +
                    "__________" +
                    "__________" +
                    "__________" +
                    "__________" +
                    "";

            success(marks, S, Right, 0, 1);
            success(marks, S, Right, 8, 1);

            success(marks, S, Left, 1, 1);
            success(marks, S, Left, 9, 1);

            fail(marks, S, Reverse, 5, 0);
            fail(marks, S, Spawn, 4, 0);
        }
    }

    @Nested
    class WithZ {
        @Test
        void checks1ok1() {
            String marks = "" +
                    "X__XXXXXXX" +
                    "XX__XXXXXX";
            success(marks, Z, Reverse, 2, 1);
            fail(marks, Z, Spawn, 2, 0);
        }

        @Test
        void checks2ok1() {
            String marks = "" +
                    "X__XXXXXXX" +
                    "X__XXXXXXX" +
                    "XX__XXXXXX";
            success(marks, Z, Reverse, 2, 1);
            fail(marks, Z, Spawn, 2, 0);
        }

        @Test
        void checks2ng1() {
            String marks = "" +
                    "___XXXXXXX" +
                    "X__XXXXXXX" +
                    "XX__XXXXXX";
            fail(marks, Z, Reverse, 2, 1);
            fail(marks, Z, Spawn, 2, 0);
        }

        @Test
        void checks3ok1() {
            String marks = "" +
                    "__X_______" +
                    "XXXX_XXXXX" +
                    "XXX__XXXXX" +
                    "XXX_XXXXXX";
            success(marks, Z, Right, 3, 1);
            fail(marks, Z, Left, 4, 1);
        }

        @Test
        void checks3ng1() {
            String marks = "" +
                    "__________" +
                    "XXXX_XXXXX" +
                    "XXX__XXXXX" +
                    "XXX_XXXXXX";
            fail(marks, Z, Right, 3, 1);
            fail(marks, Z, Left, 4, 1);
        }

        @Test
        void checks4ok1() {
            String marks = "" +
                    "____XX____" +
                    "_____X____" +
                    "XXXX_XXXXX" +
                    "XXX__XXXXX" +
                    "XXX_XXXXXX";
            success(marks, Z, Left, 4, 1);
            fail(marks, Z, Right, 3, 1);
        }

        @Test
        void checks4ok2() {
            String marks = "" +
                    "____XX____" +
                    "_____X____" +
                    "_____X____" +
                    "XXXX_XXXXX" +
                    "XXX__XXXXX" +
                    "XXX_XXXXXX";
            success(marks, Z, Left, 4, 1);
            fail(marks, Z, Right, 3, 1);
        }

        @Test
        void checks4ok3() {
            String marks = "" +
                    "_____XX___" +
                    "______X___" +
                    "XXXX_XXXXX" +
                    "XXX__XXXXX" +
                    "XXX_XXXXXX";
            success(marks, Z, Left, 4, 1);
            fail(marks, Z, Right, 3, 1);
        }

        @Test
        void checks4ng1() {
            String marks = "" +
                    "_____XX___" +
                    "______X___" +
                    "______X___" +
                    "XXXX_XXXXX" +
                    "XXX__XXXXX" +
                    "XXX_XXXXXX";
            fail(marks, Z, Left, 4, 1);
            fail(marks, Z, Right, 3, 1);
        }

        @Test
        void checksEmpty() {
            String marks = "" +
                    "__________" +
                    "__________" +
                    "__________" +
                    "__________" +
                    "";
            success(marks, Z, Right, 0, 1);
            success(marks, Z, Right, 8, 1);

            success(marks, Z, Left, 1, 1);
            success(marks, Z, Left, 9, 1);

            fail(marks, Z, Reverse, 5, 0);
            fail(marks, Z, Spawn, 4, 0);
        }
    }

    @Nested
    class WithL {
        @Test
        void checks1ok1() {
            String marks = "" +
                    "XXX_______" +
                    "XX________" +
                    "XX_X______";
            success(marks, L, Reverse, 3, 1);
        }

        @Test
        void checks2ok1() {
            String marks = "" +
                    "XXX__XXXXX" +
                    "XX___XXXXX" +
                    "XX_XXXXXXX";
            success(marks, L, Reverse, 3, 1);
        }

        @Test
        void checks2ng1() {
            String marks = "" +
                    "XXX___XXXX" +
                    "XX___XXXXX" +
                    "XX_XXXXXXX";
            fail(marks, L, Reverse, 3, 1);
        }

        @Test
        void checks3ok1() {
            String marks = "" +
                    "XXXX______" +
                    "XX________" +
                    "XX_XXXXXXX";
            success(marks, L, Reverse, 3, 1);
        }

        @Test
        void checks3ng1() {
            String marks = "" +
                    "XXX_______" +
                    "XX________" +
                    "XX_XXXXXXX";
            fail(marks, L, Reverse, 3, 1);
        }

        @Test
        void checks4ok1() {
            String marks = "" +
                    "____XXXXXX" +
                    "XX___XXXXX" +
                    "XX_XXXXXXX";
            success(marks, L, Reverse, 3, 1);
        }

        @Test
        void checks4ng1() {
            String marks = "" +
                    "____XXXXXX" +
                    "X____XXXXX" +
                    "XX_XXXXXXX";
            fail(marks, L, Reverse, 3, 1);
        }

        @Test
        void checks5ok1() {
            String marks = "" +
                    "___XXXXXXX" +
                    "_____XXXXX" +
                    "XX_XXXXXXX";
            success(marks, L, Reverse, 3, 1);
        }

        @Test
        void checks5ng1() {
            String marks = "" +
                    "____XXXXXX" +
                    "_____XXXXX" +
                    "XX_XXXXXXX";
            fail(marks, L, Reverse, 3, 1);
        }

        @Test
        void checks6ok1() {
            String marks = "" +
                    "XXXX_XXXXX" +
                    "XX___XXXXX";
            success(marks, L, Spawn, 3, 0);
        }

        @Test
        void checks6ok2() {
            String marks = "" +
                    "XXX__XXXXX" +
                    "XX___XXXXX";
            success(marks, L, Spawn, 3, 0);
        }

        @Test
        void checks6ok3() {
            String marks = "" +
                    "XXXX__XXXX" +
                    "XX___XXXXX";
            success(marks, L, Spawn, 3, 0);
        }

        @Test
        void checks6ng1() {
            String marks = "" +
                    "XXX___XXXX" +
                    "XX___XXXXX";
            fail(marks, L, Spawn, 3, 0);
        }

        @Test
        void checks7ok1() {
            String marks = "" +
                    "____XXXXXX" +
                    "XX___XXXXX" +
                    "XX___XXXXX";
            success(marks, L, Spawn, 3, 0);
        }

        @Test
        void checks7ng1() {
            String marks = "" +
                    "____XXXXXX" +
                    "X____XXXXX" +
                    "XX___XXXXX";
            fail(marks, L, Spawn, 3, 0);
        }

        @Test
        void checks8ok1() {
            String marks = "" +
                    "XXX_______" +
                    "XX________" +
                    "XX_XXXXXXX" +
                    "XX_XXXXXXX" +
                    "XX__XXXXXX";
            success(marks, L, Right, 2, 1);
        }

        @Test
        void checks8ok2() {
            String marks = "" +
                    "XXX_______" +
                    "XX________" +
                    "XX________" +
                    "XX_XXXXXXX" +
                    "XX_XXXXXXX" +
                    "XX__XXXXXX";
            success(marks, L, Right, 2, 1);
        }

        @Test
        void checks8ok3() {
            String marks = "" +
                    "XX________" +
                    "X_________" +
                    "XX_XXXXXXX" +
                    "XX_XXXXXXX" +
                    "XX__XXXXXX";
            success(marks, L, Right, 2, 1);
        }

        @Test
        void checks8ok4() {
            String marks = "" +
                    "XX________" +
                    "X_________" +
                    "X_________" +
                    "XX_XXXXXXX" +
                    "XX_XXXXXXX" +
                    "XX__XXXXXX";
            success(marks, L, Right, 2, 1);
        }

        @Test
        void checks8ng1() {
            String marks = "" +
                    "XX_XXXXXXX" +
                    "XX_XXXXXXX" +
                    "XX__XXXXXX";
            fail(marks, L, Right, 2, 1);
        }

        @Test
        void checksEmpty() {
            String marks = "" +
                    "__________" +
                    "__________" +
                    "__________" +
                    "__________" +
                    "";
            success(marks, L, Right, 0, 1);
            success(marks, L, Right, 8, 1);

            success(marks, L, Left, 1, 1);
            success(marks, L, Left, 9, 1);

            fail(marks, L, Reverse, 5, 0);
            fail(marks, L, Spawn, 4, 0);
        }
    }

    @Nested
    class WithJ {
        @Test
        void checks1ok1() {
            String marks = "" +
                    "_______XXX" +
                    "________XX" +
                    "______X_XX";
            success(marks, J, Reverse, 6, 1);
        }

        @Test
        void checks2ok1() {
            String marks = "" +
                    "XXXXX__XXX" +
                    "XXXXX___XX" +
                    "XXXXXXX_XX";
            success(marks, J, Reverse, 6, 1);
        }

        @Test
        void checks2ng1() {
            String marks = "" +
                    "XXXX___XXX" +
                    "XXXXX___XX" +
                    "XXXXXXX_XX";
            fail(marks, J, Reverse, 6, 1);
        }

        @Test
        void checks3ok1() {
            String marks = "" +
                    "______XXXX" +
                    "________XX" +
                    "XXXXXXX_XX";
            success(marks, J, Reverse, 6, 1);
        }

        @Test
        void checks3ng1() {
            String marks = "" +
                    "_______XXX" +
                    "________XX" +
                    "XXXXXXX_XX";
            fail(marks, J, Reverse, 6, 1);
        }

        @Test
        void checks4ok1() {
            String marks = "" +
                    "XXXXXX____" +
                    "XXXXX___XX" +
                    "XXXXXXX_XX";
            success(marks, J, Reverse, 6, 1);
        }

        @Test
        void checks4ng1() {
            String marks = "" +
                    "XXXXXX____" +
                    "XXXXX____X" +
                    "XXXXXXX_XX";
            fail(marks, J, Reverse, 6, 1);
        }

        @Test
        void checks5ok1() {
            String marks = "" +
                    "XXXXXXX___" +
                    "XXXXX_____" +
                    "XXXXXXX_XX";
            success(marks, J, Reverse, 6, 1);
        }

        @Test
        void checks5ng1() {
            String marks = "" +
                    "XXXXXX____" +
                    "XXXXX_____" +
                    "XXXXXXX_XX";
            fail(marks, J, Reverse, 6, 1);
        }

        @Test
        void checks6ok1() {
            String marks = "" +
                    "XXXXX_XXXX" +
                    "XXXXX___XX";
            success(marks, J, Spawn, 6, 0);
        }

        @Test
        void checks6ok2() {
            String marks = "" +
                    "XXXXX__XXX" +
                    "XXXXX___XX";
            success(marks, J, Spawn, 6, 0);
        }

        @Test
        void checks6ok3() {
            String marks = "" +
                    "XXXX__XXXX" +
                    "XXXXX___XX";
            success(marks, J, Spawn, 6, 0);
        }

        @Test
        void checks6ng1() {
            String marks = "" +
                    "XXXX___XXX" +
                    "XXXXX___XX";
            fail(marks, J, Spawn, 6, 0);
        }

        @Test
        void checks7ok1() {
            String marks = "" +
                    "XXXXXX____" +
                    "XXXXX___XX" +
                    "XXXXX___XX";
            success(marks, J, Spawn, 6, 0);
        }

        @Test
        void checks7ng1() {
            String marks = "" +
                    "XXXXXX____" +
                    "XXXXX____X" +
                    "XXXXX___XX";
            fail(marks, J, Spawn, 6, 0);
        }

        @Test
        void checks8ok1() {
            String marks = "" +
                    "_______XXX" +
                    "________XX" +
                    "XXXXXXX_XX" +
                    "XXXXXXX_XX" +
                    "XXXXXX__XX";
            success(marks, J, Left, 7, 1);
        }

        @Test
        void checks8ok2() {
            String marks = "" +
                    "_______XXX" +
                    "________XX" +
                    "________XX" +
                    "XXXXXXX_XX" +
                    "XXXXXXX_XX" +
                    "XXXXXX__XX";
            success(marks, J, Left, 7, 1);
        }

        @Test
        void checks8ok3() {
            String marks = "" +
                    "________XX" +
                    "_________X" +
                    "XXXXXXX_XX" +
                    "XXXXXXX_XX" +
                    "XXXXXX__XX";
            success(marks, J, Left, 7, 1);
        }

        @Test
        void checks8ok4() {
            String marks = "" +
                    "________XX" +
                    "_________X" +
                    "_________X" +
                    "XXXXXXX_XX" +
                    "XXXXXXX_XX" +
                    "XXXXXX__XX";
            success(marks, J, Left, 7, 1);
        }

        @Test
        void checks8ng1() {
            String marks = "" +
                    "XXXXXXX_XX" +
                    "XXXXXXX_XX" +
                    "XXXXXX__XX";
            fail(marks, J, Left, 7, 1);
        }

        @Test
        void checksEmpty() {
            String marks = "" +
                    "__________" +
                    "__________" +
                    "__________" +
                    "__________" +
                    "";
            success(marks, J, Right, 0, 1);
            success(marks, J, Right, 8, 1);

            success(marks, J, Left, 1, 1);
            success(marks, J, Left, 9, 1);

            fail(marks, J, Reverse, 5, 0);
            fail(marks, J, Spawn, 4, 0);
        }
    }

    @Nested
    class WithT {
        @Test
        void checks1ok1() {
            String marks = "" +
                    "XX________" +
                    "X_________" +
                    "X_________" +
                    "X_XXXXXXXX";
            success(marks, T, Right, 1, 1);
        }

        @Test
        void checks1ok2() {
            String marks = "" +
                    "________XX" +
                    "_________X" +
                    "_________X" +
                    "XXXXXXXX_X";
            success(marks, T, Left, 8, 1);
        }

        @Test
        void checks2ok1() {
            String marks = "" +
                    "_____XXXXX" +
                    "XXX__XXXXX" +
                    "XX___XXXXX";
            success(marks, T, Spawn, 3, 0);
        }

        @Test
        void checks2ok2() {
            String marks = "" +
                    "XX________" +
                    "XX__XXXXXX" +
                    "XX___XXXXX";
            success(marks, T, Spawn, 3, 0);
        }

        @Test
        void checks2ng1() {
            String marks = "" +
                    "_____XXXXX" +
                    "XXX___XXXX" +
                    "XX___XXXXX";
            fail(marks, T, Spawn, 3, 0);
        }

        @Test
        void checks2ng2() {
            String marks = "" +
                    "XX________" +
                    "X___XXXXXX" +
                    "XX___XXXXX";
            fail(marks, T, Spawn, 3, 0);
        }

        @Test
        void checks3ok1() {
            String marks = "" +
                    "____XXXXXX" +
                    "XX___XXXXX" +
                    "XXX_XXXXXX";
            success(marks, T, Reverse, 3, 1);
        }

        @Test
        void checks3ok2() {
            String marks = "" +
                    "XXX_______" +
                    "XX___XXXXX" +
                    "XXX_XXXXXX";
            success(marks, T, Reverse, 3, 1);
        }

        @Test
        void checks4ok1() {
            String marks = "" +
                    "XXXX______" +
                    "XXX_______" +
                    "XXX_XXXXXX" +
                    "XXX__XXXXX" +
                    "XXX_XXXXXX" +
                    "";
            success(marks, T, Right, 3, 1);
        }

        @Test
        void checks4ok2() {
            String marks = "" +
                    "___XX_____" +
                    "____X_____" +
                    "XXX_XXXXXX" +
                    "XX__XXXXXX" +
                    "XXX_XXXXXX" +
                    "";
            success(marks, T, Left, 3, 1);
        }

        @Test
        void checks4ng1() {
            String marks = "" +
                    "XXXX______" +
                    "XXX_______" +
                    "XXX_______" +
                    "XXX_XXXXXX" +
                    "XXX__XXXXX" +
                    "XXX_XXXXXX" +
                    "";
            fail(marks, T, Right, 3, 1);
        }

        @Test
        void checks4ng2() {
            String marks = "" +
                    "___XX_____" +
                    "____X_____" +
                    "____X_____" +
                    "XXX_XXXXXX" +
                    "XX__XXXXXX" +
                    "XXX_XXXXXX" +
                    "";
            fail(marks, T, Left, 3, 1);
        }

        @Test
        void checks4ng3() {
            String marks = "" +
                    "XXX_XXXXXX" +
                    "XXX__XXXXX" +
                    "XXX_XXXXXX" +
                    "";
            fail(marks, T, Right, 3, 1);
        }

        @Test
        void checks4ng4() {
            String marks = "" +
                    "XXX_XXXXXX" +
                    "XX__XXXXXX" +
                    "XXX_XXXXXX" +
                    "";
            fail(marks, T, Left, 3, 1);
        }

        @Test
        void checks4ng5() {
            String marks = "" +
                    "XXXX______" +
                    "XXX_______" +
                    "XXX__XXXXX" +
                    "XXX__XXXXX" +
                    "XXX_XXXXXX" +
                    "";
            fail(marks, T, Right, 3, 1);
        }

        @Test
        void checks4ng6() {
            String marks = "" +
                    "___XX_____" +
                    "____X_____" +
                    "XX__XXXXXX" +
                    "XX__XXXXXX" +
                    "XXX_XXXXXX" +
                    "";
            fail(marks, T, Left, 3, 1);
        }

        @Test
        void checks5ok1() {
            String marks = "" +
                    "__XXXX____" +
                    "_____X____" +
                    "____XXXXXX" +
                    "XXX__XXXXX" +
                    "XXX_XXXXXX" +
                    "";
            success(marks, T, Right, 3, 1);
        }

        @Test
        void checks5ok2() {
            String marks = "" +
                    "_XXXX_____" +
                    "_X________" +
                    "XXX_______" +
                    "XX__XXXXXX" +
                    "XXX_XXXXXX" +
                    "";
            success(marks, T, Left, 3, 1);
        }

        @Test
        void checks5ng1() {
            String marks = "" +
                    "___XXX____" +
                    "_____X____" +
                    "____XXXXXX" +
                    "XXX__XXXXX" +
                    "XXX_XXXXXX" +
                    "";
            fail(marks, T, Right, 3, 1);
        }

        @Test
        void checks5ng2() {
            String marks = "" +
                    "_XXX______" +
                    "_X________" +
                    "XXX_______" +
                    "XX__XXXXXX" +
                    "XXX_XXXXXX" +
                    "";
            fail(marks, T, Left, 3, 1);
        }

        @Test
        void checksEmpty() {
            String marks = "" +
                    "__________" +
                    "__________" +
                    "__________" +
                    "__________" +
                    "";
            success(marks, T, Right, 0, 1);
            success(marks, T, Right, 8, 1);

            success(marks, T, Left, 1, 1);
            success(marks, T, Left, 9, 1);

            success(marks, T, Reverse, 5, 1);
            fail(marks, T, Spawn, 4, 0);
        }
    }

    @Nested
    class Other {
        @Test
        void checksLimit() {
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
}