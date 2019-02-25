package common.datastore;

import core.mino.Mino;
import core.mino.Piece;
import core.srs.Rotate;

public class SimpleMinoOperation implements MinoOperation {
    private final Mino mino;
    private final int x;
    private final int y;

    public SimpleMinoOperation(Mino mino, int x, int y) {
        assert mino != null;
        this.mino = mino;
        this.x = x;
        this.y = y;
    }

    @Override
    public Piece getPiece() {
        return mino.getPiece();
    }

    @Override
    public Rotate getRotate() {
        return mino.getRotate();
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public Mino getMino() {
        return mino;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleMinoOperation operation = (SimpleMinoOperation) o;
        return x == operation.x && y == operation.y && mino.equals(operation.mino);
    }

    @Override
    public int hashCode() {
        return Operation.defaultHashCode(getPiece(), getRotate(), getX(), getY());
    }

    @Override
    public String toString() {
        return String.format("[%s %d,%d]", mino, x, y);
    }
}
