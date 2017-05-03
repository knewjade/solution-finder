package searcher.checkmate;

import searcher.common.DataPool;
import searcher.common.Result;
import common.datastore.order.Order;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentCheckmateDataPool implements DataPool {
    private final AtomicInteger addOrderLockObject = new AtomicInteger();   // lock object for addOrder
    private final AtomicInteger addResultLockObject = new AtomicInteger();  // lock object for addResult

    private final CheckmateDataPool checkmateDataPool;

    ConcurrentCheckmateDataPool() {
        this.checkmateDataPool = new CheckmateDataPool();
    }

    void initFirst() {
        checkmateDataPool.initFirst();
    }

    void initEachDepth() {
        checkmateDataPool.initEachDepth();
    }

    @Override
    public void addOrder(Order order) {
        synchronized (addOrderLockObject) {
            checkmateDataPool.addOrder(order);
        }
    }

    @Override
    public void addResult(Result result) {
        synchronized (addResultLockObject) {
            checkmateDataPool.addResult(result);
        }
    }

    TreeSet<Order> getNexts() {
        return checkmateDataPool.getNexts();
    }

    ArrayList<Result> getResults() {
        return checkmateDataPool.getResults();
    }
}
