package searcher.checkmate;

import action.candidate.Candidate;
import core.field.Field;
import core.field.SmallField;
import core.mino.Block;
import core.mino.MinoFactory;
import searcher.common.Result;
import searcher.common.SimpleSearcherCore;
import searcher.common.action.Action;
import searcher.common.order.NormalOrder;
import searcher.common.order.Order;
import searcher.common.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class CheckmateUsingHoldReuse<T extends Action> implements Checkmate<T> {
    private final CheckmateDataPool dataPool;
    private final SimpleSearcherCore<T> searcherCore;

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
    public List<Result> search(Field initField, Block[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        TreeSet<Order> orders = new TreeSet<>();

        // 最初の探索開始depthとordersを調整
        int startDepth;
        if (!equalsField(lastField, initField) || lastBlocks == null) {
            // mementoの初期化
            // 初めから
            memento = new ArrayList<>();
            orders.add(new NormalOrder(initField, pieces[0], maxClearLine, maxDepth));
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
                orders.add(new NormalOrder(initField, pieces[0], maxClearLine, maxDepth));
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

        dataPool.initFirst();

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
        int boardCount = left.getBoardCount();
        if (boardCount != right.getBoardCount())
            return false;

        for (int index = 0; index < boardCount; index++)
            if (left.getBoard(index) != right.getBoard(index))
                return false;
        return true;
    }
}
