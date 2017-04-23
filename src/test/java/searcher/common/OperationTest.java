package searcher.common;

import core.mino.Block;
import core.srs.Rotate;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class OperationTest {
    @Test
    public void testGetter() throws Exception {
        Operation operation = new Operation(Block.T, Rotate.Spawn, 4, 5);
        assertThat(operation.getBlock(), is(Block.T));
        assertThat(operation.getRotate(), is(Rotate.Spawn));
        assertThat(operation.getX(), is(4));
        assertThat(operation.getY(), is(5));
    }

    @Test
    public void testEqual() throws Exception {
        Operation operation = new Operation(Block.T, Rotate.Spawn, 4, 5);
        assertThat(operation.equals(new Operation(Block.T, Rotate.Spawn, 4, 5)), is(true));
        assertThat(operation.equals(new Operation(Block.L, Rotate.Spawn, 4, 5)), is(false));
        assertThat(operation.equals(new Operation(Block.T, Rotate.Left, 4, 5)), is(false));
        assertThat(operation.equals(new Operation(Block.T, Rotate.Spawn, 3, 5)), is(false));
        assertThat(operation.equals(new Operation(Block.T, Rotate.Spawn, 4, 6)), is(false));
    }

    @Test
    public void testHashCode() throws Exception {
        Operation operation = new Operation(Block.T, Rotate.Spawn, 4, 5);
        assertThat(new Operation(Block.T, Rotate.Spawn, 4, 5).hashCode(), is(operation.hashCode()));
        assertThat(new Operation(Block.L, Rotate.Spawn, 4, 5).hashCode(), is(not(operation.hashCode())));
        assertThat(new Operation(Block.T, Rotate.Left, 4, 5).hashCode(), is(not(operation.hashCode())));
        assertThat(new Operation(Block.T, Rotate.Spawn, 3, 5).hashCode(), is(not(operation.hashCode())));
        assertThat(new Operation(Block.T, Rotate.Spawn, 4, 6).hashCode(), is(not(operation.hashCode())));
    }

    @Test
    public void testCompareTo() throws Exception {
        Operation operation1 = new Operation(Block.T, Rotate.Spawn, 4, 5);
        Operation operation2 = new Operation(Block.T, Rotate.Spawn, 4, 5);
        Operation operation3 = new Operation(Block.T, Rotate.Spawn, 4, 13);
        Operation operation4 = new Operation(Block.T, Rotate.Spawn, 5, 13);

        assertThat(operation1.compareTo(operation2), is(0));

        assertThat(operation1.compareTo(operation3), is(not(0)));
        assertThat(operation1.compareTo(operation4), is(not(0)));
        assertThat(operation3.compareTo(operation4), is(not(0)));

        assert operation1.compareTo(operation3) < 0 && operation3.compareTo(operation4) < 0;
        assertThat(operation1.compareTo(operation4), is(lessThan(0)));
    }
}