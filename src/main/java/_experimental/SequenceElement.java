package _experimental;

import common.comparator.FieldComparator;
import core.field.Field;
import core.mino.Block;

public class SequenceElement implements PrioritizedElement, Comparable<SequenceElement> {
    private final Field field;
    private final Block hold;
    private final int maxClearLine;
    private final int depth;
    private final int priority;

    public SequenceElement(Field field, Block hold, int maxClearLine, int depth) {
        this.field = field;
        this.hold = hold;
        this.maxClearLine = maxClearLine;
        this.depth = depth;
        this.priority = Heuristic.c(field, maxClearLine, 4 - maxClearLine, depth);
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public int getDepth() {
        return depth;
    }

    public Field getField() {
        return field;
    }

    public Block getHold() {
        return hold;
    }

    public int getMaxClearLine() {
        return maxClearLine;
    }

    @Override
    public boolean equals(Object o) {
        assert o instanceof SequenceElement;
        SequenceElement that = (SequenceElement) o;
        return hold == that.hold && FieldComparator.compareField(this.field, that.field) == 0;
    }

    @Override
    public int hashCode() {
        int result = hold != null ? hold.getNumber() : 7;
        return result * 31 + field.hashCode();
    }

    @Override
    public int compareTo(SequenceElement o) {
        if (hold == o.hold) {
            return FieldComparator.compareField(field, o.field);
        } else {
            int number1 = hold != null ? hold.getNumber() : 7;
            int number2 = o.hold != null ? o.hold.getNumber() : 7;
            return number1 - number2;
        }
    }
}
