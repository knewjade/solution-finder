package searcher;

import common.datastore.action.Action;
import common.datastore.order.NormalOrder;
import common.datastore.order.Order;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Piece;
import core.mino.MinoFactory;
import searcher.checkmate.CheckmateDataPool;
import searcher.common.validator.Validator;
import searcher.core.SimpleSearcherCore;

import java.util.List;
import java.util.TreeSet;

public class PutterUsingHold<T extends Action> {
    private final CheckmateDataPool dataPool;
    private final SimpleSearcherCore<T> searcherCore;

    public PutterUsingHold(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckmateDataPool();
        this.searcherCore = new SimpleSearcherCore<>(minoFactory, validator, dataPool);
    }

    public TreeSet<Order> first(Field initField, List<Piece> headPieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Piece[] pieces = new Piece[headPieces.size()];
        return first(initField, headPieces.toArray(pieces), candidate, maxClearLine, maxDepth);
    }

    public TreeSet<Order> first(Field initField, Piece[] headPieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Field freeze = initField.freeze(maxClearLine);
        int deleteLine = freeze.clearLine();

        dataPool.initFirst(new NormalOrder(freeze, headPieces[0], maxClearLine - deleteLine, maxDepth));

        int firstMaxDepth = headPieces.length;
        for (int depth = 1; depth < firstMaxDepth; depth++) {
            TreeSet<Order> orders = dataPool.getNexts();

            dataPool.initEachDepth();

            boolean isLast = depth == maxDepth;

            if (depth < headPieces.length) {
                Piece drawn = headPieces[depth];

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
