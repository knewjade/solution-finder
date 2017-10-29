package searcher.pack.mino_field;

import common.datastore.BlockCounter;
import common.datastore.BlockField;
import common.datastore.OperationWithKey;
import core.column_field.ColumnField;
import searcher.pack.separable_mino.SeparableMino;

import java.util.List;
import java.util.stream.Stream;

public interface MinoField {
    ColumnField getOuterField();

    Stream<OperationWithKey> getOperationsStream();

    BlockCounter getBlockCounter();

    int getMaxIndex();

    Stream<SeparableMino> getSeparableMinoStream();

}
