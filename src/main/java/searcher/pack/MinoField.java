package searcher.pack;

import common.comparator.OperationWithKeyComparator;
import common.datastore.BlockCounter;
import common.datastore.BlockField;
import common.datastore.OperationWithKey;
import core.column_field.ColumnField;
import core.field.SmallField;
import core.mino.Block;
import core.mino.Mino;
import searcher.pack.separable_mino.SeparableMino;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class MinoField implements IMinoField {
    private final List<OperationWithKey> operations;
    private final ColumnField outerField;
    private final BlockField blockField;
    private final BlockCounter blockCounter;
    private final int maxIndex;

    public MinoField(List<OperationWithKey> operations, ColumnField outerField, int height, SeparableMinos separableMinos) {
        operations.sort(OperationWithKeyComparator::compareOperationWithKey);
        this.operations = operations;
        this.outerField = outerField;
        this.blockField = parseToBlockField(operations, height);
        this.blockCounter = parseToBlockCounter(operations);
        this.maxIndex = operations.stream().mapToInt(separableMinos::toIndex).max().orElse(-1);
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

    private BlockCounter parseToBlockCounter(List<OperationWithKey> operations) {
        List<Block> blocks = operations.stream()
                .map(OperationWithKey::getMino)
                .map(Mino::getBlock)
                .collect(Collectors.toList());
        return new BlockCounter(blocks);
    }

    @Override
    public ColumnField getOuterField() {
        return outerField;
    }

    @Override
    public List<OperationWithKey> getOperations() {
        return operations;
    }

    @Override
    public BlockField getBlockField() {
        return blockField;
    }

    @Override
    public BlockCounter getBlockCounter() {
        return blockCounter;
    }

    @Override
    public int getMaxIndex() {
        return maxIndex;
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
    public int compareTo(IMinoField o) {
        return MinoFieldComparator.compareMinoField(this, o);
    }
}
