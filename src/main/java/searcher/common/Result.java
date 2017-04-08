package searcher.common;

import core.mino.Block;
import searcher.common.action.Action;
import searcher.common.order.Order;

public class Result {
    private final Order order;
    private final Block lastBlock;
    private final Action action;
    private final Block lastHold;

    public Result(Order order, Block lastBlock, Action action, Block lastHold) {
        this.order = order;
        this.lastBlock = lastBlock;
        this.action = action;
        this.lastHold = lastHold;
    }

    Block getLastBlock() {
        return lastBlock;
    }

    public Action getAction() {
        return action;
    }

    Block getLastHold() {
        return lastHold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return lastBlock == result.lastBlock && action.equals(result.action) && lastHold == result.lastHold;
    }

    @Override
    public int hashCode() {
        int result = lastBlock.hashCode();
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
