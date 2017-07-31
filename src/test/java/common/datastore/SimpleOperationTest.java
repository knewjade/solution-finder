package common.datastore;

import core.mino.Block;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleOperationTest {
    @Test
    void testGetter() throws Exception {
        Operation operation = new SimpleOperation(Block.T, Rotate.Spawn, 4, 5);
        assertThat(operation)
                .returns(Block.T, Operation::getBlock)
                .returns(Rotate.Spawn, Operation::getRotate)
                .returns(4, Operation::getX)
                .returns(5, Operation::getY);
    }

    @Test
    void testEqual() throws Exception {
        Operation operation = new SimpleOperation(Block.T, Rotate.Spawn, 4, 5);
        assertThat(operation.equals(new SimpleOperation(Block.T, Rotate.Spawn, 4, 5))).isTrue();
        assertThat(operation.equals(new SimpleOperation(Block.L, Rotate.Spawn, 4, 5))).isFalse();
        assertThat(operation.equals(new SimpleOperation(Block.T, Rotate.Left, 4, 5))).isFalse();
        assertThat(operation.equals(new SimpleOperation(Block.T, Rotate.Spawn, 3, 5))).isFalse();
        assertThat(operation.equals(new SimpleOperation(Block.T, Rotate.Spawn, 4, 6))).isFalse();
    }

    @Test
    void testHashCode() throws Exception {
        Operation operation = new SimpleOperation(Block.T, Rotate.Spawn, 4, 5);
        assertThat(new SimpleOperation(Block.T, Rotate.Spawn, 4, 5).hashCode()).isEqualTo(operation.hashCode());
        assertThat(new SimpleOperation(Block.L, Rotate.Spawn, 4, 5).hashCode()).isNotEqualTo(operation.hashCode());
        assertThat(new SimpleOperation(Block.T, Rotate.Left, 4, 5).hashCode()).isNotEqualTo(operation.hashCode());
        assertThat(new SimpleOperation(Block.T, Rotate.Spawn, 3, 5).hashCode()).isNotEqualTo(operation.hashCode());
        assertThat(new SimpleOperation(Block.T, Rotate.Spawn, 4, 6).hashCode()).isNotEqualTo(operation.hashCode());
    }

    @Test
    void testCompareTo() throws Exception {
        Operation operation1 = new SimpleOperation(Block.T, Rotate.Spawn, 4, 5);
        SimpleOperation operation2 = new SimpleOperation(Block.T, Rotate.Spawn, 4, 5);
        SimpleOperation operation3 = new SimpleOperation(Block.T, Rotate.Spawn, 4, 13);
        SimpleOperation operation4 = new SimpleOperation(Block.T, Rotate.Spawn, 5, 13);

        assertThat(Operation.compareTo(operation1, operation2)).isEqualTo(0);

        assertThat(Operation.compareTo(operation1, operation3)).isNotEqualTo(0);
        assertThat(Operation.compareTo(operation1, operation4)).isNotEqualTo(0);
        assertThat(Operation.compareTo(operation3, operation4)).isNotEqualTo(0);

        assert Operation.compareTo(operation1, operation3) < 0 && Operation.compareTo(operation3, operation4) < 0;
        assertThat(Operation.compareTo(operation1, operation4)).isLessThan(0);
    }
}