package _experimental.allcomb;

import _experimental.newfield.step4.MinoMask;
import _experimental.newfield.step4.MinoMaskFactory;
import core.field.Field;
import core.mino.Mino;

public class DeleteKey {
    private final ColumnSmallField field;
    private final int lowerY;
    private final long deleteKey;

    public static DeleteKey create(Mino mino, long deleteKey, int lowerY, int upperY, int fieldHeight) {
        assert 0 <= lowerY && upperY <= 10;

        MinoMask minoMask = MinoMaskFactory.create(upperY, mino, lowerY - mino.getMinY(), deleteKey);
        Field mask = minoMask.getMinoMask(-mino.getMinX());

        ColumnSmallField field = new ColumnSmallField();
        for (int y = lowerY; y < upperY; y++) {
            for (int x = 0; x < 4; x++) {
                if (!mask.isEmpty(x, y))
                    field.setBlock(x, y, fieldHeight);
            }
        }

        return new DeleteKey(field, lowerY, deleteKey);
    }

    private DeleteKey(ColumnSmallField field, int lowerY, long deleteKey) {
        this.field = field;
        this.lowerY = lowerY;
        this.deleteKey = deleteKey;
    }
}
