package searcher.pack.mino_field;

import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import core.column_field.ColumnField;
import searcher.pack.MinoFieldComparator;
import searcher.pack.SeparableMinos;
import searcher.pack.separable_mino.SeparableMino;

import java.util.Collections;
import java.util.stream.Stream;

public class RecursiveMinoField implements MinoField {
    private final SeparableMino separableMino;
    private final RecursiveMinoField minoField;
    private final ColumnField outerField;
    private final BlockCounter blockCounter;
    private final int maxIndex;

    public RecursiveMinoField(SeparableMino separableMino, ColumnField outerField, SeparableMinos separableMinos) {
        this.separableMino = separableMino;
        this.minoField = null;
        this.outerField = outerField;
        this.blockCounter = new BlockCounter(Collections.singletonList(separableMino.getMino().getBlock()));
        this.maxIndex = separableMinos.toIndex(separableMino);
    }

    public RecursiveMinoField(SeparableMino separableMino, RecursiveMinoField minoField, ColumnField outerField, SeparableMinos separableMinos) {
        this.separableMino = separableMino;
        this.minoField = minoField;
        this.outerField = outerField;
        this.blockCounter = addToBlockCounter(minoField.getBlockCounter(), separableMino);
        int index = separableMinos.toIndex(separableMino);
        int maxIndex = minoField.getMaxIndex();
        this.maxIndex = maxIndex < index ? index : maxIndex;
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
        Stream.Builder<OperationWithKey> builder = Stream.builder();
        RecursiveMinoField current = this;
        do {
            builder.accept(current.separableMino.toOperation());
            current = current.minoField;
        } while (current != null);
        return builder.build();
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
        if (o instanceof MinoField) {
            MinoField minoField = (MinoField) o;
            return MinoFieldComparator.compareMinoField(this, minoField) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getOperationsStream()
                .mapToInt(Object::hashCode)
                .reduce(0, (left, right) -> left * 31 + right);
    }
}
