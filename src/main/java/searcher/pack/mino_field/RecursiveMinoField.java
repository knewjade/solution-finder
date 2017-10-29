package searcher.pack.mino_field;

import common.datastore.PieceCounter;
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
    private final PieceCounter pieceCounter;
    private final int maxIndex;

    public RecursiveMinoField(SeparableMino separableMino, ColumnField outerField, SeparableMinos separableMinos) {
        this.separableMino = separableMino;
        this.minoField = null;
        this.outerField = outerField;
        this.pieceCounter = new PieceCounter(Collections.singletonList(separableMino.toMinoOperationWithKey().getPiece()));
        this.maxIndex = separableMinos.toIndex(separableMino);
    }

    public RecursiveMinoField(SeparableMino separableMino, RecursiveMinoField minoField, ColumnField outerField, SeparableMinos separableMinos) {
        this.separableMino = separableMino;
        this.minoField = minoField;
        this.outerField = outerField;
        this.pieceCounter = addToBlockCounter(minoField.getPieceCounter(), separableMino);
        int index = separableMinos.toIndex(separableMino);
        int maxIndex = minoField.getMaxIndex();
        this.maxIndex = maxIndex < index ? index : maxIndex;
    }

    private PieceCounter addToBlockCounter(PieceCounter pieceCounter, SeparableMino separableMino) {
        return pieceCounter.addAndReturnNew(Collections.singletonList(separableMino.toMinoOperationWithKey().getPiece()));
    }

    @Override
    public ColumnField getOuterField() {
        return outerField;
    }

    @Override
    public Stream<OperationWithKey> getOperationsStream() {
        return getSeparableMinoStream().map(SeparableMino::toMinoOperationWithKey);
    }

    @Override
    public PieceCounter getPieceCounter() {
        return pieceCounter;
    }

    @Override
    public int getMaxIndex() {
        return maxIndex;
    }

    @Override
    public Stream<SeparableMino> getSeparableMinoStream() {
        Stream.Builder<SeparableMino> builder = Stream.builder();
        RecursiveMinoField current = this;
        do {
            builder.accept(current.separableMino);
            current = current.minoField;
        } while (current != null);
        return builder.build();
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
