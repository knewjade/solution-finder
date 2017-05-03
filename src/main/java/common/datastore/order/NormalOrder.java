package common.datastore.order;

import core.field.Field;
import core.mino.Block;
import common.FieldComparator;
import searcher.common.OperationHistory;

import java.util.Comparator;

public class NormalOrder implements Order {
    private static final Comparator<Field> FIELD_COMPARATOR = new FieldComparator();

    private final Block hold;
    private final Field field;
    private final int maxClearLine;
    private final OperationHistory history;

    public NormalOrder(Field field, Block hold, int maxClearLine, int maxDepth) {
        this(field, hold, maxClearLine, new OperationHistory(maxDepth - 1));
    }

    public NormalOrder(Field field, Block hold, int maxClearLine, OperationHistory history) {
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
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return this.compareTo(order) == 0;
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(Order o) {
        if (hold == o.getHold()) {
            return FIELD_COMPARATOR.compare(field, o.getField());
        } else {
            int number = hold != null ? hold.getNumber() : 7;
            int number1 = o.getHold() != null ? o.getHold().getNumber() : 7;
            return number - number1;
        }
    }
}
