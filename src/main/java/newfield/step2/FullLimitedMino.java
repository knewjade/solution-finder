package newfield.step2;

import core.field.Field;
import core.mino.Mino;
import newfield.ParityField;
import newfield.step4.MinoMask;

public class FullLimitedMino {
    public static FullLimitedMino create(Mino mino, PositionLimit positionLimit, DeleteKey deleteKey) {
        return new FullLimitedMino(mino, positionLimit, deleteKey);
    }

    public static final int FIELD_WIDTH = 10;

    private final Mino mino;
    private final PositionLimit positionLimit;
    private final DeleteKey deleteKey;
    private final int[] xs;
    private final int[] parity;

    private FullLimitedMino(Mino mino, PositionLimit positionLimit, DeleteKey deleteKey) {
        this.mino = mino;
        this.positionLimit = positionLimit;
        this.deleteKey = deleteKey;
        this.xs = createX(mino, positionLimit);
        this.parity = createParity(deleteKey, positionLimit);
    }

    private int[] createX(Mino mino, PositionLimit positionLimit) {
        int minX = -mino.getMinX();
        int maxX = FIELD_WIDTH - mino.getMaxX();
        switch (positionLimit) {
            case OddX:
                return createOdd(minX, maxX);
            case EvenX:
                return createEven(minX, maxX);
        }
        throw new IllegalStateException("No reachable");
    }

    private int[] createParity(DeleteKey deleteKey, PositionLimit positionLimit) {
        switch (positionLimit) {
            case OddX:
                Field oddField = deleteKey.getMinoMask().getMinoMask(6);
                ParityField oddParityField = new ParityField(oddField);
                return new int[]{oddParityField.calculateOddParity(), oddParityField.calculateEvenParity()};
            case EvenX:
                Field evenField = deleteKey.getMinoMask().getMinoMask(5);
                ParityField evenParityField = new ParityField(evenField);
                return new int[]{evenParityField.calculateOddParity(), evenParityField.calculateEvenParity()};
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

    public int[] getParity() {
        return parity;
    }

    @Override
    public String toString() {
        return "FullLimitedMino{" +
                "mino=" + mino.getBlock() + "-" + mino.getRotate() + ":" + deleteKey +
                ",pos=" + positionLimit +
                '}';
    }

    PositionLimit getPositionLimit() {
        return positionLimit;
    }
}
