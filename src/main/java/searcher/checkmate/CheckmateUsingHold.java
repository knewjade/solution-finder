package searcher.checkmate;

import action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import searcher.common.Result;
import searcher.common.SimpleSearcherCore;
import searcher.common.action.Action;
import searcher.common.order.NormalOrder;
import searcher.common.order.Order;
import searcher.common.validator.Validator;

import java.util.List;
import java.util.TreeSet;

public class CheckmateUsingHold<T extends Action> implements Checkmate<T> {
    private final CheckmateDataPool dataPool;
    private final SimpleSearcherCore<T> searcherCore;

    public CheckmateUsingHold(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckmateDataPool();
        this.searcherCore = new SimpleSearcherCore<T>(minoFactory, validator, dataPool);
    }

    @Override
    public List<Result> search(Field initField, List<Block> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Block[] blocks = new Block[pieces.size()];
        return search(initField, pieces.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    @Override
    public List<Result> search(Field initField, Block[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        dataPool.initFirst();

        TreeSet<Order> orders = new TreeSet<>();
        orders.add(new NormalOrder(initField, pieces[0], maxClearLine, maxDepth));

        for (int depth = 1; depth <= maxDepth; depth++) {
            dataPool.initEachDepth();

            boolean isLast = depth == maxDepth;

            if (depth < pieces.length) {
                Block drawn = pieces[depth];

                for (int count = 0, size = orders.size(); count < size; count++) {
                    Order order = orders.pollFirst();
                    searcherCore.stepWithNext(candidate, drawn, order, isLast);
                }
            } else {
                for (int count = 0, size = orders.size(); count < size; count++) {
                    Order order = orders.pollFirst();
                    searcherCore.stepWhenNoNext(candidate, order, isLast);
                }
            }

            orders = dataPool.getNexts();
        }

        return dataPool.getResults();
    }
}