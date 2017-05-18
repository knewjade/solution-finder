package _experimental.allcomb;

import common.datastore.BlockField;
import common.datastore.OperationWithKey;
import core.field.SmallField;
import core.mino.Mino;

import java.util.Comparator;
import java.util.List;

class MinoField implements Comparable<MinoField> {
    private static final Comparator<OperationWithKey> OPERATION_WITH_KEY_COMPARATOR = (o1, o2) -> {
        Mino mino1 = o1.getMino();
        Mino mino2 = o2.getMino();

        int compareBlock = mino1.getBlock().compareTo(mino2.getBlock());
        if (compareBlock != 0)
            return compareBlock;

        int compareRotate = mino1.getRotate().compareTo(mino2.getRotate());
        if (compareRotate != 0)
            return compareRotate;

        int compareX = Integer.compare(o1.getX(), o2.getX());
        if (compareX != 0)
            return compareX;

        int compareY = Integer.compare(o1.getY(), o2.getY());
        if (compareY != 0)
            return compareY;

        return Long.compare(o1.getNeedDeletedKey(), o2.getNeedDeletedKey());
    };

    private final List<OperationWithKey> operations;
    private final ColumnField outerField;
    private final BlockField blockField;

    MinoField(List<OperationWithKey> operations, ColumnField outerField, int height) {
        operations.sort(OPERATION_WITH_KEY_COMPARATOR);
        this.operations = operations;
        this.outerField = outerField;
        this.blockField = parseToBlockField(operations, height);
    }

    private BlockField parseToBlockField(List<OperationWithKey> operations, int height) {
        BlockField blockField = new BlockField(height);
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
        return blockField.equals(minoField.blockField) && operations.equals(minoField.operations);
    }

    @Override
    public int hashCode() {
        int result = blockField.hashCode();
        return result * 31 + operations.hashCode();
    }

    @Override
    public int compareTo(MinoField o) {
        int compareBlockField = blockField.compareTo(o.blockField);
        if (compareBlockField != 0)
            return compareBlockField;

        int compareSize = Integer.compare(operations.size(), o.operations.size());
        if (compareSize != 0)
            return compareSize;

        for (int index = 0; index < operations.size(); index++) {
            int compare = OPERATION_WITH_KEY_COMPARATOR.compare(operations.get(index), o.operations.get(index));
            if (compare != 0)
                return compare;
        }

        return 0;
    }
}
