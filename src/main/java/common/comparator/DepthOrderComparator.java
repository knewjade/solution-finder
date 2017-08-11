package common.comparator;

import common.datastore.order.Order;

import java.util.Comparator;

public class DepthOrderComparator implements Comparator<Order> {
    public static int compareOrder(Order o1, Order o2) {
        int compare = Integer.compare(o1.getHistory().getNextIndex(), o2.getHistory().getNextIndex());
        if (compare != 0)
            return compare;
        return OrderComparator.compareOrder(o1, o2);
    }

    @Override
    public int compare(Order o1, Order o2) {
        return compareOrder(o1, o2);
    }
}
