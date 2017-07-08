package common.comparator;

import common.datastore.Operation;

import java.util.Comparator;

public class OperationComparator implements Comparator<Operation> {
    public static int compareOperation(Operation o1, Operation o2) {
        int blockCompare = o1.getBlock().compareTo(o2.getBlock());
        if (blockCompare != 0)
            return blockCompare;

        int rotateCompare = o1.getRotate().compareTo(o2.getRotate());
        if (rotateCompare != 0)
            return rotateCompare;

        int xCompare = o1.getX() - o2.getX();
        if (xCompare != 0)
            return xCompare;

        return o1.getY() - o2.getY();
    }

    @Override
    public int compare(Operation o1, Operation o2) {
        return compareOperation(o1, o2);
    }
}
