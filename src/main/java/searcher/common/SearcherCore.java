package searcher.common;

import action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import searcher.common.action.Action;
import searcher.common.order.Order;
import searcher.common.order.NormalOrder;
import searcher.common.validator.Validator;

import java.util.HashSet;
import java.util.Set;

public class SearcherCore<T extends Action> {
    private final MinoFactory minoFactory;
    private final Validator validator;
    private final MinoShifter shifter;
    private final DataPool dataPool;

    public SearcherCore(MinoFactory minoFactory, Validator validator, DataPool dataPool) {
        this.minoFactory = minoFactory;
        this.validator = validator;
        this.dataPool = dataPool;
        this.shifter = new MinoShifter();
    }

    public void stepWithNext(Candidate<T> candidate, Block drawn, Order order, boolean isLast) {
        Block hold = order.getHold();
        step(candidate, drawn, hold, order, isLast);

        if (drawn != hold) {
            // Holdの探索
            step(candidate, hold, drawn, order, isLast);
        }
    }

    public void stepWhenNoNext(Candidate<T> candidate, Order order, boolean isLast) {
        Block hold = order.getHold();
        step(candidate, hold, null, order, isLast);
    }

    private void step(Candidate<T> candidate, Block drawn, Block nextHold, Order order, boolean isLast) {
        Field currentField = order.getField();
        int max = order.getMaxClearLine();
        Set<T> candidateList = candidate.search(currentField, drawn, max);

        HashSet<Action> actions = new HashSet<>();
        for (T action : candidateList)
            actions.add(shifter.createTransformedAction(drawn, action));

        OperationHistory history = order.getHistory();
        for (Action action : actions) {
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

            OperationHistory nextHistory = history.record(drawn, action);
            Order nextOrder = new NormalOrder(field, nextHold, maxClearLine, nextHistory);
            dataPool.addOrder(nextOrder);
        }
    }
}
