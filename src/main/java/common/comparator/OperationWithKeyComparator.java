package common.comparator;

import common.datastore.OperationWithKey;
import core.mino.Mino;

import java.util.Comparator;

public class OperationWithKeyComparator<T extends OperationWithKey> implements Comparator<T> {
    public static int compareOperationWithKey(OperationWithKey o1, OperationWithKey o2) {
        int compareBlock = o1.getBlock().compareTo(o2.getBlock());
        if (compareBlock != 0)
            return compareBlock;

        int compareRotate = o1.getRotate().compareTo(o2.getRotate());
        if (compareRotate != 0)
            return compareRotate;

        int compareX = Integer.compare(o1.getX(), o2.getX());
        if (compareX != 0)
            return compareX;

        int compareY = Integer.compare(o1.getY(), o2.getY());
        if (compareY != 0)
            return compareY;

        return Long.compare(o1.getNeedDeletedKey(), o2.getNeedDeletedKey());
    }

    @Override
    public int compare(T o1, T o2) {
        return compareOperationWithKey(o1, o2);
    }
}
