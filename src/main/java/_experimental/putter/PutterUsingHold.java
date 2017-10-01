package _experimental.putter;

import common.datastore.action.Action;
import common.datastore.order.NormalOrder;
import common.datastore.order.Order;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import searcher.checkmate.CheckmateDataPool;
import searcher.common.validator.Validator;
import searcher.core.SimpleSearcherCore;

import java.util.List;
import java.util.TreeSet;

public class PutterUsingHold<T extends Action> {
    private final CheckmateDataPool dataPool;
    private final SimpleSearcherCore<T> searcherCore;

    PutterUsingHold(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckmateDataPool();
        this.searcherCore = new SimpleSearcherCore<T>(minoFactory, validator, dataPool);
    }

    public TreeSet<Order> search(Field initField, List<Block> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Block[] blocks = new Block[pieces.size()];
        return search(initField, pieces.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    public TreeSet<Order> search(Field initField, Block[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Field freeze = initField.freeze(maxClearLine);
        int deleteLine = freeze.clearLine();

        dataPool.initFirst(new NormalOrder(freeze, pieces[0], maxClearLine - deleteLine, maxDepth));

        int max = pieces.length <= maxDepth ? pieces.length : maxDepth;
        for (int depth = 1; depth <= max; depth++) {
            TreeSet<Order> orders = dataPool.getNexts();

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
        }

        return dataPool.getNexts();
    }
}