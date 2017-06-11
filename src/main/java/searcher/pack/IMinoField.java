package searcher.pack;

import common.datastore.BlockCounter;
import common.datastore.BlockField;
import common.datastore.OperationWithKey;
import core.column_field.ColumnField;

import java.util.List;

public interface IMinoField extends Comparable<IMinoField> {
    ColumnField getOuterField();

    List<OperationWithKey> getOperations();

    BlockField getBlockField();

    BlockCounter getBlockCounter();

    int getMaxIndex();
}
