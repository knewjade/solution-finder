package searcher.checkmate;

import common.datastore.Result;
import common.datastore.order.NormalOrder;
import common.datastore.order.Order;
import searcher.common.DataPool;

import java.util.ArrayList;
import java.util.TreeSet;

public class CheckmateDataPool implements DataPool {
    private TreeSet<Order> nexts;
    private ArrayList<Result> results;

    public void initFirst(NormalOrder order) {
        this.results = new ArrayList<>();
        TreeSet<Order> orders = new TreeSet<>();
        orders.add(order);
        this.nexts = orders;
    }

    void resetResults() {
        this.results = new ArrayList<>();
    }

    public void initEachDepth() {
        this.nexts = new TreeSet<>();
    }

    @Override
    public void addOrder(Order order) {
        nexts.add(order);
    }

    @Override
    public void addResult(Result result) {
        results.add(result);
    }

    public TreeSet<Order> getNexts() {
        return nexts;
    }

    public ArrayList<Result> getResults() {
        return results;
    }
}
