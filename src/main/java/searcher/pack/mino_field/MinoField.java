package searcher.pack.mino_field;

import common.datastore.PieceCounter;
import common.datastore.OperationWithKey;
import core.column_field.ColumnField;
import searcher.pack.separable_mino.SeparableMino;

import java.util.stream.Stream;

public interface MinoField {
    ColumnField getOuterField();

    Stream<OperationWithKey> getOperationsStream();

    PieceCounter getPieceCounter();

    int getMaxIndex();

    Stream<SeparableMino> getSeparableMinoStream();
}
