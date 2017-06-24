package searcher.pack;

import common.comparator.OperationWithKeyComparator;
import common.datastore.OperationWithKey;
import searcher.pack.mino_field.MinoField;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MinoFieldComparator implements Comparator<MinoField> {
    public static int compareMinoField(MinoField o1, MinoField o2) {
        List<OperationWithKey> operations1 = o1.getOperationsStream()
                .sorted(OperationWithKeyComparator::compareOperationWithKey)
                .collect(Collectors.toList());
        List<OperationWithKey> operations2 = o2.getOperationsStream()
                .sorted(OperationWithKeyComparator::compareOperationWithKey)
                .collect(Collectors.toList());
        int compareSize = Integer.compare(operations1.size(), operations2.size());
        if (compareSize != 0)
            return compareSize;

        for (int index = 0; index < operations1.size(); index++) {
            int compare = OperationWithKeyComparator.compareOperationWithKey(operations1.get(index), operations2.get(index));
            if (compare != 0)
                return compare;
        }

        return 0;
    }

    @Override
    public int compare(MinoField o1, MinoField o2) {
        return compareMinoField(o1, o2);
    }
}
