package searcher.checkmate;

import action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import concurrent.CandidateThreadLocal;
import searcher.common.SearcherCore;
import searcher.common.action.Action;
import searcher.common.Result;
import searcher.common.order.Order;
import searcher.common.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class ConcurrentCheckmate<T extends Action> {
    private final CandidateThreadLocal candidateThreadLocal;
    private final ExecutorService executorService;

    private final ConcurrentCheckmateDataPool dataPool;
    private final SearcherCore<Action> searcherCore;

    private CountDownLatch latch;

    public ConcurrentCheckmate(CandidateThreadLocal threadLocal, MinoFactory minoFactory, Validator validator, ExecutorService executorService) {
        this.candidateThreadLocal = threadLocal;
        this.executorService = executorService;

        this.dataPool = new ConcurrentCheckmateDataPool();
        this.searcherCore = new SearcherCore<>(minoFactory, validator, dataPool);
    }

    // holdあり
    public List<Result> search(Field initField, List<Block> blockList, int maxClearLine, int maxDepth) {
        Block[] blocks = new Block[blockList.size()];
        return search(initField, blockList.toArray(blocks), maxClearLine, maxDepth);
    }

    public List<Result> search(Field initField, Block[] blocks, int maxClearLine, int maxDepth) {
        dataPool.initFirst();

        TreeSet<Order> orders = new TreeSet<>();
        orders.add(new Order(initField, blocks[0], maxClearLine, maxDepth));

        for (int depth = 1; depth <= maxDepth; depth++) {
            dataPool.initEachDepth();

            boolean isLast = depth == maxDepth;

            int size = orders.size();
            int core = 4 * 100;
            latch = new CountDownLatch(core);

            if (depth < blocks.length) {
                Block drawn = blocks[depth];

                int lastIndex = 0;
                for (int count = 0; count < core; count++) {
                    int toIndex = (int) (size * ((double) (count + 1) / core));
                    List<Order> subList = new ArrayList<>();
                    for (int index = lastIndex; index < toIndex; index++) {
                        Order order = orders.pollFirst();
                        subList.add(order);
                    }

                    lastIndex = toIndex;

                    executorService.execute(new MyTask(candidateThreadLocal, subList, isLast, searcherCore, latch, drawn));
                }
            } else {
                int lastIndex = 0;
                for (int count = 0; count < core; count++) {

                    int toIndex = (int) (orders.size() * ((double) (count + 1) / core));
                    List<Order> subList = new ArrayList<>();
                    for (int index = lastIndex; index < toIndex; index++) {
                        Order order = orders.pollFirst();
                        subList.add(order);
                    }

                    lastIndex = toIndex;

                    executorService.execute(new MyLastTask(candidateThreadLocal, subList, isLast, searcherCore, latch));
                }
            }

            do {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Error in Searching Checkmate Solutions");
                }
            } while (latch.getCount() != 0);


            orders = dataPool.getNexts();
        }

        return dataPool.getResults();
    }


    private static class MyTask<T extends Action> implements Runnable {
        private final CandidateThreadLocal candidateThreadLocal;
        private final List<Order> tasks;
        private final boolean isLast;
        private final SearcherCore<Action> searcherCore;
        private final CountDownLatch latch;
        private final Block drawn;

        private MyTask(CandidateThreadLocal candidateThreadLocal, List<Order> tasks, boolean isLast, SearcherCore<Action> searcherCore, CountDownLatch latch, Block drawn) {
            this.candidateThreadLocal = candidateThreadLocal;
            this.tasks = tasks;
            this.isLast = isLast;
            this.searcherCore = searcherCore;
            this.latch = latch;
            this.drawn = drawn;
        }

        @Override
        public void run() {
            Candidate<Action> candidate = candidateThreadLocal.get();
            for (Order order : tasks)
                searcherCore.stepNormal(candidate, drawn, order, isLast);
            latch.countDown();
        }
    }

    private static class MyLastTask<T extends Action> implements Runnable {
        private final CandidateThreadLocal candidateThreadLocal;
        private final List<Order> tasks;
        private final boolean isLast;
        private final SearcherCore<Action> searcherCore;
        private final CountDownLatch latch;

        private MyLastTask(CandidateThreadLocal candidateThreadLocal, List<Order> tasks, boolean isLast, SearcherCore<Action> searcherCore, CountDownLatch latch) {
            this.candidateThreadLocal = candidateThreadLocal;
            this.tasks = tasks;
            this.isLast = isLast;
            this.searcherCore = searcherCore;
            this.latch = latch;
        }

        @Override
        public void run() {
            Candidate<Action> candidate = candidateThreadLocal.get();
            for (Order order : tasks)
                searcherCore.stepLastWhenNoNext(candidate, order, isLast);
            latch.countDown();
        }
    }
}
