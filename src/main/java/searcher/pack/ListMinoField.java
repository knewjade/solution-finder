package searcher.pack;

import common.comparator.OperationWithKeyComparator;
import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import core.column_field.ColumnField;
import core.mino.Block;
import core.mino.Mino;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListMinoField implements MinoField {
    private final List<OperationWithKey> operations;
    private final ColumnField outerField;
    private final BlockCounter blockCounter;
    private final int maxIndex;

    public ListMinoField(List<OperationWithKey> operations, ColumnField outerField, SeparableMinos separableMinos) {
        operations.sort(OperationWithKeyComparator::compareOperationWithKey);
        this.operations = operations;
        this.outerField = outerField;
        this.blockCounter = parseToBlockCounter(operations);
        this.maxIndex = operations.stream().mapToInt(separableMinos::toIndex).max().orElse(-1);
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
    public Stream<OperationWithKey> getOperationsStream() {
        return operations.stream();
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
        if (o == null) return false;
        if (getClass() == o.getClass()) {
            ListMinoField minoField = (ListMinoField) o;
            return operations.equals(minoField.operations);
        } else if (o instanceof MinoField) {
            MinoField minoField = (MinoField) o;
            return MinoFieldComparator.compareMinoField(this, minoField) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return operations.hashCode();
    }
}
