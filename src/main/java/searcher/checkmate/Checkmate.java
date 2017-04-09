package searcher.checkmate;

import action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import searcher.common.Result;
import searcher.common.SearcherCore;
import searcher.common.action.Action;
import searcher.common.order.Order;
import searcher.common.order.NormalOrder;
import searcher.common.validator.Validator;

import java.util.List;
import java.util.TreeSet;

public class Checkmate<T extends Action> {
    private final CheckmateDataPool dataPool;
    private final SearcherCore<T> searcherCore;

    public Checkmate(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckmateDataPool();
        this.searcherCore = new SearcherCore<T>(minoFactory, validator, dataPool);
    }

    // holdあり
    public List<Result> search(Field initField, List<Block> blockList, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Block[] blocks = new Block[blockList.size()];
        return search(initField, blockList.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    public List<Result> search(Field initField, Block[] blocks, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        dataPool.initFirst();

        TreeSet<Order> orders = new TreeSet<>();
        orders.add(new NormalOrder(initField, blocks[0], maxClearLine, maxDepth));

        for (int depth = 1; depth <= maxDepth; depth++) {
            dataPool.initEachDepth();

            boolean isLast = depth == maxDepth;

            if (depth < blocks.length) {
                Block drawn = blocks[depth];

                for (int count = 0, size = orders.size(); count < size; count++) {
                    Order order = orders.pollFirst();
                    searcherCore.stepNormal(candidate, drawn, order, isLast);
                }
            } else {
                for (int count = 0, size = orders.size(); count < size; count++) {
                    Order order = orders.pollFirst();
                    searcherCore.stepLastWhenNoNext(candidate, order, isLast);
                }
            }

            orders = dataPool.getNexts();
        }

        return dataPool.getResults();
    }
}