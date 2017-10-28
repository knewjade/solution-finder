package searcher.pack;

import core.field.KeyOperators;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.Rotate;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SeparableMinos {
    private static final Comparator<SeparableMino> SEPARABLE_MINO_COMPARATOR = new SeparableMinoComparator();

    public static SeparableMinos createSeparableMinos(MinoFactory minoFactory, MinoShifter minoShifter, SizedBit sizedBit) {
        int height = sizedBit.getHeight();
        long mask = KeyOperators.getMaskForKeyBelowY(height);
        return createSeparableMinos(minoFactory, minoShifter, sizedBit, mask);
    }

    public static SeparableMinos createSeparableMinos(MinoFactory minoFactory, MinoShifter minoShifter, SizedBit sizedBit, long deleteKeyMask) {
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight(), deleteKeyMask);
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
    }

    private final ArrayList<SeparableMino> separableMinos;
    private final HashMap<Key, Integer> indexes;

    public SeparableMinos(List<SeparableMino> separableMinos) {
        // 順番を一意に固定する
        ArrayList<SeparableMino> minos = new ArrayList<>(separableMinos);
        minos.sort(SEPARABLE_MINO_COMPARATOR);
        this.separableMinos = minos;

        HashMap<Key, Integer> indexes = new HashMap<>();
        for (int index = 0; index < minos.size(); index++) {
            SeparableMino mino = minos.get(index);
            Key key = Key.create(mino);
            indexes.put(key, index);
        }
        this.indexes = indexes;
    }

    public List<SeparableMino> getMinos() {
        return separableMinos;
    }

    private int toIndex(Key key) {
        Integer index = indexes.getOrDefault(key, -1);
        assert 0 <= index;
        return index;
    }

    public int toIndex(SeparableMino separableMino) {
        Key key = Key.create(separableMino);
        return toIndex(key);
    }

    public int toIndex(Mino mino, int x, int y, long deleteKey) {
        Key key = new Key(mino, x, y, deleteKey);
        return toIndex(key);
    }

    private static class Key {
        private static Key create(SeparableMino separableMino) {
            Mino mino = separableMino.getMino();
            return createFromLowerY(mino, separableMino.getX(), separableMino.getLowerY(), separableMino.getDeleteKey());
        }

        private static Key createFromLowerY(Mino mino, int x, int lowerY, long deleteKey) {
            return new Key(mino, x, lowerY - mino.getMinY(), deleteKey);
        }

        private final Block block;
        private final Rotate rotate;
        private final int x;
        private final int y;
        private final long deleteKey;

        private Key(Mino mino, int x, int y, long deleteKey) {
            this.block = mino.getBlock();
            this.rotate = mino.getRotate();
            this.x = x;
            this.y = y;
            this.deleteKey = deleteKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (x != key.x) return false;
            if (y != key.y) return false;
            if (deleteKey != key.deleteKey) return false;
            if (block != key.block) return false;
            return rotate == key.rotate;
        }

        @Override
        public int hashCode() {
            int result = y;
            result = 10 * result + x;
            result = 7 * result + block.getNumber();
            result = 4 * result + rotate.getNumber();
            result = 31 * result + (int) (deleteKey ^ (deleteKey >>> 32));
            return result;
        }

        // TODO: Add Comparable: compareTo
    }
}
