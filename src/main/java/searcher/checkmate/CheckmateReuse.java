package searcher.checkmate;

import action.candidate.Candidate;
import core.field.Field;
import core.field.SmallField;
import core.mino.Block;
import core.mino.MinoFactory;
import searcher.common.SearcherCore;
import searcher.common.action.Action;
import searcher.common.Result;
import searcher.common.order.Order;
import searcher.common.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class CheckmateReuse<T extends Action> {
    private final CheckmateDataPool dataPool;
    private final SearcherCore<T> searcherCore;

    private List<TreeSet<Order>> memento = null;
    private Block[] lastBlocks = null;
    private Field lastField = new SmallField();

    public CheckmateReuse(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckmateDataPool();
        this.searcherCore = new SearcherCore<T>(minoFactory, validator, dataPool);
    }

    // holdあり
    public List<Result> search(Field initField, List<Block> blockList, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Block[] blocks = new Block[blockList.size()];
        return search(initField, blockList.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    public List<Result> search(Field initField, Block[] blocks, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        TreeSet<Order> orders = new TreeSet<>();

        // 最初の探索開始depthとordersを調整
        int startDepth = -1;
        if (!equalsField(lastField, initField) || lastBlocks == null) {
            // mementoの初期化
            // 初めから
            memento = new ArrayList<>();
            orders.add(new Order(initField, blocks[0], maxClearLine, maxDepth));
            startDepth = 1;
            memento.add(new TreeSet<>(orders));
        } else {
            int reuseIndex = -1;
            for (int index = 0; index < blocks.length; index++) {
                if (lastBlocks[index] == blocks[index])
                    reuseIndex = index;
                else
                    break;
            }

            if (reuseIndex < 0) {
                memento = new ArrayList<>();
                orders.add(new Order(initField, blocks[0], maxClearLine, maxDepth));
                startDepth = 1;
                memento.add(new TreeSet<>(orders));
            } else if (reuseIndex == blocks.length - 1) {
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

            if (depth < blocks.length) {
                Block drawn = blocks[depth];

                for (int count = 0, size = orders.size(); count < size; count++) {
                    Order order = orders.pollFirst();
                    searcherCore.stepNormal(candidate, drawn, order, isLast);
                }
            } else {
                for (int count = 0, size = orders.size(); count < size; count++) {
                    Order order = orders.pollFirst();
                    searcherCore.stepLastWhenNoNext(candidate, order, isLast);
                }
            }

            orders = dataPool.getNexts();
            memento.add(new TreeSet<>(orders));
        }

        lastBlocks = blocks;
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
