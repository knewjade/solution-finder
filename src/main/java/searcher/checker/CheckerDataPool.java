package searcher.checker;

import searcher.common.DataPool;
import searcher.common.Result;
import searcher.common.order.Order;

import java.util.ArrayList;
import java.util.TreeSet;

public class CheckerDataPool implements DataPool {
    private TreeSet<Order> nexts;
    private TreeSet<Order> test;
    private ArrayList<Result> results;

    private int mergins = 0;

    void initFirst() {
        this.results = new ArrayList<>();
        this.nexts = new TreeSet<>();
        this.test = new TreeSet<>();
        mergins = 0;
    }

    @Override
    public void addOrder(Order order) {
        boolean add = test.add(order);
        if (!add)
            mergins += 1;
        else
            nexts.add(order);
    }

    @Override
    public void addResult(Result result) {
        results.add(result);
    }

    TreeSet<Order> getNexts() {
        return nexts;
    }

    ArrayList<Result> getResults() {
        return results;
    }
}
