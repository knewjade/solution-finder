package common.datastore.order;

import core.field.Field;
import core.mino.Piece;
import common.OperationHistory;

public interface Order extends Comparable<Order> {
    Piece getHold();

    Field getField();

    OperationHistory getHistory();

    int getMaxClearLine();
}
