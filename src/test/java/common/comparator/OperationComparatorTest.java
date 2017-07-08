package common.comparator;

import common.datastore.Operation;
import common.datastore.SimpleOperation;
import core.mino.Block;
import core.srs.Rotate;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class OperationComparatorTest {
    @Test
    public void compare() throws Exception {
        SimpleOperation operation1 = new SimpleOperation(Block.I, Rotate.Spawn, 0, 1);
        Operation operation2 = createNewOperation(Block.I, Rotate.Spawn, 0, 1);

        OperationComparator comparator = new OperationComparator();
        assertThat(comparator.compare(operation1, operation2), is(0));
    }

    @Test
    public void compareDiffBlock() throws Exception {
        SimpleOperation operation1 = new SimpleOperation(Block.S, Rotate.Spawn, 0, 1);
        Operation operation2 = createNewOperation(Block.J, Rotate.Spawn, 7, 1);

        // assert is not 0 & sign reversed
        OperationComparator comparator = new OperationComparator();
        assertThat(comparator.compare(operation1, operation2) * comparator.compare(operation2, operation1), is(lessThan(0)));
    }

    @Test
    public void compareDiffRotate() throws Exception {
        SimpleOperation operation1 = new SimpleOperation(Block.S, Rotate.Left, 0, 1);
        Operation operation2 = createNewOperation(Block.J, Rotate.Right, 7, 1);

        // assert is not 0 & sign reversed
        OperationComparator comparator = new OperationComparator();
        assertThat(comparator.compare(operation1, operation2) * comparator.compare(operation2, operation1), is(lessThan(0)));
    }

    @Test
    public void compareDiffX() throws Exception {
        SimpleOperation operation1 = new SimpleOperation(Block.I, Rotate.Spawn, 0, 1);
        Operation operation2 = createNewOperation(Block.I, Rotate.Spawn, 7, 1);

        // assert is not 0 & sign reversed
        OperationComparator comparator = new OperationComparator();
        assertThat(comparator.compare(operation1, operation2) * comparator.compare(operation2, operation1), is(lessThan(0)));
    }

    @Test
    public void compareDiffY() throws Exception {
        SimpleOperation operation1 = new SimpleOperation(Block.I, Rotate.Spawn, 0, 1);
        Operation operation2 = createNewOperation(Block.I, Rotate.Spawn, 0, 4);

        // assert is not 0 & sign reversed
        OperationComparator comparator = new OperationComparator();
        assertThat(comparator.compare(operation1, operation2) * comparator.compare(operation2, operation1), is(lessThan(0)));
    }

    private Operation createNewOperation(Block block, Rotate rotate, int x, int y) {
        return new Operation() {
            @Override
            public Block getBlock() {
                return block;
            }

            @Override
            public Rotate getRotate() {
                return rotate;
            }

            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}