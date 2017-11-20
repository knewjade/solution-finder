package core.srs;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.Piece;
import core.mino.Mino;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MinoRotationTest {
    private final MinoRotation minoRotation = new MinoRotation();

    private int[] kicksLeft(String marks, Mino mino, int x, int y) {
        Field field = FieldFactory.createField(marks);
        assert field.canPut(mino, x, y);
        Mino after = new Mino(mino.getPiece(), mino.getRotate().getLeftRotate());
        return minoRotation.getKicksWithLeftRotation(field, mino, after, x, y);
    }

    private int[] kicksRight(String marks, Mino mino, int x, int y) {
        Field field = FieldFactory.createField(marks);
        assert field.canPut(mino, x, y);
        Mino after = new Mino(mino.getPiece(), mino.getRotate().getRightRotate());
        return minoRotation.getKicksWithRightRotation(field, mino, after, x, y);
    }

    @Nested
    class Kicks {
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
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Spawn), 2, 3)).containsExactly(-1, -1);
            }

            @Test
            void checks1ng1() throws Exception {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "X_X_XXXXXX" +
                        "X_X_XXXXXX" +
                        "X_XXXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Spawn), 2, 3)).containsExactly(1, 0);
            }

            @Test
            void checks1ng2() throws Exception {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "X_XX_XXXXX" +
                        "X_XX_XXXXX" +
                        "X_XXXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Spawn), 2, 3)).containsExactly(2, 0);
            }

            @Test
            void checks2ok1() throws Exception {
                String marks = "" +
                        "________XX" +
                        "_________X" +
                        "XXXXXXXX_X" +
                        "XXXXXXXX_X" +
                        "XXXXXXXX_X";
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Reverse), 7, 3)).containsExactly(1, -1);
            }

            @Test
            void checks2ng1() throws Exception {
                String marks = "" +
                        "________XX" +
                        "_________X" +
                        "XXXXXXX__X" +
                        "XXXXXXXX_X" +
                        "XXXXXXXX_X";
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Reverse), 7, 3)).containsExactly(0, 1);
            }

            @Test
            void checks2ng2() throws Exception {
                String marks = "" +
                        "________XX" +
                        "_________X" +
                        "XXXXX_XX_X" +
                        "XXXXXXXX_X" +
                        "XXXXXXXX_X";
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Reverse), 7, 3)).containsExactly(-2, 1);
            }

            @Test
            void checks3ok1() throws Exception {
                String marks = "" +
                        "XXX_______" +
                        "XXX_______" +
                        "XXX_XXXXXX" +
                        "XXX____XXX" +
                        "";
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Right), 3, 2)).containsExactly(2, -2);
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Left), 3, 1)).containsExactly(1, 1);
            }

            @Test
            void checks3ok2() throws Exception {
                String marks = "" +
                        "__________" +
                        "XXX_______" +
                        "X____XXXXX" +
                        "XXX____XXX" +
                        "";
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Right), 3, 2)).containsExactly(0, -1);
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Left), 3, 1)).containsExactly(1, 1);
            }

            @Test
            void checks3ok3() throws Exception {
                String marks = "" +
                        "X_________" +
                        "XXX___XXXX" +
                        "XXX_XXXXXX" +
                        "XXX____XXX" +
                        "";
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Right), 3, 2)).containsExactly(2, -2);
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Left), 3, 1)).containsExactly(1, -1);
            }

            @Test
            void checks3ok4() throws Exception {
                String marks = "" +
                        "__________" +
                        "XXX___XXXX" +
                        "XXX_XXXXXX" +
                        "XXX____XXX" +
                        "";
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Right), 3, 2)).containsExactly(-1, 1);
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Left), 3, 1)).containsExactly(1, -1);
            }

            @Test
            void checks4ok1() throws Exception {
                String marks = "" +
                        "_______XXX" +
                        "_______XXX" +
                        "XXXXXX_XXX" +
                        "XXX____XXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Left), 6, 1)).containsExactly(-1, -1);
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Right), 6, 2)).containsExactly(-2, 0);
            }

            @Test
            void checks4ok2() throws Exception {
                String marks = "" +
                        "__________" +
                        "_______XXX" +
                        "XXXXXX_XXX" +
                        "XXX____XXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Left), 6, 1)).containsExactly(-1, -1);
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Right), 6, 2)).containsExactly(-2, 0);
            }

            @Test
            void checks4ok3() throws Exception {
                String marks = "" +
                        "__________" +
                        "_______XXX" +
                        "XXXXX____X" +
                        "XXX____XXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Left), 6, 1)).containsExactly(1, 0);
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Right), 6, 2)).containsExactly(-2, 0);
            }

            @Test
            void checks4ok4() throws Exception {
                String marks = "" +
                        "_______XXX" +
                        "XXXX___XXX" +
                        "XXXXXX_XXX" +
                        "XXX____XXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Left), 6, 1)).containsExactly(-1, -1);
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Right), 6, 2)).containsExactly(-2, -2);
            }

            @Test
            void checks4ok5() throws Exception {
                String marks = "" +
                        "_________X" +
                        "XXXX___XXX" +
                        "XXXXXX_XXX" +
                        "XXX____XXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Left), 6, 1)).containsExactly(-1, -1);
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Right), 6, 2)).containsExactly(-2, -2);
            }

            @Test
            void checks5() throws Exception {
                String marks = "" +
                        "__________" +
                        "XXXXX_____" +
                        "XX________" +
                        "X_________" +
                        "";
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Spawn), 2, 0)).isNull();
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Spawn), 2, 0)).isNull();
            }
        }

        @Nested
        class WithO {
            @Test
            void checksRight() throws Exception {
                String marks = "" +
                        "X__XXXXXXX" +
                        "X___XXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.O, Rotate.Spawn), 1, 1)).containsExactly(0, 1);
                assertThat(kicksRight(marks, new Mino(Piece.O, Rotate.Right), 1, 2)).containsExactly(1, 0);
                assertThat(kicksRight(marks, new Mino(Piece.O, Rotate.Reverse), 2, 2)).containsExactly(0, -1);
                assertThat(kicksRight(marks, new Mino(Piece.O, Rotate.Left), 2, 1)).containsExactly(-1, 0);
            }

            @Test
            void checksLeft() throws Exception {
                String marks = "" +
                        "X__XXXXXXX" +
                        "X___XXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.O, Rotate.Spawn), 1, 1)).containsExactly(1, 0);
                assertThat(kicksLeft(marks, new Mino(Piece.O, Rotate.Left), 2, 1)).containsExactly(0, 1);
                assertThat(kicksLeft(marks, new Mino(Piece.O, Rotate.Reverse), 2, 2)).containsExactly(-1, 0);
                assertThat(kicksLeft(marks, new Mino(Piece.O, Rotate.Right), 1, 2)).containsExactly(0, -1);
            }
        }

        @Nested
        class WithS {
            @Test
            void checks1ok1() throws Exception {
                String marks = "" +
                        "XX__XXXXXX" +
                        "X__XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.S, Rotate.Right), 1, 2)).containsExactly(1, -1);
            }

            @Test
            void checks2ok1() throws Exception {
                String marks = "" +
                        "XX__XXXXXX" +
                        "XX__XXXXXX" +
                        "X__XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.S, Rotate.Left), 3, 2)).containsExactly(-1, -1);
            }

            @Test
            void checks2ng1() throws Exception {
                String marks = "" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "X__XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.S, Rotate.Left), 3, 2)).containsExactly(0, 0);
            }

            @Test
            void checks3ok1() throws Exception {
                String marks = "" +
                        "___X______" +
                        "X_XXXXXXXX" +
                        "X__XXXXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.S, Rotate.Spawn), 2, 3)).containsExactly(0, -2);
            }

            @Test
            void checks3ng1() throws Exception {
                String marks = "" +
                        "__________" +
                        "X_XXXXXXXX" +
                        "X__XXXXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.S, Rotate.Spawn), 2, 3)).containsExactly(1, 1);
            }

            @Test
            void checks4ok1() throws Exception {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "X_XXXXXXXX" +
                        "X__XXXXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.S, Rotate.Spawn), 2, 3)).containsExactly(-1, -2);
            }

            @Test
            void checks4ok2() throws Exception {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "X_________" +
                        "X_XXXXXXXX" +
                        "X__XXXXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.S, Rotate.Spawn), 2, 3)).containsExactly(-1, -2);
            }

            @Test
            void checks4ok3() throws Exception {
                String marks = "" +
                        "X_________" +
                        "__________" +
                        "X_XXXXXXXX" +
                        "X__XXXXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.S, Rotate.Spawn), 1, 3)).containsExactly(0, -2);
            }

            @Test
            void checks4ng1() throws Exception {
                String marks = "" +
                        "X_________" +
                        "__________" +
                        "__________" +
                        "X_XXXXXXXX" +
                        "X__XXXXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.S, Rotate.Spawn), 1, 3)).containsExactly(-1, 0);
            }

            @Test
            void checks5ng1() throws Exception {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "X_________";
                assertThat(kicksRight(marks, new Mino(Piece.S, Rotate.Spawn), 2, 0)).isNull();
                assertThat(kicksLeft(marks, new Mino(Piece.S, Rotate.Spawn), 2, 0)).containsExactly(1, 1);
            }
        }

        @Nested
        class WithZ {
            @Test
            void checks1ok1() throws Exception {
                String marks = "" +
                        "X__XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.Z, Rotate.Left), 3, 2)).containsExactly(-1, -1);
            }

            @Test
            void checks2ok1() throws Exception {
                String marks = "" +
                        "X__XXXXXXX" +
                        "X__XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.Z, Rotate.Right), 1, 2)).containsExactly(1, -1);
            }

            @Test
            void checks2ng1() throws Exception {
                String marks = "" +
                        "___XXXXXXX" +
                        "X__XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.Z, Rotate.Right), 1, 2)).containsExactly(0, 0);
            }

            @Test
            void checks3ok1() throws Exception {
                String marks = "" +
                        "__X_______" +
                        "XXXX_XXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.Z, Rotate.Spawn), 3, 3)).containsExactly(0, -2);
            }

            @Test
            void checks3ng1() throws Exception {
                String marks = "" +
                        "__________" +
                        "XXXX_XXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.Z, Rotate.Spawn), 3, 3)).containsExactly(-1, 1);
            }

            @Test
            void checks4ok1() throws Exception {
                String marks = "" +
                        "____XX____" +
                        "_____X____" +
                        "XXXX_XXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.Z, Rotate.Spawn), 3, 3)).containsExactly(1, -2);
            }

            @Test
            void checks4ok2() throws Exception {
                String marks = "" +
                        "____XX____" +
                        "_____X____" +
                        "_____X____" +
                        "XXXX_XXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.Z, Rotate.Spawn), 3, 3)).containsExactly(1, -2);
            }

            @Test
            void checks4ok3() throws Exception {
                String marks = "" +
                        "_____XX___" +
                        "______X___" +
                        "XXXX_XXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.Z, Rotate.Spawn), 4, 3)).containsExactly(0, -2);
            }

            @Test
            void checks4ng1() throws Exception {
                String marks = "" +
                        "_____XX___" +
                        "______X___" +
                        "______X___" +
                        "XXXX_XXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.Z, Rotate.Spawn), 4, 3)).containsExactly(1, 0);
            }

            @Test
            void checks5ng1() throws Exception {
                String marks = "" +
                        "________XX" +
                        "_________X" +
                        "_________X";
                assertThat(kicksLeft(marks, new Mino(Piece.Z, Rotate.Spawn), 7, 0)).isNull();
                assertThat(kicksRight(marks, new Mino(Piece.Z, Rotate.Spawn), 7, 0)).containsExactly(-1, 1);
            }
        }

        @Nested
        class WithL {
            @Test
            void checks1ok1() throws Exception {
                String marks = "" +
                        "XXX_______" +
                        "XX________" +
                        "XX_X______";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Left), 4, 1)).containsExactly(-1, 0);
            }

            @Test
            void checks2ok1() throws Exception {
                String marks = "" +
                        "XXX__XXXXX" +
                        "XX___XXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Left), 4, 2)).containsExactly(-1, -1);
            }

            @Test
            void checks2ng1() throws Exception {
                String marks = "" +
                        "XXX___XXXX" +
                        "XX___XXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Left), 4, 2)).containsExactly(0, 0);
            }

            @Test
            void checks3ok1() throws Exception {
                String marks = "" +
                        "XXXX______" +
                        "XX________" +
                        "XX_XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Left), 4, 2)).containsExactly(-1, -1);
            }

            @Test
            void checks3ng1() throws Exception {
                String marks = "" +
                        "XXX_______" +
                        "XX________" +
                        "XX_XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Left), 4, 2)).containsExactly(0, 0);
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Left), 3, 2)).containsExactly(0, 2);
            }

            @Test
            void checks4ok1() throws Exception {
                String marks = "" +
                        "____XXXXXX" +
                        "XX___XXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Right), 2, 2)).containsExactly(1, -1);
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Right), 3, 2)).containsExactly(0, 2);
            }

            @Test
            void checks4ng1() throws Exception {
                String marks = "" +
                        "____XXXXXX" +
                        "X____XXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Right), 2, 2)).containsExactly(0, 0);
            }

            @Test
            void checks5ok1() throws Exception {
                String marks = "" +
                        "___XXXXXXX" +
                        "_____XXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Right), 2, 2)).containsExactly(1, -1);
            }

            @Test
            void checks5ng1() throws Exception {
                String marks = "" +
                        "____XXXXXX" +
                        "_____XXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Right), 2, 2)).containsExactly(0, 0);
            }

            @Test
            void checks6ok1() throws Exception {
                String marks = "" +
                        "XXXX_XXXXX" +
                        "XX___XXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Left), 4, 1)).containsExactly(-1, -1);
            }

            @Test
            void checks6ok2() throws Exception {
                String marks = "" +
                        "XXX__XXXXX" +
                        "XX___XXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Left), 4, 1)).containsExactly(-1, -1);
            }

            @Test
            void checks6ok3() throws Exception {
                String marks = "" +
                        "XXXX__XXXX" +
                        "XX___XXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Left), 4, 1)).containsExactly(-1, -1);
            }

            @Test
            void checks6ng1() throws Exception {
                String marks = "" +
                        "XXX___XXXX" +
                        "XX___XXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Left), 4, 1)).containsExactly(0, 0);
            }

            @Test
            void checks7ok1() throws Exception {
                String marks = "" +
                        "____XXXXXX" +
                        "XX___XXXXX" +
                        "XX___XXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Right), 2, 1)).containsExactly(1, -1);
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Right), 3, 1)).containsExactly(0, 2);
            }

            @Test
            void checks7ng1() throws Exception {
                String marks = "" +
                        "____XXXXXX" +
                        "X____XXXXX" +
                        "XX___XXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Right), 2, 1)).containsExactly(0, 0);
            }

            @Test
            void checks8ok1() throws Exception {
                String marks = "" +
                        "XXX_______" +
                        "XX________" +
                        "XX_XXXXXXX" +
                        "XX_XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Spawn), 3, 3)).containsExactly(-1, -2);
            }

            @Test
            void checks8ok2() throws Exception {
                String marks = "" +
                        "XXX_______" +
                        "XX________" +
                        "XX________" +
                        "XX_XXXXXXX" +
                        "XX_XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Spawn), 3, 3)).containsExactly(-1, -2);
            }

            @Test
            void checks8ok3() throws Exception {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "XX_XXXXXXX" +
                        "XX_XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Spawn), 2, 3)).containsExactly(0, -2);
            }

            @Test
            void checks8ok4() throws Exception {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "X_________" +
                        "XX_XXXXXXX" +
                        "XX_XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Spawn), 2, 3)).containsExactly(0, -2);
            }

            @Test
            void checks8ng1() throws Exception {
                String marks = "" +
                        "XX_XXXXXXX" +
                        "XX_XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Spawn), 2, 3)).containsExactly(-1, 1);
            }

            @Test
            void checks9ng1() throws Exception {
                String marks = "" +
                        "________XX" +
                        "_________X" +
                        "_________X";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Spawn), 7, 0)).isNull();
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Spawn), 7, 0)).containsExactly(-1, 1);
            }
        }

        @Nested
        class WithJ {
            @Test
            void checks1ok1() throws Exception {
                String marks = "" +
                        "_______XXX" +
                        "________XX" +
                        "______X_XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Right), 5, 1)).containsExactly(1, 0);
            }

            @Test
            void checks2ok1() throws Exception {
                String marks = "" +
                        "XXXXX__XXX" +
                        "XXXXX___XX" +
                        "XXXXXXX_XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Right), 5, 2)).containsExactly(1, -1);
            }

            @Test
            void checks2ng1() throws Exception {
                String marks = "" +
                        "XXXX___XXX" +
                        "XXXXX___XX" +
                        "XXXXXXX_XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Right), 5, 2)).containsExactly(0, 0);
            }

            @Test
            void checks3ok1() throws Exception {
                String marks = "" +
                        "______XXXX" +
                        "________XX" +
                        "XXXXXXX_XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Right), 5, 2)).containsExactly(1, -1);
            }

            @Test
            void checks3ng1() throws Exception {
                String marks = "" +
                        "_______XXX" +
                        "________XX" +
                        "XXXXXXX_XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Right), 5, 2)).containsExactly(0, 0);
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Right), 6, 2)).containsExactly(0, 2);
            }

            @Test
            void checks4ok1() throws Exception {
                String marks = "" +
                        "XXXXXX____" +
                        "XXXXX___XX" +
                        "XXXXXXX_XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Left), 7, 2)).containsExactly(-1, -1);
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Left), 6, 2)).containsExactly(0, 2);
            }

            @Test
            void checks4ng1() throws Exception {
                String marks = "" +
                        "XXXXXX____" +
                        "XXXXX____X" +
                        "XXXXXXX_XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Left), 7, 2)).containsExactly(0, 0);
            }

            @Test
            void checks5ok1() throws Exception {
                String marks = "" +
                        "XXXXXXX___" +
                        "XXXXX_____" +
                        "XXXXXXX_XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Left), 7, 2)).containsExactly(-1, -1);
            }

            @Test
            void checks5ng1() throws Exception {
                String marks = "" +
                        "XXXXXX____" +
                        "XXXXX_____" +
                        "XXXXXXX_XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Left), 7, 2)).containsExactly(0, 0);
            }

            @Test
            void checks6ok1() throws Exception {
                String marks = "" +
                        "XXXXX_XXXX" +
                        "XXXXX___XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Right), 5, 1)).containsExactly(1, -1);
            }

            @Test
            void checks6ok2() throws Exception {
                String marks = "" +
                        "XXXXX__XXX" +
                        "XXXXX___XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Right), 5, 1)).containsExactly(1, -1);
            }

            @Test
            void checks6ok3() throws Exception {
                String marks = "" +
                        "XXXX__XXXX" +
                        "XXXXX___XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Right), 5, 1)).containsExactly(1, -1);
            }

            @Test
            void checks6ng1() throws Exception {
                String marks = "" +
                        "XXXX___XXX" +
                        "XXXXX___XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Right), 5, 1)).containsExactly(0, 0);
            }

            @Test
            void checks7ok1() throws Exception {
                String marks = "" +
                        "XXXXXX____" +
                        "XXXXX___XX" +
                        "XXXXX___XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Left), 7, 1)).containsExactly(-1, -1);
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Left), 6, 1)).containsExactly(0, 2);
            }

            @Test
            void checks7ng1() throws Exception {
                String marks = "" +
                        "XXXXXX____" +
                        "XXXXX____X" +
                        "XXXXX___XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Left), 7, 1)).containsExactly(0, 0);
            }

            @Test
            void checks8ok1() throws Exception {
                String marks = "" +
                        "_______XXX" +
                        "________XX" +
                        "XXXXXXX_XX" +
                        "XXXXXXX_XX" +
                        "XXXXXX__XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Spawn), 6, 3)).containsExactly(1, -2);
            }

            @Test
            void checks8ok2() throws Exception {
                String marks = "" +
                        "_______XXX" +
                        "________XX" +
                        "________XX" +
                        "XXXXXXX_XX" +
                        "XXXXXXX_XX" +
                        "XXXXXX__XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Spawn), 6, 3)).containsExactly(1, -2);
            }

            @Test
            void checks8ok3() throws Exception {
                String marks = "" +
                        "________XX" +
                        "_________X" +
                        "XXXXXXX_XX" +
                        "XXXXXXX_XX" +
                        "XXXXXX__XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Spawn), 7, 3)).containsExactly(0, -2);
            }

            @Test
            void checks8ok4() throws Exception {
                String marks = "" +
                        "________XX" +
                        "_________X" +
                        "_________X" +
                        "XXXXXXX_XX" +
                        "XXXXXXX_XX" +
                        "XXXXXX__XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Spawn), 7, 3)).containsExactly(0, -2);
            }

            @Test
            void checks8ng1() throws Exception {
                String marks = "" +
                        "XXXXXXX_XX" +
                        "XXXXXXX_XX" +
                        "XXXXXX__XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Spawn), 7, 3)).containsExactly(1, 1);
            }

            @Test
            void checks9ng1() throws Exception {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "X_________";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Spawn), 2, 0)).isNull();
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Spawn), 2, 0)).containsExactly(1, 1);
            }
        }

        @Nested
        class WithT {
            @Test
            void checks1ok1() throws Exception {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "X_________" +
                        "X_XXXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Spawn), 2, 1)).containsExactly(-1, 0);
            }

            @Test
            void checks1ok2() throws Exception {
                String marks = "" +
                        "________XX" +
                        "_________X" +
                        "_________X" +
                        "XXXXXXXX_X";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Spawn), 7, 1)).containsExactly(1, 0);
            }

            @Test
            void checks2ok1() throws Exception {
                String marks = "" +
                        "_____XXXXX" +
                        "XXX__XXXXX" +
                        "XX___XXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Left), 4, 1)).containsExactly(-1, -1);
            }

            @Test
            void checks2ok2() throws Exception {
                String marks = "" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "XX___XXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Right), 2, 1)).containsExactly(1, -1);
            }

            @Test
            void checks2ng1() throws Exception {
                String marks = "" +
                        "_____XXXXX" +
                        "XXX___XXXX" +
                        "XX___XXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Left), 4, 1)).containsExactly(0, 0);
            }

            @Test
            void checks2ng2() throws Exception {
                String marks = "" +
                        "XX________" +
                        "X___XXXXXX" +
                        "XX___XXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Right), 2, 1)).containsExactly(0, 0);
            }

            @Test
            void checks3ok1() throws Exception {
                String marks = "" +
                        "____XXXXXX" +
                        "XX___XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Left), 3, 1)).containsExactly(0, 0);
            }

            @Test
            void checks3ok2() throws Exception {
                String marks = "" +
                        "XXX_______" +
                        "XX___XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Right), 3, 1)).containsExactly(0, 0);
            }

            @Test
            void checks4ok1() throws Exception {
                String marks = "" +
                        "XXXX______" +
                        "XXX_______" +
                        "XXX_XXXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX" +
                        "";
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Spawn), 4, 3)).containsExactly(-1, -2);
            }

            @Test
            void checks4ok2() throws Exception {
                String marks = "" +
                        "___XX_____" +
                        "____X_____" +
                        "XXX_XXXXXX" +
                        "XX__XXXXXX" +
                        "XXX_XXXXXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Spawn), 2, 3)).containsExactly(1, -2);
            }

            @Test
            void checks4ng1() throws Exception {
                String marks = "" +
                        "XXXX______" +
                        "XXX_______" +
                        "XXX_______" +
                        "XXX_XXXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX" +
                        "";
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Spawn), 4, 3)).containsExactly(-1, 0);
            }

            @Test
            void checks4ng2() throws Exception {
                String marks = "" +
                        "___XX_____" +
                        "____X_____" +
                        "____X_____" +
                        "XXX_XXXXXX" +
                        "XX__XXXXXX" +
                        "XXX_XXXXXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Spawn), 2, 3)).containsExactly(1, 0);
            }

            @Test
            void checks4ng3() throws Exception {
                String marks = "" +
                        "XXX_XXXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Spawn), 3, 3)).containsExactly(0, 0);
            }

            @Test
            void checks4ng4() throws Exception {
                String marks = "" +
                        "XXX_XXXXXX" +
                        "XX__XXXXXX" +
                        "XXX_XXXXXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Spawn), 3, 3)).containsExactly(0, 0);
            }

            @Test
            void checks5ok1() throws Exception {
                String marks = "" +
                        "XXXX______" +
                        "XX________" +
                        "XXX_______" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Reverse), 3, 3)).containsExactly(0, -2);
            }

            @Test
            void checks5ok2() throws Exception {
                String marks = "" +
                        "______XXXX" +
                        "________XX" +
                        "_______XXX" +
                        "XXXXX__XXX" +
                        "XXXXXX_XXX" +
                        "";
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Reverse), 6, 3)).containsExactly(0, -2);
            }

            @Test
            void checks6ok1() throws Exception {
                String marks = "" +
                        "XXXXX_____" +
                        "XXX_______" +
                        "XXX_______" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Reverse), 4, 3)).containsExactly(-1, -2);
            }

            @Test
            void checks6ok2() throws Exception {
                String marks = "" +
                        "_____XXXXX" +
                        "_______XXX" +
                        "_______XXX" +
                        "XXXXX__XXX" +
                        "XXXXXX_XXX" +
                        "";
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Reverse), 5, 3)).containsExactly(1, -2);
            }

            @Test
            void checks7ok1() throws Exception {
                String marks = "" +
                        "XXXXX_____" +
                        "XX________" +
                        "XXX_______" +
                        "XX__XXXXXX" +
                        "XXX_XXXXXX" +
                        "";
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Reverse), 3, 3)).containsExactly(0, -2);
            }

            @Test
            void checks7ok2() throws Exception {
                String marks = "" +
                        "_____XXXXX" +
                        "________XX" +
                        "_______XXX" +
                        "XXXXXX__XX" +
                        "XXXXXX_XXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Reverse), 6, 3)).containsExactly(0, -2);
            }
        }
    }

    @Nested
    class Offset {
        @Nested
        class JLSTZ {
            private final List<Piece> pieces = Arrays.asList(Piece.J, Piece.L, Piece.S, Piece.T, Piece.Z);

            @Test
            void leftFromSpawn() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(piece, Rotate.Spawn));
                    assertThat(patterns).containsExactly(
                            new int[]{0, 0},
                            new int[]{1, 0},
                            new int[]{1, 1},
                            new int[]{0, -2},
                            new int[]{1, -2}
                    );
                }
            }

            @Test
            void rightFromSpawn() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(piece, Rotate.Spawn));
                    assertThat(patterns).containsExactly(
                            new int[]{0, 0},
                            new int[]{-1, 0},
                            new int[]{-1, 1},
                            new int[]{0, -2},
                            new int[]{-1, -2}
                    );
                }
            }

            @Test
            void leftFromLeft() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(piece, Rotate.Left));
                    assertThat(patterns).containsExactly(
                            new int[]{0, 0},
                            new int[]{-1, 0},
                            new int[]{-1, -1},
                            new int[]{0, 2},
                            new int[]{-1, 2}
                    );
                }
            }

            @Test
            void rightFromLeft() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(piece, Rotate.Left));
                    assertThat(patterns).containsExactly(
                            new int[]{0, 0},
                            new int[]{-1, 0},
                            new int[]{-1, -1},
                            new int[]{0, 2},
                            new int[]{-1, 2}
                    );
                }
            }

            @Test
            void leftFromReverse() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(piece, Rotate.Reverse));
                    assertThat(patterns).containsExactly(
                            new int[]{0, 0},
                            new int[]{-1, 0},
                            new int[]{-1, 1},
                            new int[]{0, -2},
                            new int[]{-1, -2}
                    );
                }
            }

            @Test
            void rightFromReverse() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(piece, Rotate.Reverse));
                    assertThat(patterns).containsExactly(
                            new int[]{0, 0},
                            new int[]{1, 0},
                            new int[]{1, 1},
                            new int[]{0, -2},
                            new int[]{1, -2}
                    );
                }
            }

            @Test
            void leftFromRight() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(piece, Rotate.Right));
                    assertThat(patterns).containsExactly(
                            new int[]{0, 0},
                            new int[]{1, 0},
                            new int[]{1, -1},
                            new int[]{0, 2},
                            new int[]{1, 2}
                    );
                }
            }

            @Test
            void rightFromRight() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(piece, Rotate.Right));
                    assertThat(patterns).containsExactly(
                            new int[]{0, 0},
                            new int[]{1, 0},
                            new int[]{1, -1},
                            new int[]{0, 2},
                            new int[]{1, 2}
                    );
                }
            }
        }

        @Nested
        class I {
            @Test
            void leftFromSpawn() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.I, Rotate.Spawn));
                assertThat(patterns).containsExactly(
                        new int[]{0, -1},
                        new int[]{-1, -1},
                        new int[]{2, -1},
                        new int[]{-1, 1},
                        new int[]{2, -2}
                );
            }

            @Test
            void rightFromSpawn() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.I, Rotate.Spawn));
                assertThat(patterns).containsExactly(
                        new int[]{1, 0},
                        new int[]{-1, 0},
                        new int[]{2, 0},
                        new int[]{-1, -1},
                        new int[]{2, 2}
                );
            }

            @Test
            void leftFromLeft() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.I, Rotate.Left));
                assertThat(patterns).containsExactly(
                        new int[]{1, 0},
                        new int[]{-1, 0},
                        new int[]{2, 0},
                        new int[]{-1, -1},
                        new int[]{2, 2}
                );
            }

            @Test
            void rightFromLeft() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.I, Rotate.Left));
                assertThat(patterns).containsExactly(
                        new int[]{0, 1},
                        new int[]{1, 1},
                        new int[]{-2, 1},
                        new int[]{1, -1},
                        new int[]{-2, 2}
                );
            }

            @Test
            void leftFromReverse() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.I, Rotate.Reverse));
                assertThat(patterns).containsExactly(
                        new int[]{0, 1},
                        new int[]{1, 1},
                        new int[]{-2, 1},
                        new int[]{1, -1},
                        new int[]{-2, 2}
                );
            }

            @Test
            void rightFromReverse() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.I, Rotate.Reverse));
                assertThat(patterns).containsExactly(
                        new int[]{-1, 0},
                        new int[]{1, 0},
                        new int[]{-2, 0},
                        new int[]{1, 1},
                        new int[]{-2, -2}
                );
            }

            @Test
            void leftFromRight() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.I, Rotate.Right));
                assertThat(patterns).containsExactly(
                        new int[]{-1, 0},
                        new int[]{1, 0},
                        new int[]{-2, 0},
                        new int[]{1, 1},
                        new int[]{-2, -2}
                );
            }

            @Test
            void rightFromRight() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.I, Rotate.Right));
                assertThat(patterns).containsExactly(
                        new int[]{0, -1},
                        new int[]{-1, -1},
                        new int[]{2, -1},
                        new int[]{-1, 1},
                        new int[]{2, -2}
                );
            }
        }

        @Nested
        class O {
            @Test
            void leftFromSpawn() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.O, Rotate.Spawn));
                assertThat(patterns).containsExactly(
                        new int[]{1, 0}
                );
            }

            @Test
            void rightFromSpawn() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.O, Rotate.Spawn));
                assertThat(patterns).containsExactly(
                        new int[]{0, 1}
                );
            }

            @Test
            void leftFromLeft() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.O, Rotate.Left));
                assertThat(patterns).containsExactly(
                        new int[]{0, 1}
                );
            }

            @Test
            void rightFromLeft() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.O, Rotate.Left));
                assertThat(patterns).containsExactly(
                        new int[]{-1, 0}
                );
            }

            @Test
            void leftFromReverse() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.O, Rotate.Reverse));
                assertThat(patterns).containsExactly(
                        new int[]{-1, 0}
                );
            }

            @Test
            void rightFromReverse() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.O, Rotate.Reverse));
                assertThat(patterns).containsExactly(
                        new int[]{0, -1}
                );
            }

            @Test
            void leftFromRight() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.O, Rotate.Right));
                assertThat(patterns).containsExactly(
                        new int[]{0, -1}
                );
            }

            @Test
            void rightFromRight() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.O, Rotate.Right));
                assertThat(patterns).containsExactly(
                        new int[]{1, 0}
                );
            }
        }
    }
}