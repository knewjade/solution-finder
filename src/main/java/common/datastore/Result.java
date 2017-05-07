package common.datastore;

import core.mino.Block;
import common.datastore.action.Action;
import common.datastore.order.Order;

// TODO: createOperationsを外に出してdatastoreへ移動
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
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
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
