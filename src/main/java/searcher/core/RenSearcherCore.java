package searcher.core;

import common.OperationHistory;
import common.datastore.RenResult;
import common.datastore.action.Action;
import common.datastore.order.RenNormalOrder;
import common.datastore.order.RenOrder;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import searcher.common.DataPool;

import java.util.Set;

public class RenSearcherCore<T extends Action, O extends RenOrder> implements SearcherCore<T, O> {
    private final MinoFactory minoFactory;
    private final DataPool<RenOrder, RenResult> dataPool;
    private final int max;

    public RenSearcherCore(MinoFactory minoFactory, DataPool<RenOrder, RenResult> dataPool, int max) {
        this.minoFactory = minoFactory;
        this.dataPool = dataPool;
        this.max = max;
    }

    public void stepWithNext(Candidate<T> candidate, Piece drawn, O order, boolean isLast) {
        Piece hold = order.getHold();
        boolean isTerminated = step(candidate, drawn, hold, order, isLast);

        if (drawn != hold) {
            // Holdの探索
            isTerminated &= step(candidate, hold, drawn, order, isLast);
        }

        if (isTerminated) {
            dataPool.addResult(new RenResult(order, false));
        }
    }

    public void stepWithNextNoHold(Candidate<T> candidate, Piece drawn, O order, boolean isLast) {
        boolean isTerminated = step(candidate, drawn, order.getHold(), order, isLast);
        if (isTerminated) {
            dataPool.addResult(new RenResult(order, false));
        }
    }

    public void stepWhenNoNext(Candidate<T> candidate, O order, boolean isLast) {
        Piece hold = order.getHold();
        boolean isTerminated = step(candidate, hold, null, order, isLast);
        if (isTerminated) {
            dataPool.addResult(new RenResult(order, false));
        }
    }

    private boolean step(Candidate<T> candidate, Piece drawn, Piece nextHold, O order, boolean isLast) {
        Field currentField = order.getField();
        int renCount = order.getRenCount();
        Set<T> candidateList = candidate.search(currentField, drawn, max);

        boolean isTerminated = true;
        OperationHistory history = order.getHistory();
        for (T action : candidateList) {
            Field field = currentField.freeze(max);
            Mino mino = minoFactory.create(drawn, action.getRotate());
            field.put(mino, action.getX(), action.getY());
            int deletedLine = field.clearLine();

            if (0 < deletedLine) {
                // RENが続くとき
                isTerminated = false;

                OperationHistory nextHistory = history.recordAndReturnNew(drawn, action);
                RenOrder nextOrder = new RenNormalOrder(field, nextHold, renCount + 1, nextHistory);

                if (isLast) {
                    dataPool.addResult(new RenResult(nextOrder, true));
                } else {
                    dataPool.addOrder(nextOrder);
                }
            }
        }

        return isTerminated;
    }
}
