package searcher.checkmate;

import common.comparator.FieldComparator;
import common.datastore.Result;
import common.datastore.action.Action;
import common.datastore.order.NormalOrder;
import common.datastore.order.Order;
import core.action.candidate.Candidate;
import core.field.Field;
import core.field.SmallField;
import core.mino.Block;
import core.mino.MinoFactory;
import searcher.common.validator.Validator;
import searcher.core.SimpleSearcherCore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class CheckmateUsingHoldReuse<T extends Action> implements Checkmate<T> {
    private final CheckmateDataPool dataPool;
    private final SimpleSearcherCore<T> searcherCore;
    private final Comparator<Field> fieldComparator = new FieldComparator();

    private List<TreeSet<Order>> memento = null;
    private Block[] lastBlocks = null;
    private Field lastField = new SmallField();

    public CheckmateUsingHoldReuse(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckmateDataPool();
        this.searcherCore = new SimpleSearcherCore<T>(minoFactory, validator, dataPool);
    }

    @Override
    public List<Result> search(Field initField, List<Block> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Block[] blocks = new Block[pieces.size()];
        return search(initField, pieces.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    @Override
    public List<Result> search(Field initFieldOrigin, Block[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Field initField = initFieldOrigin.freeze(maxClearLine);
        int deleteLine = initField.clearLine();
        int height = maxClearLine - deleteLine;

        TreeSet<Order> orders = new TreeSet<>();

        // 最初の探索開始depthとordersを調整
        int startDepth;
        if (!equalsField(lastField, initField) || lastBlocks == null) {
            // mementoの初期化
            // 初めから
            memento = new ArrayList<>();
            orders.add(new NormalOrder(initField, pieces[0], height, maxDepth));
            startDepth = 1;
            memento.add(new TreeSet<>(orders));
        } else {
            int reuseIndex = -1;
            int minLength = lastBlocks.length < pieces.length ? lastBlocks.length : pieces.length;
            int max = maxDepth + 1 < minLength ? maxDepth + 1 : minLength;
            for (int index = 0; index < max; index++) {
                if (lastBlocks[index] == pieces[index])
                    reuseIndex = index;
                else
                    break;
            }

            if (reuseIndex < 0) {
                memento = new ArrayList<>();
                orders.add(new NormalOrder(initField, pieces[0], height, maxDepth));
                startDepth = 1;
                memento.add(new TreeSet<>(orders));
            } else if (reuseIndex == maxDepth) {
                return dataPool.getResults();
            } else {
                orders.addAll(memento.get(reuseIndex));
                startDepth = reuseIndex + 1;
                memento = memento.subList(0, reuseIndex + 1);
            }
        }

        dataPool.resetResults();

        for (int depth = startDepth; depth <= maxDepth; depth++) {
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

            orders = dataPool.getNexts();
            memento.add(new TreeSet<>(orders));
        }

        lastBlocks = pieces;
        lastField = initField;

        return dataPool.getResults();
    }

    private boolean equalsField(Field left, Field right) {
        return fieldComparator.compare(left, right) == 0;
    }
}
