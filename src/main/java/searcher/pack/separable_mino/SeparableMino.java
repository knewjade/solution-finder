package searcher.pack.separable_mino;

import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.mino.Mino;
import searcher.pack.separable_mino.mask.MinoMask;
import searcher.pack.separable_mino.mask.MinoMaskFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SeparableMino {
    public static SeparableMino create(MinoOperationWithKey operationWithKey, int upperY, int fieldHeight) {
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

        return new SeparableMino(operationWithKey, field);
    }

    private final MinoOperationWithKey operation;
    private final ColumnField field;
    private final int lowerY;

    private SeparableMino(MinoOperationWithKey operationWithKey, ColumnField field) {
        this.operation = operationWithKey;
        this.field = field;
        this.lowerY = operationWithKey.getY() + operationWithKey.getMino().getMinY();
        assert 0 <= lowerY : lowerY;
    }

    public Mino getMino() {
        return operation.getMino();
    }

    public int getLowerY() {
        return lowerY;
    }

    public long getDeleteKey() {
        return operation.getNeedDeletedKey();
    }

    public ColumnField getField() {
        return field;
    }

    public int getX() {
        return operation.getX();
    }

    public long getUsingKey() {
        return operation.getUsingKey();
    }

    public OperationWithKey toOperation() {
        return operation;
    }

    public MinoOperationWithKey toMinoOperationWithKey() {
        throw new NotImplementedException();  // FIXME
    }
}
