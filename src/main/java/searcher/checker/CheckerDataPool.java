package searcher.checker;

import common.comparator.DepthOrderComparator;
import common.datastore.Result;
import common.datastore.order.Order;
import searcher.common.DataPool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

public class CheckerDataPool implements DataPool {
    private TreeSet<Order> nexts;
    private TreeSet<Order> existsCheck;
    private ArrayList<Result> results;
    private final Comparator<Order> comparator = new DepthOrderComparator();

    void initFirst() {
        this.results = new ArrayList<>();
        this.nexts = new TreeSet<>(comparator);
        this.existsCheck = new TreeSet<>(comparator);
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
