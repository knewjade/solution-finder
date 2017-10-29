package core.mino;

import core.field.FieldFactory;
import core.field.SmallField;
import core.srs.Rotate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinoTest {
    @Nested
    class I {
        @Test
        void spawn() {
            Mino mino = new Mino(Piece.I, Rotate.Spawn);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(2, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Piece.I, Mino::getPiece)
                    .returns(Rotate.Spawn, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{0, 0})
                    .contains(new int[]{1, 0})
                    .contains(new int[]{2, 0});

            SmallField mask = FieldFactory.createSmallField(
                    "XXXX______"
            );
            assertThat(mino.getMask(1, 0)).isEqualTo(mask.getXBoard());
        }

        @Test
        void left() {
            Mino mino = new Mino(Piece.I, Rotate.Left);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(2, Mino::getMaxY)
                    .returns(Piece.I, Mino::getPiece)
                    .returns(Rotate.Left, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, -1})
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, 1})
                    .contains(new int[]{0, 2});

            SmallField mask = FieldFactory.createSmallField("" +
                    "X_________" +
                    "X_________" +
                    "X_________" +
                    "X_________"
            );
            assertThat(mino.getMask(0, 1)).isEqualTo(mask.getXBoard());
        }

        @Test
        void reverse() {
            Mino mino = new Mino(Piece.I, Rotate.Reverse);
            assertThat(mino)
                    .returns(-2, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Piece.I, Mino::getPiece)
                    .returns(Rotate.Reverse, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{-2, 0})
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{0, 0})
                    .contains(new int[]{1, 0});

            SmallField mask = FieldFactory.createSmallField(
                    "XXXX______"
            );
            assertThat(mino.getMask(2, 0)).isEqualTo(mask.getXBoard());
        }

        @Test
        void right() {
            Mino mino = new Mino(Piece.I, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-2, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.I, Mino::getPiece)
                    .returns(Rotate.Right, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, -2})
                    .contains(new int[]{0, -1})
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, 1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "X_________" +
                    "X_________" +
                    "X_________" +
                    "X_________"
            );
            assertThat(mino.getMask(0, 2)).isEqualTo(mask.getXBoard());
        }
    }

    @Nested
    class O {
        @Test
        void spawn() {
            Mino mino = new Mino(Piece.O, Rotate.Spawn);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.O, Mino::getPiece)
                    .returns(Rotate.Spawn, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{1, 0})
                    .contains(new int[]{0, 1})
                    .contains(new int[]{1, 1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "XX________" +
                    "XX________"
            );
            assertThat(mino.getMask(0, 0)).isEqualTo(mask.getXBoard());
        }

        @Test
        void left() {
            Mino mino = new Mino(Piece.O, Rotate.Left);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.O, Mino::getPiece)
                    .returns(Rotate.Left, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{-1, 1})
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, 1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "XX________" +
                    "XX________"
            );
            assertThat(mino.getMask(1, 0)).isEqualTo(mask.getXBoard());
        }

        @Test
        void reverse() {
            Mino mino = new Mino(Piece.O, Rotate.Reverse);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Piece.O, Mino::getPiece)
                    .returns(Rotate.Reverse, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{-1, -1})
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, -1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "XX________" +
                    "XX________"
            );
            assertThat(mino.getMask(1, 1)).isEqualTo(mask.getXBoard());
        }

        @Test
        void right() {
            Mino mino = new Mino(Piece.O, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Piece.O, Mino::getPiece)
                    .returns(Rotate.Right, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, -1})
                    .contains(new int[]{0, 0})
                    .contains(new int[]{1, -1})
                    .contains(new int[]{1, 0});

            SmallField mask = FieldFactory.createSmallField("" +
                    "XX________" +
                    "XX________"
            );
            assertThat(mino.getMask(0, 1)).isEqualTo(mask.getXBoard());
        }
    }

    @Nested
    class S {
        @Test
        void spawn() {
            Mino mino = new Mino(Piece.S, Rotate.Spawn);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.S, Mino::getPiece)
                    .returns(Rotate.Spawn, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{0, 1})
                    .contains(new int[]{1, 1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "_XX_______" +
                    "XX________"
            );
            assertThat(mino.getMask(1, 0)).isEqualTo(mask.getXBoard());
        }

        @Test
        void left() {
            Mino mino = new Mino(Piece.S, Rotate.Left);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.S, Mino::getPiece)
                    .returns(Rotate.Left, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{-1, 1})
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, -1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "X_________" +
                    "XX________" +
                    "_X________"
            );
            assertThat(mino.getMask(1, 1)).isEqualTo(mask.getXBoard());
        }

        @Test
        void reverse() {
            Mino mino = new Mino(Piece.S, Rotate.Reverse);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Piece.S, Mino::getPiece)
                    .returns(Rotate.Reverse, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{1, 0})
                    .contains(new int[]{0, -1})
                    .contains(new int[]{-1, -1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "_XX_______" +
                    "XX________"
            );
            assertThat(mino.getMask(1, 1)).isEqualTo(mask.getXBoard());
        }

        @Test
        void right() {
            Mino mino = new Mino(Piece.S, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.S, Mino::getPiece)
                    .returns(Rotate.Right, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, 1})
                    .contains(new int[]{1, 0})
                    .contains(new int[]{1, -1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "X_________" +
                    "XX________" +
                    "_X________"
            );
            assertThat(mino.getMask(0, 1)).isEqualTo(mask.getXBoard());
        }
    }

    @Nested
    class Z {
        @Test
        void spawn() {
            Mino mino = new Mino(Piece.Z, Rotate.Spawn);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.Z, Mino::getPiece)
                    .returns(Rotate.Spawn, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{-1, 1})
                    .contains(new int[]{0, 1})
                    .contains(new int[]{1, 0});

            SmallField mask = FieldFactory.createSmallField("" +
                    "XX________" +
                    "_XX_______"
            );
            assertThat(mino.getMask(1, 0)).isEqualTo(mask.getXBoard());
        }

        @Test
        void left() {
            Mino mino = new Mino(Piece.Z, Rotate.Left);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.Z, Mino::getPiece)
                    .returns(Rotate.Left, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{-1, -1})
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, 1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "_X________" +
                    "XX________" +
                    "X_________"
            );
            assertThat(mino.getMask(1, 1)).isEqualTo(mask.getXBoard());
        }

        @Test
        void reverse() {
            Mino mino = new Mino(Piece.Z, Rotate.Reverse);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Piece.Z, Mino::getPiece)
                    .returns(Rotate.Reverse, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{0, -1})
                    .contains(new int[]{1, -1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "XX________" +
                    "_XX_______"
            );
            assertThat(mino.getMask(1, 1)).isEqualTo(mask.getXBoard());
        }

        @Test
        void right() {
            Mino mino = new Mino(Piece.Z, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.Z, Mino::getPiece)
                    .returns(Rotate.Right, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, -1})
                    .contains(new int[]{1, 0})
                    .contains(new int[]{1, 1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "_X________" +
                    "XX________" +
                    "X_________"
            );
            assertThat(mino.getMask(0, 1)).isEqualTo(mask.getXBoard());
        }
    }

    @Nested
    class L {
        @Test
        void spawn() {
            Mino mino = new Mino(Piece.L, Rotate.Spawn);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.L, Mino::getPiece)
                    .returns(Rotate.Spawn, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{1, 0})
                    .contains(new int[]{1, 1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "__X_______" +
                    "XXX_______"
            );
            assertThat(mino.getMask(1, 0)).isEqualTo(mask.getXBoard());
        }

        @Test
        void left() {
            Mino mino = new Mino(Piece.L, Rotate.Left);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.L, Mino::getPiece)
                    .returns(Rotate.Left, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{-1, 1})
                    .contains(new int[]{0, 1})
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, -1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "XX________" +
                    "_X________" +
                    "_X________"
            );
            assertThat(mino.getMask(1, 1)).isEqualTo(mask.getXBoard());
        }

        @Test
        void reverse() {
            Mino mino = new Mino(Piece.L, Rotate.Reverse);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Piece.L, Mino::getPiece)
                    .returns(Rotate.Reverse, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{1, 0})
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{-1, -1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "XXX_______" +
                    "X_________"
            );
            assertThat(mino.getMask(1, 1)).isEqualTo(mask.getXBoard());
        }

        @Test
        void right() {
            Mino mino = new Mino(Piece.L, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.L, Mino::getPiece)
                    .returns(Rotate.Right, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, 1})
                    .contains(new int[]{0, -1})
                    .contains(new int[]{1, -1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "X_________" +
                    "X_________" +
                    "XX________"
            );
            assertThat(mino.getMask(0, 1)).isEqualTo(mask.getXBoard());
        }
    }

    @Nested
    class J {
        @Test
        void spawn() {
            Mino mino = new Mino(Piece.J, Rotate.Spawn);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.J, Mino::getPiece)
                    .returns(Rotate.Spawn, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{1, 0})
                    .contains(new int[]{-1, 1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "X_________" +
                    "XXX_______"
            );
            assertThat(mino.getMask(1, 0)).isEqualTo(mask.getXBoard());
        }

        @Test
        void left() {
            Mino mino = new Mino(Piece.J, Rotate.Left);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.J, Mino::getPiece)
                    .returns(Rotate.Left, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, 1})
                    .contains(new int[]{0, -1})
                    .contains(new int[]{-1, -1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "_X________" +
                    "_X________" +
                    "XX________"
            );
            assertThat(mino.getMask(1, 1)).isEqualTo(mask.getXBoard());
        }

        @Test
        void reverse() {
            Mino mino = new Mino(Piece.J, Rotate.Reverse);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Piece.J, Mino::getPiece)
                    .returns(Rotate.Reverse, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{1, 0})
                    .contains(new int[]{1, -1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "XXX_______" +
                    "__X_______"
            );
            assertThat(mino.getMask(1, 1)).isEqualTo(mask.getXBoard());
        }

        @Test
        void right() {
            Mino mino = new Mino(Piece.J, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.J, Mino::getPiece)
                    .returns(Rotate.Right, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, -1})
                    .contains(new int[]{0, 1})
                    .contains(new int[]{1, 1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "XX________" +
                    "X_________" +
                    "X_________"
            );
            assertThat(mino.getMask(0, 1)).isEqualTo(mask.getXBoard());
        }
    }

    @Nested
    class T {
        @Test
        void spawn() {
            Mino mino = new Mino(Piece.T, Rotate.Spawn);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.T, Mino::getPiece)
                    .returns(Rotate.Spawn, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{0, 1})
                    .contains(new int[]{0, 1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "_X________" +
                    "XXX_______"
            );
            assertThat(mino.getMask(1, 0)).isEqualTo(mask.getXBoard());
        }

        @Test
        void left() {
            Mino mino = new Mino(Piece.T, Rotate.Left);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.T, Mino::getPiece)
                    .returns(Rotate.Left, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{-1, 0})
                    .contains(new int[]{0, 1})
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, -1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "_X________" +
                    "XX________" +
                    "_X________"
            );
            assertThat(mino.getMask(1, 1)).isEqualTo(mask.getXBoard());
        }

        @Test
        void reverse() {
            Mino mino = new Mino(Piece.T, Rotate.Reverse);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Piece.T, Mino::getPiece)
                    .returns(Rotate.Reverse, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{1, 0})
                    .contains(new int[]{0, -1})
                    .contains(new int[]{0, -1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "XXX_______" +
                    "_X________"
            );
            assertThat(mino.getMask(1, 1)).isEqualTo(mask.getXBoard());
        }

        @Test
        void right() {
            Mino mino = new Mino(Piece.T, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Piece.T, Mino::getPiece)
                    .returns(Rotate.Right, Mino::getRotate);
            assertThat(mino.getPositions())
                    .contains(new int[]{0, 0})
                    .contains(new int[]{0, 1})
                    .contains(new int[]{1, 0})
                    .contains(new int[]{0, -1});

            SmallField mask = FieldFactory.createSmallField("" +
                    "X_________" +
                    "XX________" +
                    "X_________"
            );
            assertThat(mino.getMask(0, 1)).isEqualTo(mask.getXBoard());
        }
    }

    @Test
    void testEquals() {
        Mino mino1 = new Mino(Piece.I, Rotate.Spawn);
        Mino mino2 = new Mino(Piece.I, Rotate.Spawn);
        assertThat(mino1.equals(mino2)).isTrue();
    }

    @Test
    void testEqualsDiffRotate() {
        Mino mino1 = new Mino(Piece.I, Rotate.Spawn);
        Mino mino2 = new Mino(Piece.I, Rotate.Reverse);
        assertThat(mino1.equals(mino2)).isFalse();
    }

    @Test
    void testEqualsDiffBlock() {
        Mino mino1 = new Mino(Piece.I, Rotate.Spawn);
        Mino mino2 = new Mino(Piece.T, Rotate.Spawn);
        assertThat(mino1.equals(mino2)).isFalse();
    }

    @Test
    void testHashCode() {
        Mino mino1 = new Mino(Piece.I, Rotate.Spawn);
        Mino mino2 = new Mino(Piece.I, Rotate.Spawn);
        assertThat(mino1.hashCode()).isEqualTo(mino2.hashCode());

        Mino mino3 = new Mino(Piece.I, Rotate.Reverse);
        assertThat(mino1.hashCode()).isNotEqualTo(mino3.hashCode());

        Mino mino4 = new Mino(Piece.T, Rotate.Spawn);
        assertThat(mino1.hashCode()).isNotEqualTo(mino4.hashCode());
    }

    @Test
    void testHashCodeDiffBlock() {
        Mino mino1 = new Mino(Piece.I, Rotate.Spawn);
        Mino mino2 = new Mino(Piece.I, Rotate.Reverse);
        assertThat(mino1.hashCode()).isNotEqualTo(mino2.hashCode());
    }

    @Test
    void testHashCodeDiffRotate() {
        Mino mino1 = new Mino(Piece.I, Rotate.Spawn);
        Mino mino2 = new Mino(Piece.T, Rotate.Spawn);
        assertThat(mino1.hashCode()).isNotEqualTo(mino2.hashCode());
    }
}