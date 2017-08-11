package core.mino;

import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.srs.Rotate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MinoShifterTest {
    @Nested
    class I {
        @Test
        void createTransformedAction() throws Exception {
            MinoShifter shifter = new MinoShifter();
            Action fromSpawn = shifter.createTransformedAction(Block.I, MinimalAction.create(1, 0, Rotate.Spawn));
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Block.I, MinimalAction.create(2, 0, Rotate.Reverse));
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromLeft = shifter.createTransformedAction(Block.I, MinimalAction.create(0, 1, Rotate.Left));
            assertThat(fromLeft).isEqualTo(MinimalAction.create(0, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Block.I, MinimalAction.create(0, 2, Rotate.Right));
            assertThat(fromRight).isEqualTo(MinimalAction.create(0, 1, Rotate.Left));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Block.I, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Block.I, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Spawn);

            Rotate fromLeft = shifter.createTransformedRotate(Block.I, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Left);

            Rotate fromRight = shifter.createTransformedRotate(Block.I, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Left);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.I, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn)
                    .hasSize(1)
                    .contains(MinimalAction.create(2, 0, Rotate.Reverse));

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.I, Rotate.Reverse, 2, 0);
            assertThat(fromReverse)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 0, Rotate.Spawn));

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.I, Rotate.Left, 0, 1);
            assertThat(fromLeft)
                    .hasSize(1)
                    .contains(MinimalAction.create(0, 2, Rotate.Right));

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.I, Rotate.Right, 0, 2);
            assertThat(fromRight)
                    .hasSize(1)
                    .contains(MinimalAction.create(0, 1, Rotate.Left));
        }
    }

    @Nested
    class S {
        @Test
        void createTransformedAction() throws Exception {
            MinoShifter shifter = new MinoShifter();
            Action fromSpawn = shifter.createTransformedAction(Block.S, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Block.S, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromLeft = shifter.createTransformedAction(Block.S, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Block.S, Rotate.Right, 0, 1);
            assertThat(fromRight).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Block.S, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Block.S, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Spawn);

            Rotate fromLeft = shifter.createTransformedRotate(Block.S, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Left);

            Rotate fromRight = shifter.createTransformedRotate(Block.S, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Left);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.S, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 1, Rotate.Reverse));

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.S, Rotate.Reverse, 1, 1);
            assertThat(fromReverse)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 0, Rotate.Spawn));

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.S, Rotate.Left, 1, 1);
            assertThat(fromLeft)
                    .hasSize(1)
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.S, Rotate.Right, 0, 1);
            assertThat(fromRight)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 1, Rotate.Left));
        }
    }

    @Nested
    class Z {
        @Test
        void createTransformedAction() throws Exception {
            MinoShifter shifter = new MinoShifter();
            Action fromSpawn = shifter.createTransformedAction(Block.Z, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Block.Z, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromLeft = shifter.createTransformedAction(Block.Z, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(0, 1, Rotate.Right));

            Action fromRight = shifter.createTransformedAction(Block.Z, Rotate.Right, 0, 1);
            assertThat(fromRight).isEqualTo(MinimalAction.create(0, 1, Rotate.Right));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Block.Z, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Block.Z, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Spawn);

            Rotate fromLeft = shifter.createTransformedRotate(Block.Z, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Right);

            Rotate fromRight = shifter.createTransformedRotate(Block.Z, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Right);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.Z, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 1, Rotate.Reverse));

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.Z, Rotate.Reverse, 1, 1);
            assertThat(fromReverse)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 0, Rotate.Spawn));

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.Z, Rotate.Left, 1, 1);
            assertThat(fromLeft)
                    .hasSize(1)
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.Z, Rotate.Right, 0, 1);
            assertThat(fromRight)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 1, Rotate.Left));
        }
    }

    @Nested
    class O {
        @Test
        void createTransformedAction() throws Exception {
            MinoShifter shifter = new MinoShifter();
            Action fromSpawn = shifter.createTransformedAction(Block.O, Rotate.Spawn, 0, 0);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(0, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Block.O, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(0, 0, Rotate.Spawn));

            Action fromLeft = shifter.createTransformedAction(Block.O, Rotate.Left, 1, 0);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(0, 0, Rotate.Spawn));

            Action fromRight = shifter.createTransformedAction(Block.O, Rotate.Right, 0, 1);
            assertThat(fromRight).isEqualTo(MinimalAction.create(0, 0, Rotate.Spawn));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Block.O, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Block.O, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Spawn);

            Rotate fromLeft = shifter.createTransformedRotate(Block.O, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Spawn);

            Rotate fromRight = shifter.createTransformedRotate(Block.O, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Spawn);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.O, Rotate.Spawn, 0, 0);
            assertThat(fromSpawn)
                    .hasSize(3)
                    .contains(MinimalAction.create(1, 1, Rotate.Reverse))
                    .contains(MinimalAction.create(1, 0, Rotate.Left))
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.O, Rotate.Reverse, 1, 1);
            assertThat(fromReverse)
                    .hasSize(3)
                    .contains(MinimalAction.create(0, 0, Rotate.Spawn))
                    .contains(MinimalAction.create(1, 0, Rotate.Left))
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.O, Rotate.Left, 1, 0);
            assertThat(fromLeft)
                    .hasSize(3)
                    .contains(MinimalAction.create(0, 0, Rotate.Spawn))
                    .contains(MinimalAction.create(1, 1, Rotate.Reverse))
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.O, Rotate.Right, 0, 1);
            assertThat(fromRight)
                    .hasSize(3)
                    .contains(MinimalAction.create(0, 0, Rotate.Spawn))
                    .contains(MinimalAction.create(1, 1, Rotate.Reverse))
                    .contains(MinimalAction.create(1, 0, Rotate.Left));
        }
    }

    @Nested
    class T {
        @Test
        void createTransformedAction() throws Exception {
            MinoShifter shifter = new MinoShifter();
            Action fromSpawn = shifter.createTransformedAction(Block.T, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Block.T, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 1, Rotate.Reverse));

            Action fromLeft = shifter.createTransformedAction(Block.T, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Block.T, Rotate.Right, 0, 1);
            assertThat(fromRight).isEqualTo(MinimalAction.create(0, 1, Rotate.Right));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Block.T, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Block.T, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Reverse);

            Rotate fromLeft = shifter.createTransformedRotate(Block.T, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Left);

            Rotate fromRight = shifter.createTransformedRotate(Block.T, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Right);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.T, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEmpty();

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.T, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEmpty();

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.T, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEmpty();

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.T, Rotate.Right, 0, 1);
            assertThat(fromRight).isEmpty();
        }
    }

    @Nested
    class L {
        @Test
        void createTransformedAction() throws Exception {
            MinoShifter shifter = new MinoShifter();
            Action fromSpawn = shifter.createTransformedAction(Block.L, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Block.L, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 1, Rotate.Reverse));

            Action fromLeft = shifter.createTransformedAction(Block.L, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Block.L, Rotate.Right, 0, 1);
            assertThat(fromRight).isEqualTo(MinimalAction.create(0, 1, Rotate.Right));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Block.L, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Block.L, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Reverse);

            Rotate fromLeft = shifter.createTransformedRotate(Block.L, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Left);

            Rotate fromRight = shifter.createTransformedRotate(Block.L, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Right);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.L, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEmpty();

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.L, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEmpty();

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.L, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEmpty();

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.L, Rotate.Right, 0, 1);
            assertThat(fromRight).isEmpty();
        }
    }

    @Nested
    class J {
        @Test
        void createTransformedAction() throws Exception {
            MinoShifter shifter = new MinoShifter();
            Action fromSpawn = shifter.createTransformedAction(Block.J, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Block.J, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 1, Rotate.Reverse));

            Action fromLeft = shifter.createTransformedAction(Block.J, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Block.J, Rotate.Right, 0, 1);
            assertThat(fromRight).isEqualTo(MinimalAction.create(0, 1, Rotate.Right));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Block.J, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Block.J, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Reverse);

            Rotate fromLeft = shifter.createTransformedRotate(Block.J, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Left);

            Rotate fromRight = shifter.createTransformedRotate(Block.J, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Right);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.J, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEmpty();

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.J, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEmpty();

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.J, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEmpty();

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.J, Rotate.Right, 0, 1);
            assertThat(fromRight).isEmpty();
        }
    }
}