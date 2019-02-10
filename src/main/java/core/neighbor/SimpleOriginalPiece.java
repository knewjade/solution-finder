package core.neighbor;

import common.datastore.FullOperationWithKey;
import common.datastore.MinoOperationWithKey;
import core.field.Field;
import core.mino.Mino;
import core.mino.Piece;
import core.srs.Rotate;

public class SimpleOriginalPiece implements MinoOperationWithKey {
    private final FullOperationWithKey operationWithKey;
    private final Field minoField;

    public SimpleOriginalPiece(FullOperationWithKey operationWithKey, int fieldHeight) {
        this.operationWithKey = operationWithKey;
        this.minoField = operationWithKey.createMinoField(fieldHeight);
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
        SimpleOriginalPiece piece = (SimpleOriginalPiece) o;
        return this.operationWithKey.equals(piece.operationWithKey);
    }

    @Override
    public int hashCode() {
        return operationWithKey.hashCode();
    }

    @Override
    public String toString() {
        return "SimpleOriginalPiece{" +
                operationWithKey.toString() +
                '}';
    }

    public Field getMinoField() {
        return minoField;
    }
}
