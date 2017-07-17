package core.mino;

import core.field.FieldFactory;
import core.field.SmallField;
import core.srs.Rotate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MinoTest {
    @Nested
    class I {
        @Test
        void spawn() {
            Mino mino = new Mino(Block.I, Rotate.Spawn);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(2, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Block.I, Mino::getBlock)
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
            Mino mino = new Mino(Block.I, Rotate.Left);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(2, Mino::getMaxY)
                    .returns(Block.I, Mino::getBlock)
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
            Mino mino = new Mino(Block.I, Rotate.Reverse);
            assertThat(mino)
                    .returns(-2, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Block.I, Mino::getBlock)
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
            Mino mino = new Mino(Block.I, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-2, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.I, Mino::getBlock)
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
            Mino mino = new Mino(Block.O, Rotate.Spawn);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.O, Mino::getBlock)
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
            Mino mino = new Mino(Block.O, Rotate.Left);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.O, Mino::getBlock)
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
            Mino mino = new Mino(Block.O, Rotate.Reverse);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Block.O, Mino::getBlock)
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
            Mino mino = new Mino(Block.O, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Block.O, Mino::getBlock)
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
            Mino mino = new Mino(Block.S, Rotate.Spawn);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.S, Mino::getBlock)
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
            Mino mino = new Mino(Block.S, Rotate.Left);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.S, Mino::getBlock)
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
            Mino mino = new Mino(Block.S, Rotate.Reverse);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Block.S, Mino::getBlock)
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
            Mino mino = new Mino(Block.S, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.S, Mino::getBlock)
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
            Mino mino = new Mino(Block.Z, Rotate.Spawn);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.Z, Mino::getBlock)
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
            Mino mino = new Mino(Block.Z, Rotate.Left);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.Z, Mino::getBlock)
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
            Mino mino = new Mino(Block.Z, Rotate.Reverse);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Block.Z, Mino::getBlock)
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
            Mino mino = new Mino(Block.Z, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.Z, Mino::getBlock)
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
            Mino mino = new Mino(Block.L, Rotate.Spawn);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.L, Mino::getBlock)
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
            Mino mino = new Mino(Block.L, Rotate.Left);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.L, Mino::getBlock)
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
            Mino mino = new Mino(Block.L, Rotate.Reverse);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Block.L, Mino::getBlock)
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
            Mino mino = new Mino(Block.L, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.L, Mino::getBlock)
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
            Mino mino = new Mino(Block.J, Rotate.Spawn);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.J, Mino::getBlock)
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
            Mino mino = new Mino(Block.J, Rotate.Left);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.J, Mino::getBlock)
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
            Mino mino = new Mino(Block.J, Rotate.Reverse);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Block.J, Mino::getBlock)
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
            Mino mino = new Mino(Block.J, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.J, Mino::getBlock)
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
            Mino mino = new Mino(Block.T, Rotate.Spawn);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(0, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.T, Mino::getBlock)
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
            Mino mino = new Mino(Block.T, Rotate.Left);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(0, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.T, Mino::getBlock)
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
            Mino mino = new Mino(Block.T, Rotate.Reverse);
            assertThat(mino)
                    .returns(-1, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(0, Mino::getMaxY)
                    .returns(Block.T, Mino::getBlock)
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
            Mino mino = new Mino(Block.T, Rotate.Right);
            assertThat(mino)
                    .returns(0, Mino::getMinX)
                    .returns(1, Mino::getMaxX)
                    .returns(-1, Mino::getMinY)
                    .returns(1, Mino::getMaxY)
                    .returns(Block.T, Mino::getBlock)
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
}