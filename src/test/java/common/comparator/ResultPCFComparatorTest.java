package common.comparator;

import common.OperationHistory;
import common.datastore.Result;
import common.datastore.action.MinimalAction;
import common.datastore.order.Order;
import core.field.Field;
import core.mino.Piece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultPCFComparatorTest {
    private static final Order DUMMY_ORDER = new Order() {
        @Override
        public Piece getHold() {
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
    };

    @Test
    void compare1() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L);
        Result result2 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L);
        ResultPCFComparator comparator = new ResultPCFComparator();
        assertThat(comparator.compare(result1, result2))
                .as(result1.toString())
                .isEqualTo(0);
        assertThat(comparator.compare(result2, result1))
                .as(result1.toString())
                .isEqualTo(0);
    }

    @Test
    void compare2() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Piece.T, MinimalAction.create(8, 2, Rotate.Left), null);
        Result result2 = new Result(DUMMY_ORDER, Piece.T, MinimalAction.create(8, 2, Rotate.Left), null);
        ResultPCFComparator comparator = new ResultPCFComparator();
        assertThat(comparator.compare(result1, result2))
                .as(result1.toString())
                .isEqualTo(0);
        assertThat(comparator.compare(result2, result1))
                .as(result1.toString())
                .isEqualTo(0);
    }

    @Test
    void compareDiffLastBlock() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L);
        Result result2 = new Result(DUMMY_ORDER, Piece.J, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(comparator.compare(result1, result2) * comparator.compare(result2, result1))
                .as(result1.toString())
                .isLessThan(0);
    }

    @Test
    void compareDiffLastX() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L);
        Result result2 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(1, 1, Rotate.Spawn), Piece.L);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(comparator.compare(result1, result2) * comparator.compare(result2, result1))
                .as(result1.toString())
                .isLessThan(0);
    }

    @Test
    void compareDiffLastY() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L);
        Result result2 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 4, Rotate.Spawn), Piece.L);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(comparator.compare(result1, result2) * comparator.compare(result2, result1))
                .as(result1.toString())
                .isLessThan(0);
    }

    @Test
    void compareDiffLastRotate() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L);
        Result result2 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 1, Rotate.Reverse), Piece.L);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(comparator.compare(result1, result2) * comparator.compare(result2, result1))
                .as(result1.toString())
                .isLessThan(0);
    }

    @Test
    void compareDiffLastHold1() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L);
        Result result2 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.I);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(comparator.compare(result1, result2) * comparator.compare(result2, result1))
                .as(result1.toString())
                .isLessThan(0);
    }

    @Test
    void compareDiffLastHold2() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L);
        Result result2 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), null);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(comparator.compare(result1, result2) * comparator.compare(result2, result1))
                .as(result1.toString())
                .isLessThan(0);
    }

    @Test
    void compareDiffLastHold3() throws Exception {
        Result result1 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), null);
        Result result2 = new Result(DUMMY_ORDER, Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.I);
        ResultPCFComparator comparator = new ResultPCFComparator();

        // assert is not 0 & sign reversed
        assertThat(comparator.compare(result1, result2) * comparator.compare(result2, result1))
                .as(result1.toString())
                .isLessThan(0);
    }
}