package core.neighbor;

import common.datastore.FullOperationWithKey;
import common.datastore.MinoOperationWithKey;
import common.parser.OperationTransform;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.Piece;
import core.srs.Rotate;

public class OriginalPiece implements MinoOperationWithKey {
    static final OriginalPiece EMPTY_COLLIDER_PIECE = new OriginalPiece();

    private final FullOperationWithKey operationWithKey;
    private final Field harddropCollider;
    private final Field minoField;

    public OriginalPiece(Mino mino, int x, int y, int fieldHeight) {
        this.operationWithKey = OperationTransform.toFullOperationWithKey(mino, x, y, 0L);
        this.minoField = createMinoField(mino, x, y);
        this.harddropCollider = createHarddropCollider(mino, x, y, fieldHeight);
    }

    private OriginalPiece() {
        this.operationWithKey = new FullOperationWithKey(new Mino(Piece.I, Rotate.Spawn), -1, -1, 0L, 0L);
        this.minoField = FieldFactory.createField(1);
        this.harddropCollider = FieldFactory.createField(1);
    }

    private Field createMinoField(Mino mino, int x, int y) {
        Field field = FieldFactory.createField(y + mino.getMaxY() + 1);
        field.put(mino, x, y);
        return field;
    }

    private Field createHarddropCollider(Mino mino, int x, int y, int fieldHeight) {
        Field field = FieldFactory.createField(fieldHeight);
        for (int yIndex = y; yIndex < fieldHeight - mino.getMinY(); yIndex++)
            field.put(mino, x, yIndex);
        for (int yIndex = fieldHeight; yIndex < field.getMaxFieldHeight(); yIndex++)
            for (int xIndex = 0; xIndex < 10; xIndex++)
                field.removeBlock(xIndex, yIndex);
        return field;
    }

    @Override
    public Mino getMino() {
        return operationWithKey.getMino();
    }

    @Override
    public long getNeedDeletedKey() {
        return operationWithKey.getNeedDeletedKey();
    }

    @Override
    public long getUsingKey() {
        return operationWithKey.getUsingKey();
    }

    @Override
    public Piece getPiece() {
        return operationWithKey.getPiece();
    }

    @Override
    public Rotate getRotate() {
        return operationWithKey.getRotate();
    }

    @Override
    public int getX() {
        return operationWithKey.getX();
    }

    @Override
    public int getY() {
        return operationWithKey.getY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OriginalPiece piece = (OriginalPiece) o;
        return this.operationWithKey.equals(piece.operationWithKey);
    }

    @Override
    public int hashCode() {
        return operationWithKey.hashCode();
    }

    @Override
    public String toString() {
        return "OriginalPiece{" +
                operationWithKey.toString() +
                '}';
    }

    public Field getMinoField() {
        return minoField;
    }

    public Field getHarddropCollider() {
        return harddropCollider;
    }
}
