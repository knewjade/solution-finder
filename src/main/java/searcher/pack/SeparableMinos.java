package searcher.pack;

import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;
import searcher.pack.separable_mino.SeparableMino;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SeparableMinos {
    public static final Comparator<SeparableMino> SEPARABLE_MINO_COMPARATOR = (o1, o2) -> {
        Mino mino1 = o1.getMino();
        Mino mino2 = o2.getMino();

        int blockCompare = mino1.getBlock().compareTo(mino2.getBlock());
        if (blockCompare != 0)
            return blockCompare;

        int rotateCompare = mino1.getRotate().compareTo(mino2.getRotate());
        if (rotateCompare != 0)
            return rotateCompare;

        int xCompare = Integer.compare(o1.getX(), o2.getX());
        if (xCompare != 0)
            return xCompare;

        int yCompare = Integer.compare(o1.getLowerY(), o2.getLowerY());
        if (yCompare != 0)
            return yCompare;

        return Long.compare(o1.getDeleteKey(), o2.getDeleteKey());
    };

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
            Key key = new Key(mino);
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
        Key key = new Key(separableMino);
        return toIndex(key);
    }

    private static class Key {
        private final Block block;
        private final Rotate rotate;
        private final int x;
        private final int y;
        private final long deleteKey;

        private Key(SeparableMino separableMino) {
            Mino mino = separableMino.getMino();
            this.block = mino.getBlock();
            this.rotate = mino.getRotate();
            this.x = separableMino.getX();
            this.y = separableMino.getLowerY() - mino.getMinY();
            this.deleteKey = separableMino.getDeleteKey();
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
    }
}
