package searcher.checkmate;

import common.datastore.Result;
import common.datastore.action.Action;
import common.datastore.order.NormalOrder;
import common.datastore.order.Order;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.Piece;
import searcher.common.validator.Validator;
import searcher.core.SearcherCore;
import searcher.core.SimpleSearcherCore;

import java.util.List;
import java.util.TreeSet;

public class CheckmateNoHold<T extends Action> implements Checkmate<T> {
    private final CheckmateDataPool dataPool;
    private final SearcherCore<T, Order> searcherCore;

    public CheckmateNoHold(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckmateDataPool();
        this.searcherCore = new SimpleSearcherCore<>(minoFactory, validator, dataPool);
    }

    @Override
    public List<Result> search(Field initField, List<Piece> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Piece[] blocks = new Piece[pieces.size()];
        return search(initField, pieces.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    @Override
    public List<Result> search(Field initField, Piece[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Field freeze = initField.freeze(maxClearLine);
        int deleteLine = freeze.clearLine();

        dataPool.initFirst(new NormalOrder(freeze, null, maxClearLine - deleteLine, maxDepth));

        for (int depth = 0; depth < maxDepth; depth++) {
            TreeSet<Order> orders = dataPool.getNexts();

            dataPool.initEachDepth();

            assert depth < pieces.length;
            boolean isLast = depth == maxDepth - 1;

            for (int count = 0, size = orders.size(); count < size; count++) {
                Order order = orders.pollFirst();
                searcherCore.stepWithNextNoHold(candidate, pieces[depth], order, isLast);
            }
        }

        return dataPool.getResults();
    }
}