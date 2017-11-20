package common.comparator;

import common.datastore.Operation;
import common.datastore.SimpleOperation;
import core.mino.Piece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OperationComparatorTest {
    @Test
    void compare() throws Exception {
        SimpleOperation operation1 = new SimpleOperation(Piece.I, Rotate.Spawn, 0, 1);
        Operation operation2 = createNewOperation(Piece.I, Rotate.Spawn, 0, 1);

        OperationComparator comparator = new OperationComparator();
        assertThat(comparator.compare(operation1, operation2)).isEqualTo(0);
    }

    @Test
    void compareDiffBlock() throws Exception {
        SimpleOperation operation1 = new SimpleOperation(Piece.S, Rotate.Spawn, 0, 1);
        Operation operation2 = createNewOperation(Piece.J, Rotate.Spawn, 7, 1);

        // assert is not 0 & sign reversed
        OperationComparator comparator = new OperationComparator();
        assertThat(comparator.compare(operation1, operation2) * comparator.compare(operation2, operation1)).isLessThan(0);
    }

    @Test
    void compareDiffRotate() throws Exception {
        SimpleOperation operation1 = new SimpleOperation(Piece.S, Rotate.Left, 0, 1);
        Operation operation2 = createNewOperation(Piece.J, Rotate.Right, 7, 1);

        // assert is not 0 & sign reversed
        OperationComparator comparator = new OperationComparator();
        assertThat(comparator.compare(operation1, operation2) * comparator.compare(operation2, operation1)).isLessThan(0);
    }

    @Test
    void compareDiffX() throws Exception {
        SimpleOperation operation1 = new SimpleOperation(Piece.I, Rotate.Spawn, 0, 1);
        Operation operation2 = createNewOperation(Piece.I, Rotate.Spawn, 7, 1);

        // assert is not 0 & sign reversed
        OperationComparator comparator = new OperationComparator();
        assertThat(comparator.compare(operation1, operation2) * comparator.compare(operation2, operation1)).isLessThan(0);
    }

    @Test
    void compareDiffY() throws Exception {
        SimpleOperation operation1 = new SimpleOperation(Piece.I, Rotate.Spawn, 0, 1);
        Operation operation2 = createNewOperation(Piece.I, Rotate.Spawn, 0, 4);

        // assert is not 0 & sign reversed
        OperationComparator comparator = new OperationComparator();
        assertThat(comparator.compare(operation1, operation2) * comparator.compare(operation2, operation1)).isLessThan(0);
    }

    private Operation createNewOperation(Piece piece, Rotate rotate, int x, int y) {
        return new Operation() {
            @Override
            public Piece getPiece() {
                return piece;
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