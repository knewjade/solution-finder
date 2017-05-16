package _experimental.allcomb;

import common.datastore.BlockField;
import common.datastore.OperationWithKey;
import core.field.SmallField;
import core.mino.Mino;

import java.util.List;

class MinoField implements Comparable<MinoField> {
    private final List<OperationWithKey> operations;
    private final ColumnField outerField;
    private final BlockField blockField;

    MinoField(List<OperationWithKey> operations, ColumnField outerField, int height) {
        this.operations = operations;
        this.outerField = outerField;
        this.blockField = parseToBlockField(operations);
    }

    private BlockField parseToBlockField(List<OperationWithKey> operations) {
        BlockField blockField = new BlockField(4);
        for (OperationWithKey operation : operations) {
            SmallField smallField = new SmallField();
            Mino mino = operation.getMino();
            smallField.putMino(mino, operation.getX(), operation.getY());
            smallField.insertWhiteLineWithKey(operation.getNeedDeletedKey());
            blockField.merge(smallField, mino.getBlock());
        }
        return blockField;
    }

    ColumnField getOuterField() {
        return outerField;
    }

    List<OperationWithKey> getOperations() {
        return operations;
    }

    BlockField getBlockField() {
        return blockField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinoField minoField = (MinoField) o;
        return blockField.equals(minoField.blockField);
    }

    @Override
    public int hashCode() {
        return blockField.hashCode();
    }

    @Override
    public int compareTo(MinoField o) {
        return blockField.compareTo(o.blockField);
    }
}
