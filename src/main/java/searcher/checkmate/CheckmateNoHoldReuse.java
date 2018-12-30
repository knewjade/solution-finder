package searcher.checkmate;

import core.action.candidate.Candidate;
import core.field.Field;
import core.field.SmallField;
import core.mino.Piece;
import core.mino.MinoFactory;
import common.comparator.FieldComparator;
import common.datastore.Result;
import searcher.core.SimpleSearcherCore;
import common.datastore.action.Action;
import common.datastore.order.NormalOrder;
import common.datastore.order.Order;
import searcher.common.validator.Validator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class CheckmateNoHoldReuse<T extends Action> implements Checkmate<T> {
    private final CheckmateDataPool dataPool;
    private final SimpleSearcherCore<T> searcherCore;
    private final Comparator<Field> fieldComparator = new FieldComparator();

    private List<TreeSet<Order>> memento = null;
    private Piece[] lastPieces = null;
    private Field lastField = new SmallField();

    public CheckmateNoHoldReuse(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckmateDataPool();
        this.searcherCore = new SimpleSearcherCore<T>(minoFactory, validator, dataPool);
    }

    @Override
    public List<Result> search(Field initField, List<Piece> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Piece[] blocks = new Piece[pieces.size()];
        return search(initField, pieces.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    @Override
    public List<Result> search(Field initFieldOrigin, Piece[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Field initField = initFieldOrigin.freeze(maxClearLine);
        int deleteLine = initField.clearLine();
        int height = maxClearLine - deleteLine;

        TreeSet<Order> orders = new TreeSet<>();

        // 最初の探索開始depthとordersを調整
        int startDepth;
        if (!equalsField(lastField, initField) || lastPieces == null) {
            // mementoの初期化
            // 初めから
            memento = new ArrayList<>();
            orders.add(new NormalOrder(initField, null, height, maxDepth));
            startDepth = 0;
            memento.add(new TreeSet<>(orders));
        } else {
            int reuseIndex = -1;
            for (int index = 0; index < maxDepth; index++) {
                if (lastPieces[index] == pieces[index])
                    reuseIndex = index;
                else
                    break;
            }

            if (reuseIndex < 0) {
                memento = new ArrayList<>();
                orders.add(new NormalOrder(initField, null, height, maxDepth));
                startDepth = 0;
                memento.add(new TreeSet<>(orders));
            } else if (reuseIndex == maxDepth - 1) {
                return dataPool.getResults();
            } else {
                orders.addAll(memento.get(reuseIndex));
                startDepth = reuseIndex;
                memento = memento.subList(0, reuseIndex + 1);
            }
        }

        dataPool.resetResults();

        for (int depth = startDepth; depth < maxDepth; depth++) {
            dataPool.initEachDepth();

            assert depth < pieces.length;
            boolean isLast = depth == maxDepth - 1;

            for (int count = 0, size = orders.size(); count < size; count++) {
                Order order = orders.pollFirst();
                searcherCore.stepWithNextNoHold(candidate, pieces[depth], order, isLast);
            }

            orders = dataPool.getNexts();
            memento.add(new TreeSet<>(orders));
        }

        lastPieces = pieces;
        lastField = initField;

        return dataPool.getResults();
    }

    private boolean equalsField(Field left, Field right) {
        return fieldComparator.compare(left, right) == 0;
    }
}
