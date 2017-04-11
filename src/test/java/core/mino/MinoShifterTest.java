package core.mino;

import core.srs.Rotate;
import org.junit.Test;
import searcher.common.action.Action;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MinoShifterTest {
    @Test
    public void createTransformedActionI() throws Exception {
        MinoShifter shifter = new MinoShifter();
        Action fromSpawn = shifter.createTransformedAction(Block.I, 1, 0, Rotate.Spawn);
        assertAction(fromSpawn, 1, 0, Rotate.Spawn);

        Action fromReverse = shifter.createTransformedAction(Block.I, 2, 0, Rotate.Reverse);
        assertAction(fromReverse, 1, 0, Rotate.Spawn);

        Action fromLeft = shifter.createTransformedAction(Block.I, 0, 1, Rotate.Left);
        assertAction(fromLeft, 0, 1, Rotate.Left);

        Action fromRight = shifter.createTransformedAction(Block.I, 0, 2, Rotate.Right);
        assertAction(fromRight, 0, 1, Rotate.Left);
    }

    private void assertAction(Action actual, int x, int y, Rotate rotate) {
        assertThat(actual.getX(), is(x));
        assertThat(actual.getY(), is(y));
        assertThat(actual.getRotate(), is(rotate));
    }

    @Test
    public void createTransformedActionS() throws Exception {
        MinoShifter shifter = new MinoShifter();
        Action fromSpawn = shifter.createTransformedAction(Block.S, 1, 0, Rotate.Spawn);
        assertAction(fromSpawn, 1, 0, Rotate.Spawn);

        Action fromReverse = shifter.createTransformedAction(Block.S, 1, 1, Rotate.Reverse);
        assertAction(fromReverse, 1, 0, Rotate.Spawn);

        Action fromLeft = shifter.createTransformedAction(Block.S, 1, 1, Rotate.Left);
        assertAction(fromLeft, 1, 1, Rotate.Left);

        Action fromRight = shifter.createTransformedAction(Block.S, 0, 1, Rotate.Right);
        assertAction(fromRight, 1, 1, Rotate.Left);
    }

    @Test
    public void createTransformedActionO() throws Exception {
        MinoShifter shifter = new MinoShifter();
        Action fromSpawn = shifter.createTransformedAction(Block.O, 0, 0, Rotate.Spawn);
        assertAction(fromSpawn, 0, 0, Rotate.Spawn);

        Action fromReverse = shifter.createTransformedAction(Block.O, 1, 1, Rotate.Reverse);
        assertAction(fromReverse, 0, 0, Rotate.Spawn);

        Action fromLeft = shifter.createTransformedAction(Block.O, 1, 0, Rotate.Left);
        assertAction(fromLeft, 0, 0, Rotate.Spawn);

        Action fromRight = shifter.createTransformedAction(Block.O, 0, 1, Rotate.Right);
        assertAction(fromRight, 0, 0, Rotate.Spawn);
    }

    @Test
    public void enumerateSameOtherActionsI() throws Exception {
        MinoShifter shifter = new MinoShifter();
        List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.I, 1, 0, Rotate.Spawn);
        assertThat(fromSpawn.size(), is(1));
        assertAction(fromSpawn.get(0), 2, 0, Rotate.Reverse);

        List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.I, 2, 0, Rotate.Reverse);
        assertThat(fromReverse.size(), is(1));
        assertAction(fromReverse.get(0), 1, 0, Rotate.Spawn);

        List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.I, 0, 1, Rotate.Left);
        assertThat(fromLeft.size(), is(1));
        assertAction(fromLeft.get(0), 0, 2, Rotate.Right);

        List<Action> fromRight = shifter.enumerateSameOtherActions(Block.I, 0, 2, Rotate.Right);
        assertThat(fromRight.size(), is(1));
        assertAction(fromRight.get(0), 0, 1, Rotate.Left);
    }

    @Test
    public void enumerateSameOtherActionsO() throws Exception {
        MinoShifter shifter = new MinoShifter();
        List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.O, 0, 0, Rotate.Spawn);
        assertThat(fromSpawn.size(), is(3));

        List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.O, 1, 1, Rotate.Reverse);
        assertThat(fromReverse.size(), is(3));

        List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.O, 1, 0, Rotate.Left);
        assertThat(fromLeft.size(), is(3));

        List<Action> fromRight = shifter.enumerateSameOtherActions(Block.O, 0, 1, Rotate.Right);
        assertThat(fromRight.size(), is(3));
    }

    @Test
    public void enumerateSameOtherActionsT() throws Exception {
        MinoShifter shifter = new MinoShifter();
        List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.T, 1, 0, Rotate.Spawn);
        assertThat(fromSpawn.size(), is(0));

        List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.T, 1, 1, Rotate.Reverse);
        assertThat(fromReverse.size(), is(0));

        List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.T, 1, 1, Rotate.Left);
        assertThat(fromLeft.size(), is(0));

        List<Action> fromRight = shifter.enumerateSameOtherActions(Block.T, 0, 1, Rotate.Right);
        assertThat(fromRight.size(), is(0));
    }

    @Test
    public void enumerateSameOtherActionsL() throws Exception {
        MinoShifter shifter = new MinoShifter();
        List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.L, 1, 0, Rotate.Spawn);
        assertThat(fromSpawn.size(), is(0));

        List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.L, 1, 1, Rotate.Reverse);
        assertThat(fromReverse.size(), is(0));

        List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.L, 1, 1, Rotate.Left);
        assertThat(fromLeft.size(), is(0));

        List<Action> fromRight = shifter.enumerateSameOtherActions(Block.L, 0, 1, Rotate.Right);
        assertThat(fromRight.size(), is(0));
    }

    @Test
    public void enumerateSameOtherActionsJ() throws Exception {
        MinoShifter shifter = new MinoShifter();
        List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.J, 1, 0, Rotate.Spawn);
        assertThat(fromSpawn.size(), is(0));

        List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.J, 1, 1, Rotate.Reverse);
        assertThat(fromReverse.size(), is(0));

        List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.J, 1, 1, Rotate.Left);
        assertThat(fromLeft.size(), is(0));

        List<Action> fromRight = shifter.enumerateSameOtherActions(Block.J, 0, 1, Rotate.Right);
        assertThat(fromRight.size(), is(0));
    }

    @Test
    public void enumerateSameOtherActionsS() throws Exception {
        MinoShifter shifter = new MinoShifter();
        List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.S, 1, 0, Rotate.Spawn);
        assertThat(fromSpawn.size(), is(1));
        assertAction(fromSpawn.get(0), 1, 1, Rotate.Reverse);

        List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.S, 1, 1, Rotate.Reverse);
        assertThat(fromReverse.size(), is(1));
        assertAction(fromReverse.get(0), 1, 0, Rotate.Spawn);

        List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.S, 1, 1, Rotate.Left);
        assertThat(fromLeft.size(), is(1));
        assertAction(fromLeft.get(0), 0, 1, Rotate.Right);

        List<Action> fromRight = shifter.enumerateSameOtherActions(Block.S, 0, 1, Rotate.Right);
        assertThat(fromRight.size(), is(1));
        assertAction(fromRight.get(0), 1, 1, Rotate.Left);
    }

    @Test
    public void enumerateSameOtherActionsZ() throws Exception {
        MinoShifter shifter = new MinoShifter();
        List<Action> fromSpawn = shifter.enumerateSameOtherActions(Block.Z, 1, 0, Rotate.Spawn);
        assertThat(fromSpawn.size(), is(1));
        assertAction(fromSpawn.get(0), 1, 1, Rotate.Reverse);

        List<Action> fromReverse = shifter.enumerateSameOtherActions(Block.Z, 1, 1, Rotate.Reverse);
        assertThat(fromReverse.size(), is(1));
        assertAction(fromReverse.get(0), 1, 0, Rotate.Spawn);

        List<Action> fromLeft = shifter.enumerateSameOtherActions(Block.Z, 1, 1, Rotate.Left);
        assertThat(fromLeft.size(), is(1));
        assertAction(fromLeft.get(0), 0, 1, Rotate.Right);

        List<Action> fromRight = shifter.enumerateSameOtherActions(Block.Z, 0, 1, Rotate.Right);
        assertThat(fromRight.size(), is(1));
        assertAction(fromRight.get(0), 1, 1, Rotate.Left);
    }
}