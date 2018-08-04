package common.datastore.order;

import common.OperationHistory;
import common.comparator.RenOrderComparator;
import core.field.Field;
import core.mino.Piece;

public class RenNormalOrder implements RenOrder, Comparable<RenOrder> {
    private final Piece hold;
    private final Field field;
    private final OperationHistory history;
    private final int renCount;

    public RenNormalOrder(Field field, Piece hold, int renCount, int maxDepth) {
        this(field, hold, renCount, new OperationHistory(maxDepth));
    }

    public RenNormalOrder(Field field, Piece hold, int renCount, OperationHistory history) {
        this.field = field;
        this.hold = hold;
        this.history = history;
        this.renCount = renCount;
    }

    public OperationHistory getHistory() {
        return history;
    }

    public Piece getHold() {
        return hold;
    }

    public Field getField() {
        return field;
    }

    public int getRenCount() {
        return renCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof RenOrder)) return false;
        RenOrder order = (RenOrder) o;
        return RenOrderComparator.compareOrder(this, order) == 0;
    }

    @Override
    public int hashCode() {
        int result = hold != null ? hold.hashCode() : 0;
        result = 31 * result + field.hashCode();
        result = 31 * result + history.hashCode();
        return result;
    }

    @Override
    public int compareTo(RenOrder o) {
        return RenOrderComparator.compareOrder(this, o);
    }
}
