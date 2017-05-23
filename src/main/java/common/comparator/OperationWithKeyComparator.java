package common.comparator;

import common.datastore.OperationWithKey;
import core.mino.Mino;

import java.util.Comparator;

// TODO: unittest: write
public class OperationWithKeyComparator implements Comparator<OperationWithKey> {
    public static int compareOperationWithKey(OperationWithKey o1, OperationWithKey o2) {
        Mino mino1 = o1.getMino();
        Mino mino2 = o2.getMino();

        int compareBlock = mino1.getBlock().compareTo(mino2.getBlock());
        if (compareBlock != 0)
            return compareBlock;

        int compareRotate = mino1.getRotate().compareTo(mino2.getRotate());
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
    public int compare(OperationWithKey o1, OperationWithKey o2) {
        return compareOperationWithKey(o1, o2);
    }
}
