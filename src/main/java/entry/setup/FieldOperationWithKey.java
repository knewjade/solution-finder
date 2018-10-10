package entry.setup;

import common.datastore.FullOperationWithKey;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Piece;

class FieldOperationWithKey {
    private final FullOperationWithKey operation;
    private final Field field;

    FieldOperationWithKey(FullOperationWithKey operation) {
        int maxY = operation.getMino().getMaxY();
        Field field = FieldFactory.createField(maxY);
        field.put(operation.getMino(), operation.getX(), operation.getY());
        field.insertWhiteLineWithKey(operation.getNeedDeletedKey());

        {
            this.operation = operation;
            this.field = field;
        }
    }

    public FullOperationWithKey getOperation() {
        return operation;
    }

    public Field getField() {
        return field;
    }

    public Piece getPiece() {
        return operation.getPiece();
    }
}
