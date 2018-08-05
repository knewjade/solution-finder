package common.comparator;

import common.datastore.order.RenOrder;
import core.mino.Piece;

import java.util.Comparator;

public class RenOrderComparator implements Comparator<RenOrder> {
    public static int compareOrder(RenOrder o1, RenOrder o2) {
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
    public int compare(RenOrder o1, RenOrder o2) {
        return compareOrder(o1, o2);
    }
}
