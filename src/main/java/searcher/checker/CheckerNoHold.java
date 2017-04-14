package searcher.checker;

import action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import searcher.common.Result;
import searcher.common.SearcherCore;
import searcher.common.action.Action;
import searcher.common.order.DepthOrder;
import searcher.common.order.Order;
import searcher.common.validator.Validator;

import java.util.List;

public class CheckerNoHold<T extends Action> implements Checker<T> {
    private final CheckerDataPool dataPool;
    private final SearcherCore<T> searcherCore;

    public CheckerNoHold(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckerDataPool();
        this.searcherCore = new SearcherCore<>(minoFactory, validator, dataPool);
    }

    // holdあり
    public boolean check(Field initField, List<Block> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Block[] blocks = new Block[pieces.size()];
        return check(initField, pieces.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    public boolean check(Field initField, Block[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        dataPool.initFirst();
        dataPool.addOrder(new DepthOrder(initField, null, maxClearLine, maxDepth));

        while (!dataPool.getNexts().isEmpty() && dataPool.getResults().isEmpty()) {
            Order order = dataPool.getNexts().pollLast();
            int depth = order.getHistory().getIndex();
            boolean isLast = depth == maxDepth;

            assert depth < pieces.length;
            searcherCore.stepWithNextNoHold(candidate, pieces[depth], order, isLast);
        }

        return !dataPool.getResults().isEmpty();
    }

    public Result getResult() {
        return dataPool.getResults().get(0);
    }
}
