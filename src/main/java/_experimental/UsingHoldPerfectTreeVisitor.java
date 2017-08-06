package _experimental;

import common.datastore.action.Action;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import lib.Stopwatch;
import searcher.common.validator.PerfectValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/**
 * マルチスレッド非対応
 */
public class UsingHoldPerfectTreeVisitor implements TreeVisitor {
    private static final PrioritizedElementComparator COMPARATOR = new PrioritizedElementComparator();

    private final Field initField;
    private final int maxClearLine;
    private final int maxDepth;
    private final List<Block> blocks;

    private final List<HashSet<SequenceElement>> elements;
    private PriorityQueue<SequenceElement> queue = new PriorityQueue<>(COMPARATOR);
    private PriorityQueue<SequenceElement> temp = new PriorityQueue<>(COMPARATOR);
    private final SimpleSearcherCore2<Action> searcherCore;

    public final Stopwatch stopwatch = Stopwatch.createStoppedStopwatch();

    private int startIndex = 0;

    UsingHoldPerfectTreeVisitor(Field initField, int maxClearLine, int maxDepth) {
        this.initField = initField;
        this.maxClearLine = maxClearLine;
        this.maxDepth = maxDepth;

        int maxListSize = maxDepth + 1;
        List<Block> blocks = new ArrayList<>();
        for (int index = 0; index < maxListSize; index++)
            blocks.add(null);
        this.blocks = blocks;

        List<HashSet<SequenceElement>> elements = new ArrayList<>();
        for (int index = 0; index < maxListSize; index++)
            elements.add(new HashSet<>());
        this.elements = elements;

        MinoFactory minoFactory = new MinoFactory();
        LockedCandidate candidate = new LockedCandidate(minoFactory, new MinoShifter(), new MinoRotation(), maxClearLine);
        this.searcherCore = new SimpleSearcherCore2<>(minoFactory, new PerfectValidator(), this, candidate);
    }

    @Override
    public void visit(int depth, Block block) {
        blocks.set(depth, block);
        if (depth < startIndex)
            startIndex = depth;
    }

    @Override
    public boolean execute(int piecesLength) {
        // 過去の結果をクリア
        for (int depth = startIndex, size = elements.size(); depth < size; depth++)
            this.elements.get(depth).clear();

        if (startIndex == 0) {
            // 先頭が変わったとき
            queue.clear();
            register(new SequenceElement(initField, blocks.get(0), maxClearLine, 0));
        } else if (startIndex <= maxDepth) {
            // queueの整理
            while (!queue.isEmpty()) {
                SequenceElement poll = queue.poll();
                int depth = poll.getDepth();
                if (depth < startIndex)
                    temp.add(poll);
            }

            PriorityQueue<SequenceElement> swap = this.queue;
            this.queue = this.temp;
            this.temp = swap;
        }

        System.out.println(queue.size());

        // 探索インデックスを現在の位置でリセット
        startIndex = piecesLength;

        System.out.println(blocks);
        stopwatch.start();

        // 新たに探索する
//        int counter = 0;
        while (!queue.isEmpty()) {
            // 現在のblocksの探索と関係ない要素はスキップする

//            counter++;
            SequenceElement element = queue.poll();
            int depth = element.getDepth();
            int index = depth + 1;

            boolean isLast = index == maxDepth;
            boolean result = search(piecesLength, element, index, isLast);

            if (result) {
                stopwatch.stop();
//                System.out.println(counter);
                return true;
            }
        }
        stopwatch.stop();
//        System.out.println(counter);

        return false;
    }

    private boolean search(int piecesLength, SequenceElement element, int index, boolean isLast) {
        if (index < piecesLength) {
            return searcherCore.stepWithNext(blocks.get(index), element, isLast);
        } else {
            return searcherCore.stepWhenNoNext(element, isLast);
        }
    }

    public void register(SequenceElement element) {
        int depth = element.getDepth();
        boolean isNotExist = elements.get(depth).add(element);
        if (isNotExist)
            queue.add(element);
    }
}
