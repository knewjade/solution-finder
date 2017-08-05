package common.datastore.order;

import common.OperationHistory;
import common.comparator.DepthOrderComparator;
import common.comparator.OrderComparator;
import core.field.Field;
import core.mino.Block;

public class DepthOrder implements Order {
    private final Block hold;
    private final Field field;
    private final int maxClearLine;
    private final OperationHistory history;

    public DepthOrder(Field field, Block hold, int maxClearLine, int maxDepth) {
        this(field, hold, maxClearLine, new OperationHistory(maxDepth - 1));
    }

    private DepthOrder(Field field, Block hold, int maxClearLine, OperationHistory history) {
        this.field = field;
        this.hold = hold;
        this.maxClearLine = maxClearLine;
        this.history = history;
    }

    public OperationHistory getHistory() {
        return history;
    }

    public Block getHold() {
        return hold;
    }

    public Field getField() {
        return field;
    }

    public int getMaxClearLine() {
        return maxClearLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Order)) return false;
        Order order = (Order) o;
        return OrderComparator.compareOrder(this, order) == 0;
    }

    @Override
    public int hashCode() {
        int result = hold != null ? hold.hashCode() : 0;
        result = 31 * result + field.hashCode();
        result = 31 * result + history.hashCode();
        return result;
    }

    @Override
    public int compareTo(Order o) {
        return OrderComparator.compareOrder(this, o);
    }
}

