package searcher.pack.separable_mino;

import common.datastore.MinoOperationWithKey;
import core.column_field.ColumnField;
import core.mino.Mino;

public interface SeparableMino {
    int getLowerY();

    ColumnField getField();

    MinoOperationWithKey toMinoOperationWithKey();
}
