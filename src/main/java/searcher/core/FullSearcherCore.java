package searcher.core;

import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import searcher.common.DataPool;
import common.OperationHistory;
import common.datastore.Result;
import common.datastore.action.Action;
import common.datastore.order.NormalOrder;
import common.datastore.order.Order;
import searcher.common.validator.FullValidator;

import java.util.Set;

public class FullSearcherCore<T extends Action> {
    private final MinoFactory minoFactory;
    private final DataPool dataPool;

    public FullSearcherCore(MinoFactory minoFactory, DataPool dataPool) {
        this.minoFactory = minoFactory;
        this.dataPool = dataPool;
    }

    public void stepWithNext(Candidate<T> candidate, FullValidator validator, Block drawn, Order order, boolean isLast, int depth) {
        Block hold = order.getHold();
        step(candidate, validator, drawn, hold, order, isLast, depth);

        if (drawn != hold) {
            // Holdの探索
            step(candidate, validator, hold, drawn, order, isLast, depth);
        }
    }

    public void stepWithNextNoHold(Candidate<T> candidate, FullValidator validator, Block drawn, Order order, boolean isLast, int depth) {
        step(candidate, validator, drawn, order.getHold(), order, isLast, depth);
    }

    public void stepWhenNoNext(Candidate<T> candidate, FullValidator validator, Order order, boolean isLast, int depth) {
        Block hold = order.getHold();
        step(candidate, validator, hold, null, order, isLast, depth);
    }

    private void step(Candidate<T> candidate, FullValidator validator, Block drawn, Block nextHold, Order order, boolean isLast, int depth) {
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

            if (!validator.validate(currentField, field, maxClearLine, drawn, action, depth))
                continue;

            if (validator.satisfies(currentField, field, maxClearLine, drawn, action, depth)) {
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
