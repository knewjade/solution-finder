package searcher.pack.separable_mino;

import common.datastore.FullOperationWithKey;
import common.datastore.MinoOperationWithKey;
import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.mino.Mino;
import searcher.pack.separable_mino.mask.MinoMask;
import searcher.pack.separable_mino.mask.MinoMaskFactory;

public class FullOperationSeparableMino implements SeparableMino {
    public static SeparableMino create(FullOperationWithKey operationWithKey, int upperY, int fieldHeight) {
        assert upperY <= 10 : upperY;

        Mino mino = operationWithKey.getMino();
        long deleteKey = operationWithKey.getNeedDeletedKey();
        int y = operationWithKey.getY();
        MinoMask minoMask = MinoMaskFactory.create(fieldHeight, mino, y, deleteKey);

        int x = operationWithKey.getX();
        Field mask = minoMask.getMinoMask(x);

        int lowerY = operationWithKey.getY() + operationWithKey.getMino().getMinY();
        ColumnSmallField field = ColumnFieldFactory.createField();
        for (int ny = lowerY; ny <= upperY; ny++) {
            for (int nx = x + mino.getMinX(); nx <= x + mino.getMaxX(); nx++) {
                if (!mask.isEmpty(nx, ny))
                    field.setBlock(nx, ny, fieldHeight);
            }
        }

        return new FullOperationSeparableMino(operationWithKey, field);
    }

    private final FullOperationWithKey operation;
    private final ColumnField field;
    private final int lowerY;

    private FullOperationSeparableMino(FullOperationWithKey operationWithKey, ColumnField field) {
        this.operation = operationWithKey;
        this.field = field;
        this.lowerY = operationWithKey.getY() + operationWithKey.getMino().getMinY();
        assert 0 <= lowerY : lowerY;
    }

    @Override
    public int getLowerY() {
        return lowerY;
    }

    @Override
    public ColumnField getField() {
        return field;
    }

    @Override
    public MinoOperationWithKey toMinoOperationWithKey() {
        return operation;
    }
}
