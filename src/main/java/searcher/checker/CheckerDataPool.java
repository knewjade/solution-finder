package searcher.checker;

import searcher.common.DataPool;
import common.datastore.Result;
import common.datastore.order.Order;

import java.util.ArrayList;
import java.util.TreeSet;

public class CheckerDataPool implements DataPool {
    private TreeSet<Order> nexts;
    private TreeSet<Order> existsCheck;
    private ArrayList<Result> results;

    void initFirst() {
        this.results = new ArrayList<>();
        this.nexts = new TreeSet<>();
        this.existsCheck = new TreeSet<>();
    }

    @Override
    public void addOrder(Order order) {
        boolean add = existsCheck.add(order);
        if (add)
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
