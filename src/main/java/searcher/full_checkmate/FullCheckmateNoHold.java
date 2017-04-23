package searcher.full_checkmate;

import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import searcher.checkmate.CheckmateDataPool;
import searcher.core.FullSearcherCore;
import searcher.common.Result;
import searcher.common.action.Action;
import searcher.common.order.NormalOrder;
import searcher.common.order.Order;
import searcher.common.validator.FullValidator;

import java.util.List;
import java.util.TreeSet;

/**
 * マルチスレッド非対応
 */
public class FullCheckmateNoHold<T extends Action> implements FullCheckmate<T> {
    private final CheckmateDataPool dataPool;
    private final FullSearcherCore<T> searcherCore;

    public FullCheckmateNoHold(MinoFactory minoFactory) {
        this.dataPool = new CheckmateDataPool();
        this.searcherCore = new FullSearcherCore<>(minoFactory, dataPool);
    }

    @Override
    public List<Result> search(Field initField, List<Block> pieces, Candidate<T> candidate, FullValidator validator, int maxClearLine, int maxDepth) {
        Block[] blocks = new Block[pieces.size()];
        return search(initField, pieces.toArray(blocks), candidate, validator, maxClearLine, maxDepth);
    }

    @Override
    public List<Result> search(Field initField, Block[] pieces, Candidate<T> candidate, FullValidator validator, int maxClearLine, int maxDepth) {
        dataPool.initFirst();

        TreeSet<Order> orders = new TreeSet<>();
        orders.add(new NormalOrder(initField, null, maxClearLine, maxDepth));

        for (int depth = 0; depth < maxDepth; depth++) {
            dataPool.initEachDepth();

            assert depth < pieces.length;
            boolean isLast = depth == maxDepth - 1;

            for (int count = 0, size = orders.size(); count < size; count++) {
                Order order = orders.pollFirst();
                searcherCore.stepWithNextNoHold(candidate, validator, pieces[depth], order, isLast, depth);
            }

            orders = dataPool.getNexts();
        }

        return dataPool.getResults();
    }
}