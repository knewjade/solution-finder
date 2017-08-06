package searcher.core;

import common.OperationHistory;
import common.datastore.Result;
import common.datastore.action.Action;
import common.datastore.order.NormalOrder;
import common.datastore.order.Order;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import searcher.common.DataPool;
import searcher.common.validator.Validator;

import java.util.Set;

public class SimpleSearcherCore<T extends Action> {
    private final MinoFactory minoFactory;
    private final Validator validator;
    private final DataPool dataPool;

    public SimpleSearcherCore(MinoFactory minoFactory, Validator validator, DataPool dataPool) {
        this.minoFactory = minoFactory;
        this.validator = validator;
        this.dataPool = dataPool;
    }

    public void stepWithNext(Candidate<T> candidate, Block drawn, Order order, boolean isLast) {
        Block hold = order.getHold();
        step(candidate, drawn, hold, order, isLast);

        if (drawn != hold) {
            // Holdの探索
            step(candidate, hold, drawn, order, isLast);
        }
    }

    public void stepWithNextNoHold(Candidate<T> candidate, Block drawn, Order order, boolean isLast) {
        step(candidate, drawn, order.getHold(), order, isLast);
    }

    public void stepWhenNoNext(Candidate<T> candidate, Order order, boolean isLast) {
        Block hold = order.getHold();
        step(candidate, hold, null, order, isLast);
    }

    private void step(Candidate<T> candidate, Block drawn, Block nextHold, Order order, boolean isLast) {
        Field currentField = order.getField();
        int max = order.getMaxClearLine();
        Set<T> candidateList = candidate.search(currentField, drawn, max);

        OperationHistory history = order.getHistory();
        for (T action : candidateList) {
            Field field = currentField.freeze(max);
            Mino mino = minoFactory.create(drawn, action.getRotate());
            field.putMino(mino, action.getX(), action.getY());
            int clearLine = field.clearLine();
            int maxClearLine = max - clearLine;

            if (!validator.validate(field, maxClearLine))
                continue;

            if (validator.satisfies(field, maxClearLine)) {
                Result result = new Result(order, drawn, action, nextHold);
                dataPool.addResult(result);
                continue;
            }

            if (isLast)
                continue;

            OperationHistory nextHistory = history.recordAndReturnNew(drawn, action);
            Order nextOrder = new NormalOrder(field, nextHold, maxClearLine, nextHistory);
            dataPool.addOrder(nextOrder);
        }
    }
}
