package newfield.step2;

import core.mino.Mino;
import newfield.step4.MinoMask;

public class FullLimitedMino {
    public static final int FIELD_WIDTH = 10;

    private final int[] xs;
    private final Mino mino;
    private final DeleteKey deleteKey;

    public static FullLimitedMino create(Mino mino, PositionLimit positionLimit, DeleteKey deleteKey) {
        return new FullLimitedMino(mino, positionLimit, deleteKey);
    }

    private FullLimitedMino(Mino mino, PositionLimit positionLimit, DeleteKey deleteKey) {
        this.mino = mino;
        this.deleteKey = deleteKey;
        this.xs = createX(mino, positionLimit);
    }

    private int[] createX(Mino mino, PositionLimit positionLimit) {
        int minX = -mino.getMinX();
        int maxX = FIELD_WIDTH - mino.getMaxX();
        switch (positionLimit) {
            case OddX:
                return createOdd(minX, maxX);
            case EvenX:
                return createEven(minX, maxX);
            case NoLimit:
                return createAll(minX, maxX);
        }
        throw new IllegalStateException("No reachable");
    }

    private int[] createOdd(int minX, int maxX) {
        minX = minX % 2 == 0 ? minX : minX + 1;
        int[] ints = new int[(maxX - minX + 1) / 2];
        for (int index = 0; index < ints.length; index++)
            ints[index] = minX + index * 2;
        return ints;
    }

    private int[] createEven(int minX, int maxX) {
        minX = minX % 2 == 1 ? minX : minX + 1;
        int[] ints = new int[(maxX - minX + 1) / 2];
        for (int index = 0; index < ints.length; index++)
            ints[index] = minX + index * 2;
        return ints;
    }

    private int[] createAll(int minX, int maxX) {
        int[] ints = new int[maxX - minX];
        for (int index = 0; index < ints.length; index++)
            ints[index] = minX + index;
        return ints;
    }

    public Mino getMino() {
        return mino;
    }

    public int[] getXs() {
        return xs;
    }

    public MinoMask getMinoMask() {
        return deleteKey.getMinoMask();
    }

    public int[][] getBlockCountEachLines() {
        return deleteKey.getBlockCountEachLines();
    }

    public int getLowerY() {
        return deleteKey.getLowerY();
    }

    public long getDeleteKey() {
        return deleteKey.getNeedKey();
    }

    @Override
    public String toString() {
        return "FullLimitedMino{" +
                "mino=" + mino.getBlock() +
                "," + mino.getRotate() +
                '}';
    }
}
