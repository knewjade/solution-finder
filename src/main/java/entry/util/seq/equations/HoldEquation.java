package entry.util.seq.equations;

import common.order.WithHoldCount;
import core.mino.Piece;

import java.util.Objects;
import java.util.function.Predicate;

public class HoldEquation {
    private final int value;
    private final Operators operator;

    public HoldEquation(int value, Operators operator) {
        this.value = value;
        this.operator = operator;
    }

    public Predicate<WithHoldCount<Piece>> toPredict() {
        switch (operator) {
            case EqualTo:
                return (count) -> count.getHoldCount() == value;
            case NotEqualTo:
                return (count) -> count.getHoldCount() != value;
            case GreaterThan:
                return (count) -> value < count.getHoldCount();
            case GreaterThanOrEqualTo:
                return (count) -> value <= count.getHoldCount();
            case LessThan:
                return (count) -> count.getHoldCount() < value;
            case LessThanOrEqualTo:
                return (count) -> count.getHoldCount() <= value;
        }
        throw new IllegalStateException("Unsupported operator: operator=" + operator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoldEquation that = (HoldEquation) o;
        return value == that.value && operator == that.operator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, operator);
    }
}
