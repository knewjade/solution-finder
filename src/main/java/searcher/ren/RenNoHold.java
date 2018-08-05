package searcher.ren;

import common.datastore.RenResult;
import common.datastore.action.Action;
import common.datastore.blocks.Pieces;
import common.datastore.order.RenNormalOrder;
import common.datastore.order.RenOrder;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.Piece;
import searcher.core.RenSearcherCore;

import java.util.List;
import java.util.TreeSet;

public class RenNoHold<T extends Action> implements RenSearcher<T> {
    private static final int MAX_FIELD_HEIGHT = 24;
    private final RenDataPool dataPool;
    private final RenSearcherCore<T> searcherCore;

    public RenNoHold(MinoFactory minoFactory) {
        this.dataPool = new RenDataPool();
        this.searcherCore = new RenSearcherCore<>(minoFactory, dataPool, MAX_FIELD_HEIGHT);
    }

    @Override
    public List<RenResult> check(Field initField, Pieces pieces, Candidate<T> candidate, int maxDepth) {
        return check(initField, pieces.getPieceArray(), candidate, maxDepth);
    }

    @Override
    public List<RenResult> check(Field initField, List<Piece> pieces, Candidate<T> candidate, int maxDepth) {
        Piece[] blocks = new Piece[pieces.size()];
        return check(initField, pieces.toArray(blocks), candidate, maxDepth);
    }

    @Override
    public List<RenResult> check(Field initField, Piece[] pieces, Candidate<T> candidate, int maxDepth) {
        Field freeze = initField.freeze(MAX_FIELD_HEIGHT);
        freeze.clearLine();

        dataPool.initFirst(new RenNormalOrder(freeze, null, 0, maxDepth));

        for (int depth = 0; depth < maxDepth; depth++) {
            TreeSet<RenOrder> orders = dataPool.getNexts();

            dataPool.initEachDepth();

            assert depth < pieces.length;
            boolean isLast = depth == maxDepth - 1;

            for (int count = 0, size = orders.size(); count < size; count++) {
                RenOrder order = orders.pollFirst();
                searcherCore.stepWithNextNoHold(candidate, pieces[depth], order, isLast);
            }
        }

        return dataPool.getResults();
    }
}
