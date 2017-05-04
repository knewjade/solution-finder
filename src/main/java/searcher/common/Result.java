package searcher.common;

import common.datastore.Operation;
import common.datastore.SimpleOperation;
import core.mino.Block;
import common.datastore.action.Action;
import common.datastore.order.Order;

import java.util.ArrayList;
import java.util.List;

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

    public List<Operation> createOperations() {
        OperationHistory history = order.getHistory();
        int[] operationNumbers = history.getOperationNumbers();
        int max = history.getNextIndex();
        ArrayList<Operation> operations = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            int value = operationNumbers[i];
            Operation operation = ActionParser.parseToOperation(value);
            operations.add(operation);
        }
        operations.add(new SimpleOperation(lastBlock, action.getRotate(), action.getX(), action.getY()));
        return operations;
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
