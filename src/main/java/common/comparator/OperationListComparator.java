package common.comparator;

import common.datastore.Operation;

import java.util.Comparator;
import java.util.List;

public class OperationListComparator implements Comparator<List<Operation>> {
    public static int compareOperation(List<Operation> o1, List<Operation> o2) {
        int size1 = o1.size();
        int size2 = o2.size();
        int compareSize = Integer.compare(size1, size2);
        if (compareSize != 0)
            return compareSize;

        for (int index = 0; index < size1; index++) {
            int compare = Operation.compareTo(o1.get(index), o2.get(index));
            if (compare != 0)
                return compare;
        }

        return 0;
    }

    @Override
    public int compare(List<Operation> o1, List<Operation> o2) {
        return compareOperation(o1, o2);
    }
}
