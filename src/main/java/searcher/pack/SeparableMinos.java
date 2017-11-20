package searcher.pack;

import common.datastore.MinimalOperationWithKey;
import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import searcher.pack.separable_mino.AllSeparableMinoFactory;
import searcher.pack.separable_mino.SeparableMino;

import java.util.*;

public class SeparableMinos {
    private static final Comparator<SeparableMino> SEPARABLE_MINO_COMPARATOR = new FullOperationSeparableMinoComparator();

    public static SeparableMinos createSeparableMinos(MinoFactory minoFactory, MinoShifter minoShifter, SizedBit sizedBit) {
        int height = sizedBit.getHeight();
        long mask = KeyOperators.getMaskForKeyBelowY(height);
        return createSeparableMinos(minoFactory, minoShifter, sizedBit, mask);
    }

    public static SeparableMinos createSeparableMinos(MinoFactory minoFactory, MinoShifter minoShifter, SizedBit sizedBit, long deleteKeyMask) {
        AllSeparableMinoFactory factory = new AllSeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight(), deleteKeyMask);
        Set<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
    }

    private final ArrayList<SeparableMino> separableMinos;
    private final HashMap<OperationWithKey, Integer> indexes;

    public SeparableMinos(Set<SeparableMino> separableMinos) {
        this(new ArrayList<>(separableMinos));
    }

    public SeparableMinos(List<SeparableMino> separableMinos) {
        // 順番を一意に固定する
        ArrayList<SeparableMino> minos = new ArrayList<>(separableMinos);
        minos.sort(SEPARABLE_MINO_COMPARATOR);
        this.separableMinos = minos;

        HashMap<OperationWithKey, Integer> indexes = new HashMap<>();
        for (int index = 0; index < minos.size(); index++) {
            SeparableMino separableMino = minos.get(index);
            MinoOperationWithKey operationWithKey = separableMino.toMinoOperationWithKey();
            indexes.put(operationWithKey, index);
        }
        this.indexes = indexes;
    }

    public List<SeparableMino> getMinos() {
        return separableMinos;
    }

    private int toIndex(OperationWithKey operation) {
        Integer index = indexes.getOrDefault(operation, -1);
        assert 0 <= index;
        return index;
    }

    public int toIndex(SeparableMino separableMino) {
        MinoOperationWithKey operationWithKey = separableMino.toMinoOperationWithKey();
        return toIndex(operationWithKey);
    }

    public int toIndex(Mino mino, int x, int y, long deleteKey) {
        OperationWithKey operation = new MinimalOperationWithKey(mino, x, y, deleteKey);
        return toIndex(operation);
    }
}
