package searcher.checkmate;

import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import common.datastore.Result;
import searcher.core.SimpleSearcherCore;
import common.datastore.action.Action;
import common.datastore.order.NormalOrder;
import common.datastore.order.Order;
import searcher.common.validator.Validator;

import java.util.List;
import java.util.TreeSet;

public class CheckmateNoHold<T extends Action> implements Checkmate<T> {
    private final CheckmateDataPool dataPool;
    private final SimpleSearcherCore<T> searcherCore;

    public CheckmateNoHold(MinoFactory minoFactory, Validator validator) {
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
        Field freeze = initField.freeze(maxClearLine);
        int deleteLine = freeze.clearLine();

        dataPool.initFirst();

        TreeSet<Order> orders = new TreeSet<>();
        orders.add(new NormalOrder(freeze, null, maxClearLine - deleteLine, maxDepth));

        for (int depth = 0; depth < maxDepth; depth++) {
            dataPool.initEachDepth();

            assert depth < pieces.length;
            boolean isLast = depth == maxDepth - 1;

            for (int count = 0, size = orders.size(); count < size; count++) {
                Order order = orders.pollFirst();
                searcherCore.stepWithNextNoHold(candidate, pieces[depth], order, isLast);
            }

            orders = dataPool.getNexts();
        }

        return dataPool.getResults();
    }
}