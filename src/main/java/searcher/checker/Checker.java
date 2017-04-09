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

public class Checker<T extends Action> {
    private final CheckerDataPool dataPool;
    private final SearcherCore<T> searcherCore;

    public Checker(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckerDataPool();
        this.searcherCore = new SearcherCore<>(minoFactory, validator, dataPool);
    }

    // holdあり
    public boolean check(Field initField, List<Block> blockList, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Block[] blocks = new Block[blockList.size()];
        return check(initField, blockList.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    public boolean check(Field initField, Block[] blocks, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        dataPool.initFirst();
        dataPool.addOrder(new DepthOrder(initField, blocks[0], maxClearLine, maxDepth));

        while (!dataPool.getNexts().isEmpty() && dataPool.getResults().isEmpty()) {
            Order order = dataPool.getNexts().pollLast();
            int depth = order.getHistory().getIndex() + 1;
            boolean isLast = depth == maxDepth;

            if (depth < blocks.length) {
                searcherCore.stepNormal(candidate, blocks[depth], order, isLast);
            } else {
                searcherCore.stepLastWhenNoNext(candidate, order, isLast);
            }
        }

//        System.out.println(results);

        return !dataPool.getResults().isEmpty();
    }

    public Result getResult() {
        return dataPool.getResults().get(0);
    }

    private long getMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
