package _implements.parity_based_pack.step2;

import core.field.Field;
import core.mino.Mino;
import searcher.pack.separable_mino.mask.MinoMask;
import searcher.pack.separable_mino.mask.MinoMaskFactory;

public class DeleteKey {
    public static DeleteKey create(Mino mino, long deleteKey, long usingKey, int lowerY, int upperY) {
        int y = lowerY - mino.getMinY();
        int maxHeight = upperY + 1;
        MinoMask minoMask = MinoMaskFactory.create(maxHeight, mino, y, deleteKey);

        int minoHeight = mino.getMaxY() - mino.getMinY() + 1;
        int[][] blockCountEachLines = new int[minoHeight][2];
        int index = 0;
        for (int newY = lowerY; newY <= upperY; newY++) {
            Field mask = minoMask.getMinoMask(-mino.getMinX());
            int block = mask.getBlockCountOnY(newY);
            if (block != 0) {
                blockCountEachLines[index][0] = newY;
                blockCountEachLines[index][1] = block;
                index++;
            }
        }
        assert index == minoHeight;

        return new DeleteKey(minoMask, blockCountEachLines, lowerY, deleteKey, usingKey);
    }

    private final MinoMask minoMask;
    private final int[][] blockCountEachLines;
    private final int lowerY;
    private final long deleteKey;
    private final long usingKey;

    private DeleteKey(MinoMask minoMask, int[][] blockCountEachLines, int lowerY, long deleteKey, long usingKey) {
        assert 0 < blockCountEachLines.length && blockCountEachLines[0].length == 2;
        this.minoMask = minoMask;
        this.blockCountEachLines = blockCountEachLines;
        this.lowerY = lowerY;
        this.deleteKey = deleteKey;
        this.usingKey = usingKey;
    }

    MinoMask getMinoMask() {
        return minoMask;
    }

    int[][] getBlockCountEachLines() {
        return blockCountEachLines;
    }

    int getLowerY() {
        return lowerY;
    }

    long getNeedKey() {
        return deleteKey;
    }

    @Override
    public String toString() {
        return String.format("key{%d, %d}", lowerY, deleteKey);
    }

    public long getUsingKey() {
        return usingKey;
    }
}
