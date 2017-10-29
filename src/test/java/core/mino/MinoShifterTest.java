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
            Action fromSpawn = shifter.createTransformedAction(Piece.I, MinimalAction.create(1, 0, Rotate.Spawn));
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Piece.I, MinimalAction.create(2, 0, Rotate.Reverse));
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromLeft = shifter.createTransformedAction(Piece.I, MinimalAction.create(0, 1, Rotate.Left));
            assertThat(fromLeft).isEqualTo(MinimalAction.create(0, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Piece.I, MinimalAction.create(0, 2, Rotate.Right));
            assertThat(fromRight).isEqualTo(MinimalAction.create(0, 1, Rotate.Left));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Piece.I, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Piece.I, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Spawn);

            Rotate fromLeft = shifter.createTransformedRotate(Piece.I, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Left);

            Rotate fromRight = shifter.createTransformedRotate(Piece.I, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Left);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Piece.I, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn)
                    .hasSize(1)
                    .contains(MinimalAction.create(2, 0, Rotate.Reverse));

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Piece.I, Rotate.Reverse, 2, 0);
            assertThat(fromReverse)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 0, Rotate.Spawn));

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Piece.I, Rotate.Left, 0, 1);
            assertThat(fromLeft)
                    .hasSize(1)
                    .contains(MinimalAction.create(0, 2, Rotate.Right));

            List<Action> fromRight = shifter.enumerateSameOtherActions(Piece.I, Rotate.Right, 0, 2);
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
            Action fromSpawn = shifter.createTransformedAction(Piece.S, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Piece.S, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromLeft = shifter.createTransformedAction(Piece.S, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Piece.S, Rotate.Right, 0, 1);
            assertThat(fromRight).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Piece.S, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Piece.S, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Spawn);

            Rotate fromLeft = shifter.createTransformedRotate(Piece.S, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Left);

            Rotate fromRight = shifter.createTransformedRotate(Piece.S, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Left);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Piece.S, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 1, Rotate.Reverse));

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Piece.S, Rotate.Reverse, 1, 1);
            assertThat(fromReverse)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 0, Rotate.Spawn));

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Piece.S, Rotate.Left, 1, 1);
            assertThat(fromLeft)
                    .hasSize(1)
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromRight = shifter.enumerateSameOtherActions(Piece.S, Rotate.Right, 0, 1);
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
            Action fromSpawn = shifter.createTransformedAction(Piece.Z, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Piece.Z, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromLeft = shifter.createTransformedAction(Piece.Z, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(0, 1, Rotate.Right));

            Action fromRight = shifter.createTransformedAction(Piece.Z, Rotate.Right, 0, 1);
            assertThat(fromRight).isEqualTo(MinimalAction.create(0, 1, Rotate.Right));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Piece.Z, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Piece.Z, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Spawn);

            Rotate fromLeft = shifter.createTransformedRotate(Piece.Z, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Right);

            Rotate fromRight = shifter.createTransformedRotate(Piece.Z, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Right);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Piece.Z, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 1, Rotate.Reverse));

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Piece.Z, Rotate.Reverse, 1, 1);
            assertThat(fromReverse)
                    .hasSize(1)
                    .contains(MinimalAction.create(1, 0, Rotate.Spawn));

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Piece.Z, Rotate.Left, 1, 1);
            assertThat(fromLeft)
                    .hasSize(1)
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromRight = shifter.enumerateSameOtherActions(Piece.Z, Rotate.Right, 0, 1);
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
            Action fromSpawn = shifter.createTransformedAction(Piece.O, Rotate.Spawn, 0, 0);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(0, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Piece.O, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(0, 0, Rotate.Spawn));

            Action fromLeft = shifter.createTransformedAction(Piece.O, Rotate.Left, 1, 0);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(0, 0, Rotate.Spawn));

            Action fromRight = shifter.createTransformedAction(Piece.O, Rotate.Right, 0, 1);
            assertThat(fromRight).isEqualTo(MinimalAction.create(0, 0, Rotate.Spawn));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Piece.O, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Piece.O, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Spawn);

            Rotate fromLeft = shifter.createTransformedRotate(Piece.O, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Spawn);

            Rotate fromRight = shifter.createTransformedRotate(Piece.O, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Spawn);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Piece.O, Rotate.Spawn, 0, 0);
            assertThat(fromSpawn)
                    .hasSize(3)
                    .contains(MinimalAction.create(1, 1, Rotate.Reverse))
                    .contains(MinimalAction.create(1, 0, Rotate.Left))
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Piece.O, Rotate.Reverse, 1, 1);
            assertThat(fromReverse)
                    .hasSize(3)
                    .contains(MinimalAction.create(0, 0, Rotate.Spawn))
                    .contains(MinimalAction.create(1, 0, Rotate.Left))
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Piece.O, Rotate.Left, 1, 0);
            assertThat(fromLeft)
                    .hasSize(3)
                    .contains(MinimalAction.create(0, 0, Rotate.Spawn))
                    .contains(MinimalAction.create(1, 1, Rotate.Reverse))
                    .contains(MinimalAction.create(0, 1, Rotate.Right));

            List<Action> fromRight = shifter.enumerateSameOtherActions(Piece.O, Rotate.Right, 0, 1);
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
            Action fromSpawn = shifter.createTransformedAction(Piece.T, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Piece.T, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 1, Rotate.Reverse));

            Action fromLeft = shifter.createTransformedAction(Piece.T, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Piece.T, Rotate.Right, 0, 1);
            assertThat(fromRight).isEqualTo(MinimalAction.create(0, 1, Rotate.Right));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Piece.T, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Piece.T, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Reverse);

            Rotate fromLeft = shifter.createTransformedRotate(Piece.T, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Left);

            Rotate fromRight = shifter.createTransformedRotate(Piece.T, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Right);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Piece.T, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEmpty();

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Piece.T, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEmpty();

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Piece.T, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEmpty();

            List<Action> fromRight = shifter.enumerateSameOtherActions(Piece.T, Rotate.Right, 0, 1);
            assertThat(fromRight).isEmpty();
        }
    }

    @Nested
    class L {
        @Test
        void createTransformedAction() throws Exception {
            MinoShifter shifter = new MinoShifter();
            Action fromSpawn = shifter.createTransformedAction(Piece.L, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Piece.L, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 1, Rotate.Reverse));

            Action fromLeft = shifter.createTransformedAction(Piece.L, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Piece.L, Rotate.Right, 0, 1);
            assertThat(fromRight).isEqualTo(MinimalAction.create(0, 1, Rotate.Right));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Piece.L, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Piece.L, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Reverse);

            Rotate fromLeft = shifter.createTransformedRotate(Piece.L, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Left);

            Rotate fromRight = shifter.createTransformedRotate(Piece.L, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Right);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Piece.L, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEmpty();

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Piece.L, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEmpty();

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Piece.L, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEmpty();

            List<Action> fromRight = shifter.enumerateSameOtherActions(Piece.L, Rotate.Right, 0, 1);
            assertThat(fromRight).isEmpty();
        }
    }

    @Nested
    class J {
        @Test
        void createTransformedAction() throws Exception {
            MinoShifter shifter = new MinoShifter();
            Action fromSpawn = shifter.createTransformedAction(Piece.J, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEqualTo(MinimalAction.create(1, 0, Rotate.Spawn));

            Action fromReverse = shifter.createTransformedAction(Piece.J, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEqualTo(MinimalAction.create(1, 1, Rotate.Reverse));

            Action fromLeft = shifter.createTransformedAction(Piece.J, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEqualTo(MinimalAction.create(1, 1, Rotate.Left));

            Action fromRight = shifter.createTransformedAction(Piece.J, Rotate.Right, 0, 1);
            assertThat(fromRight).isEqualTo(MinimalAction.create(0, 1, Rotate.Right));
        }

        @Test
        void createTransformedRotate() {
            MinoShifter shifter = new MinoShifter();
            Rotate fromSpawn = shifter.createTransformedRotate(Piece.J, Rotate.Spawn);
            assertThat(fromSpawn).isEqualTo(Rotate.Spawn);

            Rotate fromReverse = shifter.createTransformedRotate(Piece.J, Rotate.Reverse);
            assertThat(fromReverse).isEqualTo(Rotate.Reverse);

            Rotate fromLeft = shifter.createTransformedRotate(Piece.J, Rotate.Left);
            assertThat(fromLeft).isEqualTo(Rotate.Left);

            Rotate fromRight = shifter.createTransformedRotate(Piece.J, Rotate.Right);
            assertThat(fromRight).isEqualTo(Rotate.Right);
        }

        @Test
        void enumerateSameOtherActions() throws Exception {
            MinoShifter shifter = new MinoShifter();
            List<Action> fromSpawn = shifter.enumerateSameOtherActions(Piece.J, Rotate.Spawn, 1, 0);
            assertThat(fromSpawn).isEmpty();

            List<Action> fromReverse = shifter.enumerateSameOtherActions(Piece.J, Rotate.Reverse, 1, 1);
            assertThat(fromReverse).isEmpty();

            List<Action> fromLeft = shifter.enumerateSameOtherActions(Piece.J, Rotate.Left, 1, 1);
            assertThat(fromLeft).isEmpty();

            List<Action> fromRight = shifter.enumerateSameOtherActions(Piece.J, Rotate.Right, 0, 1);
            assertThat(fromRight).isEmpty();
        }
    }
}