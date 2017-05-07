package searcher.checkmate;

import searcher.common.DataPool;
import common.datastore.Result;
import common.datastore.order.Order;

import java.util.ArrayList;
import java.util.TreeSet;

public class CheckmateDataPool implements DataPool {
    private TreeSet<Order> nexts;
    private ArrayList<Result> results;

    public void initFirst() {
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
