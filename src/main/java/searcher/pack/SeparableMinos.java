package searcher.pack;

import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.piece.Piece;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.AllSeparableMinoFactory;

import java.util.*;

public class SeparableMinos {
    private static final Comparator<SeparableMino> SEPARABLE_MINO_COMPARATOR = new SeparableMinoComparator();

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
    private final HashMap<Piece, Integer> indexes;

    public SeparableMinos(Set<SeparableMino> separableMinos) {
        this(new ArrayList<>(separableMinos));
    }

    public SeparableMinos(List<SeparableMino> separableMinos) {
        // 順番を一意に固定する
        ArrayList<SeparableMino> minos = new ArrayList<>(separableMinos);
        minos.sort(SEPARABLE_MINO_COMPARATOR);
        this.separableMinos = minos;

        HashMap<Piece, Integer> indexes = new HashMap<>();
        for (int index = 0; index < minos.size(); index++) {
            SeparableMino mino = minos.get(index);
            Mino mino1 = mino.getMino();
            Piece key = new Piece(mino1, mino.getX(), mino.getLowerY() - mino1.getMinY(), mino.getDeleteKey());
            indexes.put(key, index);
        }
        this.indexes = indexes;
    }

    public List<SeparableMino> getMinos() {
        return separableMinos;
    }

    private int toIndex(Piece key) {
        Integer index = indexes.getOrDefault(key, -1);
        assert 0 <= index;
        return index;
    }

    public int toIndex(SeparableMino separableMino) {
        Mino mino = separableMino.getMino();
        Piece piece = new Piece(mino, separableMino.getX(), separableMino.getLowerY() - mino.getMinY(), separableMino.getDeleteKey());
        return toIndex(piece);
    }

    public int toIndex(Mino mino, int x, int y, long deleteKey) {
        Piece piece = new Piece(mino, x, y, deleteKey);
        return toIndex(piece);
    }
}
