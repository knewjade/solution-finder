package searcher.checker;

import action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import searcher.common.Result;
import searcher.common.SimpleSearcherCore;
import searcher.common.action.Action;
import searcher.common.order.DepthOrder;
import searcher.common.order.Order;
import searcher.common.validator.Validator;

import java.util.List;

public class CheckerUsingHold<T extends Action> implements Checker<T> {
    private final CheckerDataPool dataPool;
    private final SimpleSearcherCore<T> searcherCore;

    public CheckerUsingHold(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckerDataPool();
        this.searcherCore = new SimpleSearcherCore<>(minoFactory, validator, dataPool);
    }

    @Override
    public boolean check(Field initField, List<Block> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Block[] blocks = new Block[pieces.size()];
        return check(initField, pieces.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    @Override
    public boolean check(Field initField, Block[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        dataPool.initFirst();
        dataPool.addOrder(new DepthOrder(initField, pieces[0], maxClearLine, maxDepth));

        while (!dataPool.getNexts().isEmpty() && dataPool.getResults().isEmpty()) {
            Order order = dataPool.getNexts().pollLast();
            int depth = order.getHistory().getNextIndex() + 1;
            boolean isLast = depth == maxDepth;

            if (depth < pieces.length) {
                searcherCore.stepWithNext(candidate, pieces[depth], order, isLast);
            } else {
                searcherCore.stepWhenNoNext(candidate, order, isLast);
            }
        }

        return !dataPool.getResults().isEmpty();
    }

    @Override
    public Result getResult() {
        return dataPool.getResults().get(0);
    }
}
