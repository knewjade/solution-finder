package searcher.pack.separable_mino;

import common.datastore.FullOperationWithKey;
import common.datastore.MinoOperationWithKey;
import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.FieldFactory;
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
        ColumnSmallField columnField = ColumnFieldFactory.createField();
        for (int ny = lowerY; ny <= upperY; ny++) {
            for (int nx = x + mino.getMinX(); nx <= x + mino.getMaxX(); nx++) {
                if (!mask.isEmpty(nx, ny))
                    columnField.setBlock(nx, ny, fieldHeight);
            }
        }

        Field field = FieldFactory.createField(fieldHeight);
        field.put(operationWithKey.getMino(), operationWithKey.getX(), operationWithKey.getY());
        field.insertWhiteLineWithKey(operationWithKey.getNeedDeletedKey());
        return new FullOperationSeparableMino(operationWithKey, columnField, field);
    }

    private final FullOperationWithKey operation;
    private final ColumnField columnField;
    private final Field field;
    private final int lowerY;

    private FullOperationSeparableMino(FullOperationWithKey operationWithKey, ColumnField columnField, Field field) {
        this.operation = operationWithKey;
        this.columnField = columnField;
        this.field = field;
        this.lowerY = operationWithKey.getY() + operationWithKey.getMino().getMinY();
        assert 0 <= lowerY : lowerY;
    }

    @Override
    public int getLowerY() {
        return lowerY;
    }

    @Override
    public ColumnField getColumnField() {
        return columnField;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public MinoOperationWithKey toMinoOperationWithKey() {
        return operation;
    }
}
