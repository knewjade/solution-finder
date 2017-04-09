package searcher.checkmate;

import searcher.common.DataPool;
import searcher.common.Result;
import searcher.common.order.Order;

import java.util.ArrayList;
import java.util.TreeSet;

public class ConcurrentCheckmateDataPool implements DataPool {
    private final Integer addOrderLockObject = 0;   // lock object for addOrder
    private final Integer addResultLockObject = 0;  // lock object for addResult

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
