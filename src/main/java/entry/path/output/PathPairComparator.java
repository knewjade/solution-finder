package entry.path.output;

import common.datastore.OperationWithKey;
import core.mino.Mino;
import entry.path.PathPair;

import java.util.Comparator;
import java.util.List;

public class PathPairComparator implements Comparator<PathPair> {
    @Override
    public int compare(PathPair o1, PathPair o2) {
        List<OperationWithKey> operations1 = o1.getSampleOperations();
        List<OperationWithKey> operations2 = o2.getSampleOperations();

        int compareSize = Integer.compare(operations1.size(), operations2.size());
        if (compareSize != 0)
            return compareSize;

        for (int index = 0; index < operations1.size(); index++) {
            Mino mino1 = operations1.get(index).getMino();
            Mino mino2 = operations2.get(index).getMino();

            int compareBlock = mino1.getBlock().compareTo(mino2.getBlock());
            if (compareBlock != 0)
                return compareBlock;

            int compareRotate = mino1.getRotate().compareTo(mino2.getRotate());
            if (compareRotate != 0)
                return compareRotate;
        }

        return 0;
    }
}
