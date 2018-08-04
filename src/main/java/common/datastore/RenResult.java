package common.datastore;

import common.datastore.order.RenOrder;

import java.util.Objects;

public class RenResult {
    private final RenOrder order;
    private final boolean isContinue;

    public RenResult(RenOrder order, boolean isContinue) {
        assert order != null;
        this.order = order;
        this.isContinue = isContinue;
    }

    public boolean isContinue() {
        return isContinue;
    }

    public int getRenCount() {
        return order.getRenCount();
    }

    public RenOrder getRenOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenResult renResult = (RenResult) o;
        return isContinue == renResult.isContinue && Objects.equals(order, renResult.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, isContinue);
    }

    @Override
    public String toString() {
        return "RenResult{" +
                "order=" + order +
                ", isContinue=" + isContinue +
                '}';
    }
}
