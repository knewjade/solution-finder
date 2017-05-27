package common.datastore;

import common.datastore.action.Action;
import common.datastore.order.Order;
import core.mino.Block;

public class Result {
    private final Order order;
    private final Block lastBlock;
    private final Action action;
    private final Block lastHold;

    public Result(Order order, Block lastBlock, Action action, Block lastHold) {
        assert order != null && lastBlock != null && action != null;
        this.order = order;
        this.lastBlock = lastBlock;
        this.action = action;
        this.lastHold = lastHold;
    }

    public Block getLastBlock() {
        return lastBlock;
    }

    public Action getAction() {
        return action;
    }

    public Block getLastHold() {
        return lastHold;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (!order.equals(result.order)) return false;
        if (lastBlock != result.lastBlock) return false;
        if (!action.equals(result.action)) return false;
        return lastHold == result.lastHold;
    }

    @Override
    public int hashCode() {
        int result = order.hashCode();
        result = 31 * result + lastBlock.hashCode();
        result = 31 * result + action.hashCode();
        result = 31 * result + (lastHold != null ? lastHold.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "order=" + order.getHistory() +
                ", lastBlock=" + lastBlock +
                ", candidate.candidate=" + action +
                ", hold=" + lastHold +
                '}';
    }
}
