package common.comparator;

import common.order.StackOrder;
import common.NotImplementedException;

import java.util.Comparator;

public class StackOrderComparator implements Comparator<StackOrder> {
    public static int compareStackOrder(StackOrder o1, StackOrder o2) {
        throw new NotImplementedException();
    }

    @Override
    public int compare(StackOrder o1, StackOrder o2) {
        return compareStackOrder(o1, o2);
    }
}
