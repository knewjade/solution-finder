package common.datastore.order;

import core.field.Field;
import core.mino.Block;
import searcher.common.OperationHistory;

// TODO: Comparableをはずして、なるべくComparatorをつかう
public interface Order extends Comparable<Order> {
    Block getHold();

    Field getField();

    OperationHistory getHistory();

    int getMaxClearLine();
}
