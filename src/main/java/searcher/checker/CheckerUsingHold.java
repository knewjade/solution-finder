package searcher.checker;

import common.OperationHistory;
import common.datastore.Result;
import common.datastore.action.Action;
import common.datastore.blocks.Pieces;
import common.datastore.order.DepthOrder;
import common.datastore.order.Order;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.Piece;
import searcher.common.validator.Validator;
import searcher.core.SimpleSearcherCore;

import java.util.List;

public class CheckerUsingHold<T extends Action> implements Checker<T> {
    private final CheckerDataPool dataPool;
    private final SimpleSearcherCore<T> searcherCore;

    public CheckerUsingHold(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckerDataPool();
        this.searcherCore = new SimpleSearcherCore<>(minoFactory, validator, dataPool);
    }

    @Override
    public boolean check(Field initField, Pieces pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        return check(initField, pieces.getPieceArray(), candidate, maxClearLine, maxDepth);
    }

    @Override
    public boolean check(Field initField, List<Piece> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Piece[] blocks = new Piece[pieces.size()];
        return check(initField, pieces.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    @Override
    public boolean check(Field initField, Piece[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Field freeze = initField.freeze(maxClearLine);
        int deleteLine = freeze.clearLine();

        dataPool.initFirst();
        dataPool.addOrder(new DepthOrder(freeze, pieces[0], maxClearLine - deleteLine, maxDepth));

        while (!dataPool.getNexts().isEmpty() && dataPool.getResults().isEmpty()) {
            Order order = dataPool.getNexts().pollLast();
            OperationHistory history = order.getHistory();
            assert history != null;
            int depth = history.getNextIndex() + 1;
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
