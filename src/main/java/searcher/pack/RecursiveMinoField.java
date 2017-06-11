package searcher.pack;

import common.comparator.OperationWithKeyComparator;
import common.datastore.BlockCounter;
import common.datastore.BlockField;
import common.datastore.OperationWithKey;
import core.column_field.ColumnField;
import core.field.SmallField;
import core.mino.Mino;
import searcher.pack.separable_mino.SeparableMino;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecursiveMinoField implements MinoField {
    private final SeparableMino separableMino;
    private final MinoField minoField;
    private final ColumnField outerField;
    private final BlockField blockField;
    private final BlockCounter blockCounter;
    private final int maxIndex;

    public RecursiveMinoField(SeparableMino separableMino, ColumnField outerField, int height, SeparableMinos separableMinos) {
        this.separableMino = separableMino;
        this.minoField = null;
        this.outerField = outerField;
        this.blockField = parseToBlockField(separableMino, height);
        this.blockCounter = new BlockCounter(Collections.singletonList(separableMino.getMino().getBlock()));
        this.maxIndex = separableMinos.toIndex(separableMino);
    }

    public RecursiveMinoField(SeparableMino separableMino, MinoField minoField, ColumnField outerField, int height, SeparableMinos separableMinos) {
        this.separableMino = separableMino;
        this.minoField = minoField;
        this.outerField = outerField;
        this.blockField = parseToBlockField(separableMino, minoField, height);
        this.blockCounter = addToBlockCounter(minoField.getBlockCounter(), separableMino);
        int index = separableMinos.toIndex(separableMino);
        int maxIndex = minoField.getMaxIndex();
        this.maxIndex = maxIndex < index ? index : maxIndex;
    }

    private BlockField parseToBlockField(SeparableMino separableMino, int height) {
        BlockField blockField = new BlockField(height);
        addToBlockField(blockField, separableMino);
        return blockField;
    }

    private void addToBlockField(BlockField blockField, SeparableMino separableMino) {
        SmallField smallField = new SmallField();
        OperationWithKey operation = separableMino.toOperation();
        Mino mino = operation.getMino();
        smallField.putMino(mino, operation.getX(), operation.getY());
        smallField.insertWhiteLineWithKey(operation.getNeedDeletedKey());
        blockField.merge(smallField, mino.getBlock());
    }

    private BlockField parseToBlockField(SeparableMino separableMino, MinoField minoField, int height) {
        BlockField freeze = minoField.getBlockField().freeze(height);
        addToBlockField(freeze, separableMino);
        return freeze;
    }

    private BlockCounter addToBlockCounter(BlockCounter blockCounter, SeparableMino separableMino) {
        return blockCounter.addAndReturnNew(Collections.singletonList(separableMino.getMino().getBlock()));
    }

    @Override
    public ColumnField getOuterField() {
        return outerField;
    }

    @Override
    public Stream<OperationWithKey> getOperationsStream() {
        if (minoField == null)
            return Stream.of(separableMino.toOperation());
        return Stream.concat(minoField.getOperationsStream(), Stream.of(separableMino.toOperation()));
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
        if (o == null) return false;
        if (getClass() == o.getClass()) {
            RecursiveMinoField minoField = (RecursiveMinoField) o;
            if (!blockField.equals(minoField.blockField))
                return false;

            List<OperationWithKey> stream1 = this.getOperationsStream()
                    .sorted(OperationWithKeyComparator::compareOperationWithKey)
                    .collect(Collectors.toList());
            List<OperationWithKey> stream2 = minoField.getOperationsStream()
                    .sorted(OperationWithKeyComparator::compareOperationWithKey)
                    .collect(Collectors.toList());
            return stream1.equals(stream2);
        } else if (o instanceof MinoField) {
            MinoField minoField = (MinoField) o;
            return MinoFieldComparator.compareMinoField(this, minoField) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getOperationsStream()
                .mapToInt(Object::hashCode)
                .reduce(blockField.hashCode(), (left, right) -> left * 31 + right);
    }
}
