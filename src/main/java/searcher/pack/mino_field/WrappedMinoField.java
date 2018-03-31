package searcher.pack.mino_field;

import common.datastore.OperationWithKey;
import common.datastore.PieceCounter;
import core.column_field.ColumnField;
import searcher.pack.SeparableMinos;
import searcher.pack.separable_mino.SeparableMino;

import java.util.stream.Stream;

public class WrappedMinoField implements MinoField {
    private final MinoField minoField;
    private final ColumnField outerField;
    private final SeparableMino separableMino;
    private final int maxIndex;

    public WrappedMinoField(SeparableMino separableMino, MinoField minoField, ColumnField outerField, SeparableMinos separableMinos) {
        this.minoField = minoField;
        this.separableMino = separableMino;
        this.outerField = outerField;

        int index = separableMinos.toIndex(separableMino);
        int maxIndex = minoField.getMaxIndex();
        this.maxIndex = maxIndex < index ? index : maxIndex;
    }

    @Override
    public ColumnField getOuterField() {
        return outerField;
    }

    @Override
    public Stream<OperationWithKey> getOperationsStream() {
        return Stream.concat(minoField.getOperationsStream(), Stream.of(separableMino.toMinoOperationWithKey()));
    }

    @Override
    public PieceCounter getPieceCounter() {
        return minoField.getPieceCounter().addAndReturnNew(Stream.of(separableMino.toMinoOperationWithKey().getPiece()));
    }

    @Override
    public int getMaxIndex() {
        return maxIndex;
    }

    @Override
    public Stream<SeparableMino> getSeparableMinoStream() {
        return Stream.concat(minoField.getSeparableMinoStream(), Stream.of(separableMino));
    }
}
