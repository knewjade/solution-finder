package common.datastore.order;

import common.OperationHistory;
import core.field.Field;
import core.mino.Piece;

public interface RenOrder {
    Piece getHold();

    Field getField();

    OperationHistory getHistory();

    int getRenCount();
}
