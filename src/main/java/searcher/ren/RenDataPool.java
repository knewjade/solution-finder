package searcher.ren;

import common.comparator.RenOrderComparator;
import common.datastore.RenResult;
import common.datastore.order.RenOrder;
import searcher.common.DataPool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

public class RenDataPool implements DataPool<RenOrder, RenResult> {
    private TreeSet<RenOrder> nexts;
    private ArrayList<RenResult> results;
    private final Comparator<RenOrder> comparator = new RenOrderComparator();

    public void initFirst(RenOrder order) {
        this.results = new ArrayList<>();
        TreeSet<RenOrder> orders = new TreeSet<>(comparator);
        orders.add(order);
        this.nexts = orders;
    }

    void resetResults() {
        this.results = new ArrayList<>();
    }

    public void initEachDepth() {
        this.nexts = new TreeSet<>(comparator);
    }

    @Override
    public void addOrder(RenOrder order) {
        assert this.nexts != null;
        nexts.add(order);
    }

    @Override
    public void addResult(RenResult result) {
        assert this.results != null;
        results.add(result);
    }

    public TreeSet<RenOrder> getNexts() {
        return nexts;
    }

    public ArrayList<RenResult> getResults() {
        return results;
    }
}
