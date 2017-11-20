package common.comparator;

import common.datastore.order.Order;
import core.mino.Piece;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order> {
    public static int compareOrder(Order o1, Order o2) {
        Piece hold1 = o1.getHold();
        Piece hold2 = o2.getHold();
        if (hold1 == hold2) {
            return FieldComparator.compareField(o1.getField(), o2.getField());
        } else {
            int number1 = hold1 != null ? hold1.getNumber() : 7;
            int number2 = hold2 != null ? hold2.getNumber() : 7;
            return number1 - number2;
        }
    }

    @Override
    public int compare(Order o1, Order o2) {
        return compareOrder(o1, o2);
    }
}
