package common.comparator;

import common.OperationHistory;
import common.datastore.Result;
import common.datastore.action.MinimalAction;
import common.datastore.order.Order;
import core.field.Field;
import core.mino.Block;
import core.srs.Rotate;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class ResultPCFComparatorTest {
    private static final Order DUMMY_ORDER = new Order() {
        @Override
        public Block getHold() {
            return null;
        }

        @Override
        public Field getField() {
            return null;
        }

        @Override
        public OperationHistory getHistory() {
            return null;
        }

        @Override
        public int getMaxClearLine() {
            return 0;
        }

        @Override
        public int compareTo(Order o) {
            return 0;
        }
    };

    @Test
    public void compare1() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 1, Rotate.Spawn), Block.L);
        Result result2 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 1, Rotate.Spawn), Block.L);
        ResultPCFComparator comparator = new ResultPCFComparator();
        assertThat(result1.toString(), comparator.compare(result1, result2), is(0));
        assertThat(result1.toString(), comparator.compare(result2, result1), is(0));
    }

    @Test
    public void compare2() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Block.T, MinimalAction.create(8, 2, Rotate.Left), null);
        Result result2 = new Result(DUMMY_ORDER, Block.T, MinimalAction.create(8, 2, Rotate.Left), null);
        ResultPCFComparator comparator = new ResultPCFComparator();
        assertThat(result1.toString(), comparator.compare(result1, result2), is(0));
        assertThat(result1.toString(), comparator.compare(result2, result1), is(0));
    }

    @Test
    public void compareDiffLastBlock() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 1, Rotate.Spawn), Block.L);
        Result result2 = new Result(DUMMY_ORDER, Block.J, MinimalAction.create(0, 1, Rotate.Spawn), Block.L);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(result1.toString(), comparator.compare(result1, result2) * comparator.compare(result2, result1), is(lessThan(0)));
    }

    @Test
    public void compareDiffLastX() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 1, Rotate.Spawn), Block.L);
        Result result2 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(1, 1, Rotate.Spawn), Block.L);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(result1.toString(), comparator.compare(result1, result2) * comparator.compare(result2, result1), is(lessThan(0)));
    }

    @Test
    public void compareDiffLastY() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 1, Rotate.Spawn), Block.L);
        Result result2 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 4, Rotate.Spawn), Block.L);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(result1.toString(), comparator.compare(result1, result2) * comparator.compare(result2, result1), is(lessThan(0)));
    }

    @Test
    public void compareDiffLastRotate() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 1, Rotate.Spawn), Block.L);
        Result result2 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 1, Rotate.Reverse), Block.L);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(result1.toString(), comparator.compare(result1, result2) * comparator.compare(result2, result1), is(lessThan(0)));
    }

    @Test
    public void compareDiffLastHold1() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 1, Rotate.Spawn), Block.L);
        Result result2 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 1, Rotate.Spawn), Block.I);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(result1.toString(), comparator.compare(result1, result2) * comparator.compare(result2, result1), is(lessThan(0)));
    }

    @Test
    public void compareDiffLastHold2() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 1, Rotate.Spawn), Block.L);
        Result result2 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 1, Rotate.Spawn), null);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(result1.toString(), comparator.compare(result1, result2) * comparator.compare(result2, result1), is(lessThan(0)));
    }

    @Test
    public void compareDiffLastHold3() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 1, Rotate.Spawn), null);
        Result result2 = new Result(DUMMY_ORDER, Block.I, MinimalAction.create(0, 1, Rotate.Spawn), Block.I);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(result1.toString(), comparator.compare(result1, result2) * comparator.compare(result2, result1), is(lessThan(0)));
    }
}