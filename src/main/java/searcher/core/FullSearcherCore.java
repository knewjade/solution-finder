package searcher.core;

import common.OperationHistory;
import common.datastore.Result;
import common.datastore.action.Action;
import common.datastore.order.NormalOrder;
import common.datastore.order.Order;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import searcher.SearchValidator;
import searcher.common.DataPool;

import java.util.Set;

public class FullSearcherCore<T extends Action, O extends Order> implements SearcherCore<T, O> {
    private final MinoFactory minoFactory;
    private final SearchValidator validator;
    private final DataPool<Order, Result> dataPool;

    public FullSearcherCore(MinoFactory minoFactory, SearchValidator validator, DataPool<Order, Result> dataPool) {
        this.minoFactory = minoFactory;
        this.validator = validator;
        this.dataPool = dataPool;
    }

    public void stepWithNext(Candidate<T> candidate, Piece drawn, O order, boolean isLast) {
        Piece hold = order.getHold();
        step(candidate, drawn, hold, order, isLast);

        if (drawn != hold) {
            // Holdの探索
            step(candidate, hold, drawn, order, isLast);
        }
    }

    public void stepWithNextNoHold(Candidate<T> candidate, Piece drawn, O order, boolean isLast) {
        step(candidate, drawn, order.getHold(), order, isLast);
    }

    public void stepWhenNoNext(Candidate<T> candidate, O order, boolean isLast) {
        Piece hold = order.getHold();
        step(candidate, hold, null, order, isLast);
    }

    private void step(Candidate<T> candidate, Piece drawn, Piece nextHold, Order order, boolean isLast) {
        Field currentField = order.getField();
        int max = order.getMaxClearLine();
        Set<T> candidateList = candidate.search(currentField, drawn, max);

        OperationHistory history = order.getHistory();
        for (T action : candidateList) {
            Field field = currentField.freeze(max);
            Mino mino = minoFactory.create(drawn, action.getRotate());
            int x = action.getX();
            int y = action.getY();
            field.put(mino, x, y);
            long deletedKey = field.clearLineReturnKey();
            int maxClearLine = max - Long.bitCount(deletedKey);

            ValidationParameter parameter = new ValidationParameter(order, field, mino, x, y, deletedKey, maxClearLine, isLast);
            switch (validator.check(parameter)) {
                case Result: {
                    Result result = new Result(order, drawn, action, nextHold);
                    dataPool.addResult(result);
                    continue;
                }
                case Prune: {
                    continue;
                }
            }

            OperationHistory nextHistory = history.recordAndReturnNew(drawn, action);
            Order nextOrder = new NormalOrder(field, nextHold, maxClearLine, nextHistory);
            dataPool.addOrder(nextOrder);
        }
    }
}
