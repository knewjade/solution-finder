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
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.I, 1, 0, Rotate.Spawn);
            assertThat(fromSpawn)
                    .hasSize(1)
                    .contains(MinimalAction.create(2, 0, Rotate.Reverse));

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.I, 2, 0, Rotate.Reverse);
            assertThat(fromReverse)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 0, Rotate.Spawn));

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.I, 0, 1, Rotate.Left);
            assertThat(fromLeft)
                    .hasSize(1)
                    .contains(MinimalAction.create(0, 2, Rotate.Right));

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.I, 0, 2, Rotate.Right);
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
            Action fromSpawn = shifter.createTransformedAction(Block.S, 1, 0, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Block.S, 1, 1, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromLeft = shifter.createTransformedAction(Block.S, 1, 1, Rotate.Left);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Block.S, 0, 1, Rotate.Right);
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
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.S, 1, 0, Rotate.Spawn);
            assertThat(fromSpawn)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 1, Rotate.Reverse));

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.S, 1, 1, Rotate.Reverse);
            assertThat(fromReverse)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 0, Rotate.Spawn));

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.S, 1, 1, Rotate.Left);
            assertThat(fromLeft)
                    .hasSize(1)
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.S, 0, 1, Rotate.Right);
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
            Action fromSpawn = shifter.createTransformedAction(Block.Z, 1, 0, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Block.Z, 1, 1, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromLeft = shifter.createTransformedAction(Block.Z, 1, 1, Rotate.Left);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(0, 1, Rotate.Right));

            Action fromRight = shifter.createTransformedAction(Block.Z, 0, 1, Rotate.Right);
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
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.Z, 1, 0, Rotate.Spawn);
            assertThat(fromSpawn)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 1, Rotate.Reverse));

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.Z, 1, 1, Rotate.Reverse);
            assertThat(fromReverse)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 0, Rotate.Spawn));

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.Z, 1, 1, Rotate.Left);
            assertThat(fromLeft)
                    .hasSize(1)
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.Z, 0, 1, Rotate.Right);
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
            Action fromSpawn = shifter.createTransformedAction(Block.O, 0, 0, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(0, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Block.O, 1, 1, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(0, 0, Rotate.Spawn));

            Action fromLeft = shifter.createTransformedAction(Block.O, 1, 0, Rotate.Left);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(0, 0, Rotate.Spawn));

            Action fromRight = shifter.createTransformedAction(Block.O, 0, 1, Rotate.Right);
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
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.O, 0, 0, Rotate.Spawn);
            assertThat(fromSpawn)
                    .hasSize(3)
                    .contains(MinimalAction.create(1, 1, Rotate.Reverse))
                    .contains(MinimalAction.create(1, 0, Rotate.Left))
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.O, 1, 1, Rotate.Reverse);
            assertThat(fromReverse)
                    .hasSize(3)
                    .contains(MinimalAction.create(0, 0, Rotate.Spawn))
                    .contains(MinimalAction.create(1, 0, Rotate.Left))
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.O, 1, 0, Rotate.Left);
            assertThat(fromLeft)
                    .hasSize(3)
                    .contains(MinimalAction.create(0, 0, Rotate.Spawn))
                    .contains(MinimalAction.create(1, 1, Rotate.Reverse))
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.O, 0, 1, Rotate.Right);
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
            Action fromSpawn = shifter.createTransformedAction(Block.T, 1, 0, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Block.T, 1, 1, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 1, Rotate.Reverse));

            Action fromLeft = shifter.createTransformedAction(Block.T, 1, 1, Rotate.Left);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Block.T, 0, 1, Rotate.Right);
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
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.T, 1, 0, Rotate.Spawn);
            assertThat(fromSpawn).isEmpty();

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.T, 1, 1, Rotate.Reverse);
            assertThat(fromReverse).isEmpty();

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.T, 1, 1, Rotate.Left);
            assertThat(fromLeft).isEmpty();

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.T, 0, 1, Rotate.Right);
            assertThat(fromRight).isEmpty();
        }
    }

    @Nested
    class L {
        @Test
        void createTransformedAction() throws Exception {
            MinoShifter shifter = new MinoShifter();
            Action fromSpawn = shifter.createTransformedAction(Block.L, 1, 0, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Block.L, 1, 1, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 1, Rotate.Reverse));

            Action fromLeft = shifter.createTransformedAction(Block.L, 1, 1, Rotate.Left);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Block.L, 0, 1, Rotate.Right);
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
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.L, 1, 0, Rotate.Spawn);
            assertThat(fromSpawn).isEmpty();

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.L, 1, 1, Rotate.Reverse);
            assertThat(fromReverse).isEmpty();

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.L, 1, 1, Rotate.Left);
            assertThat(fromLeft).isEmpty();

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.L, 0, 1, Rotate.Right);
            assertThat(fromRight).isEmpty();
        }
    }

    @Nested
    class J {
        @Test
        void createTransformedAction() throws Exception {
            MinoShifter shifter = new MinoShifter();
            Action fromSpawn = shifter.createTransformedAction(Block.J, 1, 0, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Block.J, 1, 1, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 1, Rotate.Reverse));

            Action fromLeft = shifter.createTransformedAction(Block.J, 1, 1, Rotate.Left);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Block.J, 0, 1, Rotate.Right);
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
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.J, 1, 0, Rotate.Spawn);
            assertThat(fromSpawn).isEmpty();

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.J, 1, 1, Rotate.Reverse);
            assertThat(fromReverse).isEmpty();

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.J, 1, 1, Rotate.Left);
            assertThat(fromLeft).isEmpty();

            List<Action> fromRight = shifter.enumerateSameOtherActions(Block.J, 0, 1, Rotate.Right);
            assertThat(fromRight).isEmpty();
        }
    }
}