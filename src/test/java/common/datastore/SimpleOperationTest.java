package common.datastore;

import core.mino.Block;
import core.srs.Rotate;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class SimpleOperationTest {
    @Test
    public void testGetter() throws Exception {
        Operation operation = new SimpleOperation(Block.T, Rotate.Spawn, 4, 5);
        assertThat(operation.getBlock(), is(Block.T));
        assertThat(operation.getRotate(), is(Rotate.Spawn));
        assertThat(operation.getX(), is(4));
        assertThat(operation.getY(), is(5));
    }

    @Test
    public void testEqual() throws Exception {
        Operation operation = new SimpleOperation(Block.T, Rotate.Spawn, 4, 5);
        assertThat(operation.equals(new SimpleOperation(Block.T, Rotate.Spawn, 4, 5)), is(true));
        assertThat(operation.equals(new SimpleOperation(Block.L, Rotate.Spawn, 4, 5)), is(false));
        assertThat(operation.equals(new SimpleOperation(Block.T, Rotate.Left, 4, 5)), is(false));
        assertThat(operation.equals(new SimpleOperation(Block.T, Rotate.Spawn, 3, 5)), is(false));
        assertThat(operation.equals(new SimpleOperation(Block.T, Rotate.Spawn, 4, 6)), is(false));
    }

    @Test
    public void testHashCode() throws Exception {
        Operation operation = new SimpleOperation(Block.T, Rotate.Spawn, 4, 5);
        assertThat(new SimpleOperation(Block.T, Rotate.Spawn, 4, 5).hashCode(), is(operation.hashCode()));
        assertThat(new SimpleOperation(Block.L, Rotate.Spawn, 4, 5).hashCode(), is(not(operation.hashCode())));
        assertThat(new SimpleOperation(Block.T, Rotate.Left, 4, 5).hashCode(), is(not(operation.hashCode())));
        assertThat(new SimpleOperation(Block.T, Rotate.Spawn, 3, 5).hashCode(), is(not(operation.hashCode())));
        assertThat(new SimpleOperation(Block.T, Rotate.Spawn, 4, 6).hashCode(), is(not(operation.hashCode())));
    }

    @Test
    public void testCompareTo() throws Exception {
        Operation operation1 = new SimpleOperation(Block.T, Rotate.Spawn, 4, 5);
        SimpleOperation operation2 = new SimpleOperation(Block.T, Rotate.Spawn, 4, 5);
        SimpleOperation operation3 = new SimpleOperation(Block.T, Rotate.Spawn, 4, 13);
        SimpleOperation operation4 = new SimpleOperation(Block.T, Rotate.Spawn, 5, 13);

        assertThat(Operation.compareTo(operation1, operation2), is(0));

        assertThat(Operation.compareTo(operation1, operation3), is(not(0)));
        assertThat(Operation.compareTo(operation1, operation4), is(not(0)));
        assertThat(Operation.compareTo(operation3, operation4), is(not(0)));

        assert Operation.compareTo(operation1, operation3) < 0 && Operation.compareTo(operation3, operation4) < 0;
        assertThat(Operation.compareTo(operation1, operation4), is(lessThan(0)));
    }
}