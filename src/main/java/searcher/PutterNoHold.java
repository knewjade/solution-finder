package searcher;

import common.datastore.Result;
import common.datastore.action.Action;
import common.datastore.order.NormalOrder;
import common.datastore.order.Order;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.Piece;
import searcher.checkmate.CheckmateDataPool;
import searcher.common.validator.Validator;
import searcher.core.SearcherCore;
import searcher.core.SimpleSearcherCore;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class PutterNoHold<T extends Action> {
    private final CheckmateDataPool dataPool;
    private final SearcherCore<T, Order> searcherCore;

    public PutterNoHold(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckmateDataPool();
        this.searcherCore = new SimpleSearcherCore<>(minoFactory, validator, dataPool);
    }

    public PutterNoHold(CheckmateDataPool dataPool, SearcherCore<T, Order> searcherCore) {
        this.dataPool = dataPool;
        this.searcherCore = searcherCore;
    }

    public TreeSet<Order> first(Field initField, List<Piece> headPieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Piece[] pieces = new Piece[headPieces.size()];
        return first(initField, headPieces.toArray(pieces), candidate, maxClearLine, maxDepth);
    }

    public TreeSet<Order> first(Field initField, Piece[] headPieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Field freeze = initField.freeze(maxClearLine);
        int deleteLine = freeze.clearLine();

        dataPool.initFirst(new NormalOrder(freeze, null, maxClearLine - deleteLine, maxDepth));

        for (int depth = 0; depth < headPieces.length; depth++) {
            TreeSet<Order> orders = dataPool.getNexts();

            dataPool.initEachDepth();

            boolean isLast = depth == maxDepth - 1;

            for (int count = 0, size = orders.size(); count < size; count++) {
                Order order = orders.pollFirst();
                searcherCore.stepWithNextNoHold(candidate, headPieces[depth], order, isLast);
            }
        }

        return dataPool.getNexts();
    }

    public ArrayList<Result> getResults() {
        return dataPool.getResults();
    }
}
