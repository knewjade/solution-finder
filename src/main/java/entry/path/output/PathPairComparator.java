package entry.path.output;

import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import entry.path.PathPair;

import java.util.Comparator;
import java.util.List;

public class PathPairComparator implements Comparator<PathPair> {
    @Override
    public int compare(PathPair o1, PathPair o2) {
        int compareDeletedLine = Boolean.compare(o1.isDeletedLine(), o2.isDeletedLine());
        if (compareDeletedLine != 0)
            return compareDeletedLine;

        List<MinoOperationWithKey> operations1 = o1.getSampleOperations();
        List<MinoOperationWithKey> operations2 = o2.getSampleOperations();

        int compareSize = Integer.compare(operations1.size(), operations2.size());
        if (compareSize != 0)
            return compareSize;

        for (int index = 0; index < operations1.size(); index++) {
            OperationWithKey operation1 = operations1.get(index);
            OperationWithKey operation2 = operations2.get(index);

            int compareBlock = operation1.getPiece().compareTo(operation2.getPiece());
            if (compareBlock != 0)
                return compareBlock;

            int compareRotate = operation1.getRotate().compareTo(operation2.getRotate());
            if (compareRotate != 0)
                return compareRotate;
        }

        return 0;
    }
}
