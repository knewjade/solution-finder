package searcher.pack;

import common.comparator.OperationWithKeyComparator;
import common.datastore.MinoOperationWithKey;
import searcher.pack.separable_mino.SeparableMino;

import java.util.Comparator;

public class FullOperationSeparableMinoComparator implements Comparator<SeparableMino> {
    private static final OperationWithKeyComparator<MinoOperationWithKey> COMPARATOR = new OperationWithKeyComparator<>();

    public static int compareSeparableMino(SeparableMino o1, SeparableMino o2) {
        MinoOperationWithKey operationWithKey1 = o1.toMinoOperationWithKey();
        MinoOperationWithKey operationWithKey2 = o2.toMinoOperationWithKey();
        return COMPARATOR.compare(operationWithKey1, operationWithKey2);
    }

    @Override
    public int compare(SeparableMino o1, SeparableMino o2) {
        return compareSeparableMino(o1, o2);
    }
}
