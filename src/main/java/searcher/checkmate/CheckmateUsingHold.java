package searcher.checkmate;

import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Piece;
import core.mino.MinoFactory;
import common.datastore.Result;
import searcher.core.SimpleSearcherCore;
import common.datastore.action.Action;
import common.datastore.order.NormalOrder;
import common.datastore.order.Order;
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
    public List<Result> search(Field initField, List<Piece> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Piece[] blocks = new Piece[pieces.size()];
        return search(initField, pieces.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    @Override
    public List<Result> search(Field initField, Piece[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Field freeze = initField.freeze(maxClearLine);
        int deleteLine = freeze.clearLine();

        dataPool.initFirst(new NormalOrder(freeze, pieces[0], maxClearLine - deleteLine, maxDepth));

        for (int depth = 1; depth <= maxDepth; depth++) {
            TreeSet<Order> orders = dataPool.getNexts();

            dataPool.initEachDepth();

            boolean isLast = depth == maxDepth;

            if (depth < pieces.length) {
                Piece drawn = pieces[depth];

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

        return dataPool.getResults();
    }
}