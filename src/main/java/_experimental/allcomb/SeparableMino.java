package _experimental.allcomb;

import _experimental.newfield.step4.MinoMask;
import _experimental.newfield.step4.MinoMaskFactory;
import core.field.Field;
import core.mino.Mino;

public class SeparableMino {
    public static SeparableMino create(Mino mino, long deleteKey, int x, int lowerY, int upperY, int fieldHeight) {
        assert 0 <= lowerY && upperY <= 10 : lowerY;

        MinoMask minoMask = MinoMaskFactory.create(fieldHeight, mino, lowerY - mino.getMinY(), deleteKey);
        Field mask = minoMask.getMinoMask(x);

        ColumnSmallField field = new ColumnSmallField();
        for (int ny = lowerY; ny <= upperY; ny++) {
            for (int nx = 0; nx < 7; nx++) {
                if (!mask.isEmpty(nx, ny))
                    field.setBlock(nx, ny, fieldHeight);
            }
        }

        return new SeparableMino(mino, field, x, lowerY, deleteKey);
    }

    private final Mino mino;
    private final ColumnField field;
    private final int x;
    private final int lowerY;
    private final long deleteKey;

    public SeparableMino(Mino mino, ColumnField field, int x, int lowerY, long deleteKey) {
        this.mino = mino;
        this.field = field;
        this.x = x;
        this.lowerY = lowerY;
        this.deleteKey = deleteKey;
    }

    public Mino getMino() {
        return mino;
    }

    public int getLowerY() {
        return lowerY;
    }

    public long getDeleteKey() {
        return deleteKey;
    }

    public ColumnField getField() {
        return field;
    }

    public int getX() {
        return x;
    }
}
