package core.srs;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.Piece;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import entry.common.kicks.factory.FileMinoRotationFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MinoRotationTest {
    @Nested
    class Kicks {
        private final MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();

        private int[] kicksLeft(String marks, Mino mino, int x, int y) {
            Field field = FieldFactory.createField(marks);
            assert field.canPut(mino, x, y);
            Mino after = new Mino(mino.getPiece(), mino.getRotate().getLeftRotate());
            return minoRotation.getKicks(field, mino, after, x, y, RotateDirection.Left);
        }

        private int[] kicksRight(String marks, Mino mino, int x, int y) {
            Field field = FieldFactory.createField(marks);
            assert field.canPut(mino, x, y);
            Mino after = new Mino(mino.getPiece(), mino.getRotate().getRightRotate());
            return minoRotation.getKicks(field, mino, after, x, y, RotateDirection.Right);
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
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Spawn), 2, 3)).containsExactly(-1, -1);
            }

            @Test
            void checks1ng1() {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "X_X_XXXXXX" +
                        "X_X_XXXXXX" +
                        "X_XXXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Spawn), 2, 3)).containsExactly(1, 0);
            }

            @Test
            void checks1ng2() {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "X_XX_XXXXX" +
                        "X_XX_XXXXX" +
                        "X_XXXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.I, Rotate.Spawn), 2, 3)).containsExactly(2, 0);
            }

            @Test
            void checks2ok1() {
                String marks = "" +
                        "________XX" +
                        "_________X" +
                        "XXXXXXXX_X" +
                        "XXXXXXXX_X" +
                        "XXXXXXXX_X";
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Reverse), 7, 3)).containsExactly(1, -1);
            }

            @Test
            void checks2ng1() {
                String marks = "" +
                        "________XX" +
                        "_________X" +
                        "XXXXXXX__X" +
                        "XXXXXXXX_X" +
                        "XXXXXXXX_X";
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Reverse), 7, 3)).containsExactly(0, 1);
            }

            @Test
            void checks2ng2() {
                String marks = "" +
                        "________XX" +
                        "_________X" +
                        "XXXXX_XX_X" +
                        "XXXXXXXX_X" +
                        "XXXXXXXX_X";
                assertThat(kicksLeft(marks, new Mino(Piece.I, Rotate.Reverse), 7, 3)).containsExactly(-2, 1);
            }

            @Test
            void checks3ok1() {
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
            void checks3ok2() {
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
            void checks3ok3() {
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
            void checks3ok4() {
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
            void checks4ok1() {
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
            void checks4ok2() {
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
            void checks4ok3() {
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
            void checks4ok4() {
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
            void checks4ok5() {
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
            void checks5() {
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
            void checksRight() {
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
            void checksLeft() {
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
            void checks1ok1() {
                String marks = "" +
                        "XX__XXXXXX" +
                        "X__XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.S, Rotate.Right), 1, 2)).containsExactly(1, -1);
            }

            @Test
            void checks2ok1() {
                String marks = "" +
                        "XX__XXXXXX" +
                        "XX__XXXXXX" +
                        "X__XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.S, Rotate.Left), 3, 2)).containsExactly(-1, -1);
            }

            @Test
            void checks2ng1() {
                String marks = "" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "X__XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.S, Rotate.Left), 3, 2)).containsExactly(0, 0);
            }

            @Test
            void checks3ok1() {
                String marks = "" +
                        "___X______" +
                        "X_XXXXXXXX" +
                        "X__XXXXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.S, Rotate.Spawn), 2, 3)).containsExactly(0, -2);
            }

            @Test
            void checks3ng1() {
                String marks = "" +
                        "__________" +
                        "X_XXXXXXXX" +
                        "X__XXXXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.S, Rotate.Spawn), 2, 3)).containsExactly(1, 1);
            }

            @Test
            void checks4ok1() {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "X_XXXXXXXX" +
                        "X__XXXXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.S, Rotate.Spawn), 2, 3)).containsExactly(-1, -2);
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
                assertThat(kicksRight(marks, new Mino(Piece.S, Rotate.Spawn), 2, 3)).containsExactly(-1, -2);
            }

            @Test
            void checks4ok3() {
                String marks = "" +
                        "X_________" +
                        "__________" +
                        "X_XXXXXXXX" +
                        "X__XXXXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.S, Rotate.Spawn), 1, 3)).containsExactly(0, -2);
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
                assertThat(kicksRight(marks, new Mino(Piece.S, Rotate.Spawn), 1, 3)).containsExactly(-1, 0);
            }

            @Test
            void checks5ng1() {
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
            void checks1ok1() {
                String marks = "" +
                        "X__XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.Z, Rotate.Left), 3, 2)).containsExactly(-1, -1);
            }

            @Test
            void checks2ok1() {
                String marks = "" +
                        "X__XXXXXXX" +
                        "X__XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.Z, Rotate.Right), 1, 2)).containsExactly(1, -1);
            }

            @Test
            void checks2ng1() {
                String marks = "" +
                        "___XXXXXXX" +
                        "X__XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.Z, Rotate.Right), 1, 2)).containsExactly(0, 0);
            }

            @Test
            void checks3ok1() {
                String marks = "" +
                        "__X_______" +
                        "XXXX_XXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.Z, Rotate.Spawn), 3, 3)).containsExactly(0, -2);
            }

            @Test
            void checks3ng1() {
                String marks = "" +
                        "__________" +
                        "XXXX_XXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.Z, Rotate.Spawn), 3, 3)).containsExactly(-1, 1);
            }

            @Test
            void checks4ok1() {
                String marks = "" +
                        "____XX____" +
                        "_____X____" +
                        "XXXX_XXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.Z, Rotate.Spawn), 3, 3)).containsExactly(1, -2);
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
                assertThat(kicksLeft(marks, new Mino(Piece.Z, Rotate.Spawn), 3, 3)).containsExactly(1, -2);
            }

            @Test
            void checks4ok3() {
                String marks = "" +
                        "_____XX___" +
                        "______X___" +
                        "XXXX_XXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.Z, Rotate.Spawn), 4, 3)).containsExactly(0, -2);
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
                assertThat(kicksLeft(marks, new Mino(Piece.Z, Rotate.Spawn), 4, 3)).containsExactly(1, 0);
            }

            @Test
            void checks5ng1() {
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
            void checks1ok1() {
                String marks = "" +
                        "XXX_______" +
                        "XX________" +
                        "XX_X______";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Left), 4, 1)).containsExactly(-1, 0);
            }

            @Test
            void checks2ok1() {
                String marks = "" +
                        "XXX__XXXXX" +
                        "XX___XXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Left), 4, 2)).containsExactly(-1, -1);
            }

            @Test
            void checks2ng1() {
                String marks = "" +
                        "XXX___XXXX" +
                        "XX___XXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Left), 4, 2)).containsExactly(0, 0);
            }

            @Test
            void checks3ok1() {
                String marks = "" +
                        "XXXX______" +
                        "XX________" +
                        "XX_XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Left), 4, 2)).containsExactly(-1, -1);
            }

            @Test
            void checks3ng1() {
                String marks = "" +
                        "XXX_______" +
                        "XX________" +
                        "XX_XXXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Left), 4, 2)).containsExactly(0, 0);
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Left), 3, 2)).containsExactly(0, 2);
            }

            @Test
            void checks4ok1() {
                String marks = "" +
                        "____XXXXXX" +
                        "XX___XXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Right), 2, 2)).containsExactly(1, -1);
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Right), 3, 2)).containsExactly(0, 2);
            }

            @Test
            void checks4ng1() {
                String marks = "" +
                        "____XXXXXX" +
                        "X____XXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Right), 2, 2)).containsExactly(0, 0);
            }

            @Test
            void checks5ok1() {
                String marks = "" +
                        "___XXXXXXX" +
                        "_____XXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Right), 2, 2)).containsExactly(1, -1);
            }

            @Test
            void checks5ng1() {
                String marks = "" +
                        "____XXXXXX" +
                        "_____XXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Right), 2, 2)).containsExactly(0, 0);
            }

            @Test
            void checks6ok1() {
                String marks = "" +
                        "XXXX_XXXXX" +
                        "XX___XXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Left), 4, 1)).containsExactly(-1, -1);
            }

            @Test
            void checks6ok2() {
                String marks = "" +
                        "XXX__XXXXX" +
                        "XX___XXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Left), 4, 1)).containsExactly(-1, -1);
            }

            @Test
            void checks6ok3() {
                String marks = "" +
                        "XXXX__XXXX" +
                        "XX___XXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Left), 4, 1)).containsExactly(-1, -1);
            }

            @Test
            void checks6ng1() {
                String marks = "" +
                        "XXX___XXXX" +
                        "XX___XXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Left), 4, 1)).containsExactly(0, 0);
            }

            @Test
            void checks7ok1() {
                String marks = "" +
                        "____XXXXXX" +
                        "XX___XXXXX" +
                        "XX___XXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Right), 2, 1)).containsExactly(1, -1);
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Right), 3, 1)).containsExactly(0, 2);
            }

            @Test
            void checks7ng1() {
                String marks = "" +
                        "____XXXXXX" +
                        "X____XXXXX" +
                        "XX___XXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.L, Rotate.Right), 2, 1)).containsExactly(0, 0);
            }

            @Test
            void checks8ok1() {
                String marks = "" +
                        "XXX_______" +
                        "XX________" +
                        "XX_XXXXXXX" +
                        "XX_XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Spawn), 3, 3)).containsExactly(-1, -2);
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
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Spawn), 3, 3)).containsExactly(-1, -2);
            }

            @Test
            void checks8ok3() {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "XX_XXXXXXX" +
                        "XX_XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Spawn), 2, 3)).containsExactly(0, -2);
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
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Spawn), 2, 3)).containsExactly(0, -2);
            }

            @Test
            void checks8ng1() {
                String marks = "" +
                        "XX_XXXXXXX" +
                        "XX_XXXXXXX" +
                        "XX__XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.L, Rotate.Spawn), 2, 3)).containsExactly(-1, 1);
            }

            @Test
            void checks9ng1() {
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
            void checks1ok1() {
                String marks = "" +
                        "_______XXX" +
                        "________XX" +
                        "______X_XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Right), 5, 1)).containsExactly(1, 0);
            }

            @Test
            void checks2ok1() {
                String marks = "" +
                        "XXXXX__XXX" +
                        "XXXXX___XX" +
                        "XXXXXXX_XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Right), 5, 2)).containsExactly(1, -1);
            }

            @Test
            void checks2ng1() {
                String marks = "" +
                        "XXXX___XXX" +
                        "XXXXX___XX" +
                        "XXXXXXX_XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Right), 5, 2)).containsExactly(0, 0);
            }

            @Test
            void checks3ok1() {
                String marks = "" +
                        "______XXXX" +
                        "________XX" +
                        "XXXXXXX_XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Right), 5, 2)).containsExactly(1, -1);
            }

            @Test
            void checks3ng1() {
                String marks = "" +
                        "_______XXX" +
                        "________XX" +
                        "XXXXXXX_XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Right), 5, 2)).containsExactly(0, 0);
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Right), 6, 2)).containsExactly(0, 2);
            }

            @Test
            void checks4ok1() {
                String marks = "" +
                        "XXXXXX____" +
                        "XXXXX___XX" +
                        "XXXXXXX_XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Left), 7, 2)).containsExactly(-1, -1);
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Left), 6, 2)).containsExactly(0, 2);
            }

            @Test
            void checks4ng1() {
                String marks = "" +
                        "XXXXXX____" +
                        "XXXXX____X" +
                        "XXXXXXX_XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Left), 7, 2)).containsExactly(0, 0);
            }

            @Test
            void checks5ok1() {
                String marks = "" +
                        "XXXXXXX___" +
                        "XXXXX_____" +
                        "XXXXXXX_XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Left), 7, 2)).containsExactly(-1, -1);
            }

            @Test
            void checks5ng1() {
                String marks = "" +
                        "XXXXXX____" +
                        "XXXXX_____" +
                        "XXXXXXX_XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Left), 7, 2)).containsExactly(0, 0);
            }

            @Test
            void checks6ok1() {
                String marks = "" +
                        "XXXXX_XXXX" +
                        "XXXXX___XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Right), 5, 1)).containsExactly(1, -1);
            }

            @Test
            void checks6ok2() {
                String marks = "" +
                        "XXXXX__XXX" +
                        "XXXXX___XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Right), 5, 1)).containsExactly(1, -1);
            }

            @Test
            void checks6ok3() {
                String marks = "" +
                        "XXXX__XXXX" +
                        "XXXXX___XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Right), 5, 1)).containsExactly(1, -1);
            }

            @Test
            void checks6ng1() {
                String marks = "" +
                        "XXXX___XXX" +
                        "XXXXX___XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Right), 5, 1)).containsExactly(0, 0);
            }

            @Test
            void checks7ok1() {
                String marks = "" +
                        "XXXXXX____" +
                        "XXXXX___XX" +
                        "XXXXX___XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Left), 7, 1)).containsExactly(-1, -1);
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Left), 6, 1)).containsExactly(0, 2);
            }

            @Test
            void checks7ng1() {
                String marks = "" +
                        "XXXXXX____" +
                        "XXXXX____X" +
                        "XXXXX___XX";
                assertThat(kicksRight(marks, new Mino(Piece.J, Rotate.Left), 7, 1)).containsExactly(0, 0);
            }

            @Test
            void checks8ok1() {
                String marks = "" +
                        "_______XXX" +
                        "________XX" +
                        "XXXXXXX_XX" +
                        "XXXXXXX_XX" +
                        "XXXXXX__XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Spawn), 6, 3)).containsExactly(1, -2);
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
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Spawn), 6, 3)).containsExactly(1, -2);
            }

            @Test
            void checks8ok3() {
                String marks = "" +
                        "________XX" +
                        "_________X" +
                        "XXXXXXX_XX" +
                        "XXXXXXX_XX" +
                        "XXXXXX__XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Spawn), 7, 3)).containsExactly(0, -2);
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
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Spawn), 7, 3)).containsExactly(0, -2);
            }

            @Test
            void checks8ng1() {
                String marks = "" +
                        "XXXXXXX_XX" +
                        "XXXXXXX_XX" +
                        "XXXXXX__XX";
                assertThat(kicksLeft(marks, new Mino(Piece.J, Rotate.Spawn), 7, 3)).containsExactly(1, 1);
            }

            @Test
            void checks9ng1() {
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
            void checks1ok1() {
                String marks = "" +
                        "XX________" +
                        "X_________" +
                        "X_________" +
                        "X_XXXXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Spawn), 2, 1)).containsExactly(-1, 0);
            }

            @Test
            void checks1ok2() {
                String marks = "" +
                        "________XX" +
                        "_________X" +
                        "_________X" +
                        "XXXXXXXX_X";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Spawn), 7, 1)).containsExactly(1, 0);
            }

            @Test
            void checks2ok1() {
                String marks = "" +
                        "_____XXXXX" +
                        "XXX__XXXXX" +
                        "XX___XXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Left), 4, 1)).containsExactly(-1, -1);
            }

            @Test
            void checks2ok2() {
                String marks = "" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "XX___XXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Right), 2, 1)).containsExactly(1, -1);
            }

            @Test
            void checks2ng1() {
                String marks = "" +
                        "_____XXXXX" +
                        "XXX___XXXX" +
                        "XX___XXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Left), 4, 1)).containsExactly(0, 0);
            }

            @Test
            void checks2ng2() {
                String marks = "" +
                        "XX________" +
                        "X___XXXXXX" +
                        "XX___XXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Right), 2, 1)).containsExactly(0, 0);
            }

            @Test
            void checks3ok1() {
                String marks = "" +
                        "____XXXXXX" +
                        "XX___XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Left), 3, 1)).containsExactly(0, 0);
            }

            @Test
            void checks3ok2() {
                String marks = "" +
                        "XXX_______" +
                        "XX___XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Right), 3, 1)).containsExactly(0, 0);
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
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Spawn), 4, 3)).containsExactly(-1, -2);
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
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Spawn), 2, 3)).containsExactly(1, -2);
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
                assertThat(kicksRight(marks, new Mino(Piece.T, Rotate.Spawn), 4, 3)).containsExactly(-1, 0);
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
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Spawn), 2, 3)).containsExactly(1, 0);
            }

            @Test
            void checks4ng3() {
                String marks = "" +
                        "XXX_XXXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Spawn), 3, 3)).containsExactly(0, 0);
            }

            @Test
            void checks4ng4() {
                String marks = "" +
                        "XXX_XXXXXX" +
                        "XX__XXXXXX" +
                        "XXX_XXXXXX" +
                        "";
                assertThat(kicksLeft(marks, new Mino(Piece.T, Rotate.Spawn), 3, 3)).containsExactly(0, 0);
            }

            @Test
            void checks5ok1() {
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
            void checks5ok2() {
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
            void checks6ok1() {
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
            void checks6ok2() {
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
            void checks7ok1() {
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
            void checks7ok2() {
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
        private final MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();

        @Nested
        class JLSTZ {
            private final List<Piece> pieces = Arrays.asList(Piece.J, Piece.L, Piece.S, Piece.T, Piece.Z);

            @Test
            void leftFromSpawn() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(piece, Rotate.Spawn));
                    assertThat(patterns[0]).isEqualTo(new int[]{0, 0});
                    assertThat(patterns[1]).isEqualTo(new int[]{1, 0});
                    assertThat(patterns[2]).isEqualTo(new int[]{1, 1});
                    assertThat(patterns[3]).isEqualTo(new int[]{0, -2});
                    assertThat(patterns[4]).isEqualTo(new int[]{1, -2});
                }
            }

            @Test
            void rightFromSpawn() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(piece, Rotate.Spawn));
                    assertThat(patterns[0]).isEqualTo(new int[]{0, 0});
                    assertThat(patterns[1]).isEqualTo(new int[]{-1, 0});
                    assertThat(patterns[2]).isEqualTo(new int[]{-1, 1});
                    assertThat(patterns[3]).isEqualTo(new int[]{0, -2});
                    assertThat(patterns[4]).isEqualTo(new int[]{-1, -2});
                }
            }

            @Test
            void leftFromLeft() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(piece, Rotate.Left));
                    assertThat(patterns[0]).isEqualTo(new int[]{0, 0});
                    assertThat(patterns[1]).isEqualTo(new int[]{-1, 0});
                    assertThat(patterns[2]).isEqualTo(new int[]{-1, -1});
                    assertThat(patterns[3]).isEqualTo(new int[]{0, 2});
                    assertThat(patterns[4]).isEqualTo(new int[]{-1, 2});
                }
            }

            @Test
            void rightFromLeft() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(piece, Rotate.Left));
                    assertThat(patterns[0]).isEqualTo(new int[]{0, 0});
                    assertThat(patterns[1]).isEqualTo(new int[]{-1, 0});
                    assertThat(patterns[2]).isEqualTo(new int[]{-1, -1});
                    assertThat(patterns[3]).isEqualTo(new int[]{0, 2});
                    assertThat(patterns[4]).isEqualTo(new int[]{-1, 2});
                }
            }

            @Test
            void leftFromReverse() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(piece, Rotate.Reverse));
                    assertThat(patterns[0]).isEqualTo(new int[]{0, 0});
                    assertThat(patterns[1]).isEqualTo(new int[]{-1, 0});
                    assertThat(patterns[2]).isEqualTo(new int[]{-1, 1});
                    assertThat(patterns[3]).isEqualTo(new int[]{0, -2});
                    assertThat(patterns[4]).isEqualTo(new int[]{-1, -2});
                }
            }

            @Test
            void rightFromReverse() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(piece, Rotate.Reverse));
                    assertThat(patterns[0]).isEqualTo(new int[]{0, 0});
                    assertThat(patterns[1]).isEqualTo(new int[]{1, 0});
                    assertThat(patterns[2]).isEqualTo(new int[]{1, 1});
                    assertThat(patterns[3]).isEqualTo(new int[]{0, -2});
                    assertThat(patterns[4]).isEqualTo(new int[]{1, -2});
                }
            }

            @Test
            void leftFromRight() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(piece, Rotate.Right));
                    assertThat(patterns[0]).isEqualTo(new int[]{0, 0});
                    assertThat(patterns[1]).isEqualTo(new int[]{1, 0});
                    assertThat(patterns[2]).isEqualTo(new int[]{1, -1});
                    assertThat(patterns[3]).isEqualTo(new int[]{0, 2});
                    assertThat(patterns[4]).isEqualTo(new int[]{1, 2});
                }
            }

            @Test
            void rightFromRight() {
                for (Piece piece : pieces) {
                    int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(piece, Rotate.Right));
                    assertThat(patterns[0]).isEqualTo(new int[]{0, 0});
                    assertThat(patterns[1]).isEqualTo(new int[]{1, 0});
                    assertThat(patterns[2]).isEqualTo(new int[]{1, -1});
                    assertThat(patterns[3]).isEqualTo(new int[]{0, 2});
                    assertThat(patterns[4]).isEqualTo(new int[]{1, 2});
                }
            }
        }

        @Nested
        class I {
            @Test
            void leftFromSpawn() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.I, Rotate.Spawn));
                assertThat(patterns[0]).isEqualTo(new int[]{0, -1});
                assertThat(patterns[1]).isEqualTo(new int[]{-1, -1});
                assertThat(patterns[2]).isEqualTo(new int[]{2, -1});
                assertThat(patterns[3]).isEqualTo(new int[]{-1, 1});
                assertThat(patterns[4]).isEqualTo(new int[]{2, -2});
            }

            @Test
            void rightFromSpawn() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.I, Rotate.Spawn));
                assertThat(patterns[0]).isEqualTo(new int[]{1, 0});
                assertThat(patterns[1]).isEqualTo(new int[]{-1, 0});
                assertThat(patterns[2]).isEqualTo(new int[]{2, 0});
                assertThat(patterns[3]).isEqualTo(new int[]{-1, -1});
                assertThat(patterns[4]).isEqualTo(new int[]{2, 2});
            }

            @Test
            void leftFromLeft() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.I, Rotate.Left));
                assertThat(patterns[0]).isEqualTo(new int[]{1, 0});
                assertThat(patterns[1]).isEqualTo(new int[]{-1, 0});
                assertThat(patterns[2]).isEqualTo(new int[]{2, 0});
                assertThat(patterns[3]).isEqualTo(new int[]{-1, -1});
                assertThat(patterns[4]).isEqualTo(new int[]{2, 2});
            }

            @Test
            void rightFromLeft() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.I, Rotate.Left));
                assertThat(patterns[0]).isEqualTo(new int[]{0, 1});
                assertThat(patterns[1]).isEqualTo(new int[]{1, 1});
                assertThat(patterns[2]).isEqualTo(new int[]{-2, 1});
                assertThat(patterns[3]).isEqualTo(new int[]{1, -1});
                assertThat(patterns[4]).isEqualTo(new int[]{-2, 2});
            }

            @Test
            void leftFromReverse() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.I, Rotate.Reverse));
                assertThat(patterns[0]).isEqualTo(new int[]{0, 1});
                assertThat(patterns[1]).isEqualTo(new int[]{1, 1});
                assertThat(patterns[2]).isEqualTo(new int[]{-2, 1});
                assertThat(patterns[3]).isEqualTo(new int[]{1, -1});
                assertThat(patterns[4]).isEqualTo(new int[]{-2, 2});
            }

            @Test
            void rightFromReverse() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.I, Rotate.Reverse));
                assertThat(patterns[0]).isEqualTo(new int[]{-1, 0});
                assertThat(patterns[1]).isEqualTo(new int[]{1, 0});
                assertThat(patterns[2]).isEqualTo(new int[]{-2, 0});
                assertThat(patterns[3]).isEqualTo(new int[]{1, 1});
                assertThat(patterns[4]).isEqualTo(new int[]{-2, -2});
            }

            @Test
            void leftFromRight() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.I, Rotate.Right));
                assertThat(patterns[0]).isEqualTo(new int[]{-1, 0});
                assertThat(patterns[1]).isEqualTo(new int[]{1, 0});
                assertThat(patterns[2]).isEqualTo(new int[]{-2, 0});
                assertThat(patterns[3]).isEqualTo(new int[]{1, 1});
                assertThat(patterns[4]).isEqualTo(new int[]{-2, -2});
            }

            @Test
            void rightFromRight() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.I, Rotate.Right));
                assertThat(patterns[0]).isEqualTo(new int[]{0, -1});
                assertThat(patterns[1]).isEqualTo(new int[]{-1, -1});
                assertThat(patterns[2]).isEqualTo(new int[]{2, -1});
                assertThat(patterns[3]).isEqualTo(new int[]{-1, 1});
                assertThat(patterns[4]).isEqualTo(new int[]{2, -2});
            }
        }

        @Nested
        class O {
            @Test
            void leftFromSpawn() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.O, Rotate.Spawn));
                assertThat(patterns[0]).isEqualTo(new int[]{1, 0});
            }

            @Test
            void rightFromSpawn() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.O, Rotate.Spawn));
                assertThat(patterns[0]).isEqualTo(new int[]{0, 1});
            }

            @Test
            void leftFromLeft() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.O, Rotate.Left));
                assertThat(patterns[0]).isEqualTo(new int[]{0, 1});
            }

            @Test
            void rightFromLeft() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.O, Rotate.Left));
                assertThat(patterns[0]).isEqualTo(new int[]{-1, 0});
            }

            @Test
            void leftFromReverse() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.O, Rotate.Reverse));
                assertThat(patterns[0]).isEqualTo(new int[]{-1, 0});
            }

            @Test
            void rightFromReverse() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.O, Rotate.Reverse));
                assertThat(patterns[0]).isEqualTo(new int[]{0, -1});
            }

            @Test
            void leftFromRight() {
                int[][] patterns = minoRotation.getLeftPatternsFrom(new Mino(Piece.O, Rotate.Right));
                assertThat(patterns[0]).isEqualTo(new int[]{0, -1});
            }

            @Test
            void rightFromRight() {
                int[][] patterns = minoRotation.getRightPatternsFrom(new Mino(Piece.O, Rotate.Right));
                assertThat(patterns[0]).isEqualTo(new int[]{1, 0});
            }
        }
    }

    @Nested
    class Rotate180 {
        private final MinoRotation minoRotation = FileMinoRotationFactory.load(Paths.get("kicks/nullpomino180.properties")).create();

        private int[] kicks180(String marks, Mino mino, int x, int y) {
            Field field = FieldFactory.createField(marks);
            assert field.canPut(mino, x, y);
            Mino after = new Mino(mino.getPiece(), mino.getRotate().get180Rotate());
            return minoRotation.getKicksWith180Rotation(field, mino, after, x, y);
        }

        @Nested
        class WithI {
            @Test
            void case1() {
                String marks = "" +
                        "__________" +
                        "XXXXXXXX__" +
                        "XXXXXXX___" +
                        "XXXXXXX___" +
                        "XXXXXXX___" +
                        "XXXXXXX_X_" +
                        "XXXXXXXXX_" +
                        "XXXXXXXXX_";
                assertThat(kicks180(marks, new Mino(Piece.I, Rotate.Right), 8, 5)).containsExactly(-1, -2);
            }

            @Test
            void case2() {
                String marks = "" +
                        "__XX______" +
                        "___XXXXXXX" +
                        "___XXXXXXX" +
                        "_X_XXXXXXX" +
                        "_X_XXXXXXX" +
                        "_XXXXXXXXX";
                assertThat(kicks180(marks, new Mino(Piece.I, Rotate.Left), 1, 4)).containsExactly(1, -1);
            }

            @Test
            void case3() {
                String marks = "" +
                        "__________" +
                        "_XXXXXXXXX" +
                        "_X_XXXXXXX" +
                        "_X_XXXXXXX" +
                        "_X_XXXXXXX" +
                        "_X_XXXXXXX";
                assertThat(kicks180(marks, new Mino(Piece.I, Rotate.Left), 0, 1)).containsExactly(2, 1);
            }

            @Test
            void case4() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "XXX____X__" +
                        "_XXXXXXXXX" +
                        "XXX____XXX";
                assertThat(kicks180(marks, new Mino(Piece.I, Rotate.Spawn), 4, 2)).containsExactly(1, -2);
            }
        }

        @Nested
        class WithO {
            @Test
            void case1() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "__________";
                assertThat(kicks180(marks, new Mino(Piece.O, Rotate.Spawn), 4, 1)).containsExactly(1, 1);
                assertThat(kicks180(marks, new Mino(Piece.O, Rotate.Reverse), 5, 2)).containsExactly(-1, -1);

                assertThat(kicks180(marks, new Mino(Piece.O, Rotate.Right), 5, 2)).containsExactly(1, -1);
                assertThat(kicks180(marks, new Mino(Piece.O, Rotate.Left), 6, 1)).containsExactly(-1, 1);
            }
        }

        @Nested
        class WithL {
            @Test
            void case1() {
                String marks = "" +
                        "__________" +
                        "XX________" +
                        "XXX___XXXX" +
                        "XXX___XXXX" +
                        "XX____XXXX" +
                        "XX_XXXXXXX";
                assertThat(kicks180(marks, new Mino(Piece.L, Rotate.Spawn), 3, 1)).containsExactly(0, 0);
            }

            @Test
            void case2() {
                String marks = "" +
                        "__________" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "XXX_XXXXXX" +
                        "XXX_XXXXXX" +
                        "XXX__XXXXX";
                assertThat(kicks180(marks, new Mino(Piece.L, Rotate.Left), 3, 2)).containsExactly(0, -1);
            }
        }

        @Nested
        class WithJ {
            @Test
            void case1() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "XXXXX__XXX" +
                        "XXXXX__XXX" +
                        "XXXXX_XXXX" +
                        "XXXX__XXXX";
                assertThat(kicks180(marks, new Mino(Piece.J, Rotate.Right), 5, 1)).containsExactly(0, 0);
            }
        }

        @Nested
        class WithS {
            @Test
            void case1() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "XXXXXX____" +
                        "XXXXXX____" +
                        "XXXXXX__X_" +
                        "XXXXX__XXX";
                assertThat(kicks180(marks, new Mino(Piece.S, Rotate.Spawn), 7, 1)).containsExactly(-1, 0);
            }

            @Test
            void case2() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "_X____X___" +
                        "XX__XXX___" +
                        "XXX__XXXXX" +
                        "XXXX_XXXXX";
                assertThat(kicks180(marks, new Mino(Piece.S, Rotate.Left), 3, 2)).containsExactly(0, -1);
            }

            @Test
            void case3() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "____XXXXXX" +
                        "X__XXXXXXX" +
                        "XXXX__XXXX" +
                        "XXX__XXXXX";
                assertThat(kicks180(marks, new Mino(Piece.S, Rotate.Spawn), 2, 2)).containsExactly(2, -1);
            }

            @Test
            void case4() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "____XXX___" +
                        "XXXXX_X___" +
                        "XXXXX__X__" +
                        "XXXXXX_XXX";
                assertThat(kicks180(marks, new Mino(Piece.S, Rotate.Right), 7, 2)).containsExactly(-1, -1);
            }

            @Test
            void case5() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "XXXXXXX___" +
                        "XXXXX_XX__" +
                        "XXXXX__XX_" +
                        "XXXXXX_XXX";
                assertThat(kicks180(marks, new Mino(Piece.S, Rotate.Right), 7, 3)).containsExactly(-1, -2);
            }
        }

        @Nested
        class WithZ {
            @Test
            void case1() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "X____XXXXX" +
                        "X____XXXXX" +
                        "XXXX__XXXX";
                assertThat(kicks180(marks, new Mino(Piece.Z, Rotate.Spawn), 3, 1)).containsExactly(1, 0);
            }

            @Test
            void case2() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "XXXXXX_XXX" +
                        "XXXXX__XXX" +
                        "XXXXX_XXXX";
                assertThat(kicks180(marks, new Mino(Piece.Z, Rotate.Right), 6, 3)).containsExactly(0, -2);
            }

            @Test
            void case3() {
                String marks = "" +
                        "__________" +
                        "X_________" +
                        "X__XXXXXXX" +
                        "X__X_XXXXX" +
                        "X_X__XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicks180(marks, new Mino(Piece.Z, Rotate.Left), 2, 2)).containsExactly(1, -1);
            }

            @Test
            void case4() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "___XXXXXXX" +
                        "__XX_XXXXX" +
                        "_XX__XXXXX" +
                        "XXX_XXXXXX";
                assertThat(kicks180(marks, new Mino(Piece.Z, Rotate.Left), 2, 3)).containsExactly(1, -2);
            }
        }

        @Nested
        class WithT {
            @Test
            void case1() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "XX___XXXXX" +
                        "XXXX__XXXX" +
                        "XXXX_XXXXX";
                assertThat(kicks180(marks, new Mino(Piece.T, Rotate.Left), 4, 2)).containsExactly(0, -1);
            }

            @Test
            void case2() {
                String marks = "" +
                        "__________" +
                        "_____XXXXX" +
                        "_____XXXXX" +
                        "XXXX_XXXXX" +
                        "XXXX__XXXX" +
                        "XXXX_XXXXX";
                assertThat(kicks180(marks, new Mino(Piece.T, Rotate.Left), 4, 3)).containsExactly(0, -2);
            }

            @Test
            void case3() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "XX___XX___" +
                        "XXX__XX___" +
                        "X_____XXXX" +
                        "XX_XXXXXXX";
                assertThat(kicks180(marks, new Mino(Piece.T, Rotate.Spawn), 4, 1)).containsExactly(-2, 0);
            }

            @Test
            void case4() {
                String marks = "" +
                        "__________" +
                        "___X______" +
                        "XXXX___XX_" +
                        "XXXX__XXXX" +
                        "X______XXX" +
                        "XX_XXXXXXX";
                assertThat(kicks180(marks, new Mino(Piece.T, Rotate.Spawn), 5, 1)).containsExactly(-3, 0);
            }

            @Test
            void case5() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "XX___XXXXX" +
                        "XXX___XXXX" +
                        "X___XXXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicks180(marks, new Mino(Piece.T, Rotate.Spawn), 4, 2)).containsExactly(-2, -1);
            }

            @Test
            void case6() {
                String marks = "" +
                        "__________" +
                        "__________" +
                        "XX________" +
                        "XXX___XXXX" +
                        "X___XXXXXX" +
                        "XX_XXXXXXX";
                assertThat(kicks180(marks, new Mino(Piece.T, Rotate.Spawn), 4, 2)).containsExactly(-2, -1);
            }
        }
    }
}