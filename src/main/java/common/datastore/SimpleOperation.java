package common.datastore;

import core.mino.Piece;
import core.srs.Rotate;

public class SimpleOperation implements Operation {
    private final Piece piece;
    private final Rotate rotate;
    private final int x;
    private final int y;

    public SimpleOperation(Piece piece, Rotate rotate, int x, int y) {
        assert piece != null && rotate != null;
        this.piece = piece;
        this.rotate = rotate;
        this.x = x;
        this.y = y;
    }

    @Override
    public Piece getPiece() {
        return piece;
    }

    @Override
    public Rotate getRotate() {
        return rotate;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleOperation operation = (SimpleOperation) o;
        return x == operation.x && y == operation.y && piece == operation.piece && rotate == operation.rotate;
    }

    @Override
    public int hashCode() {
        int result = y;
        result = 10 * result + x;
        result = 7 * result + piece.getNumber();
        result = 4 * result + rotate.getNumber();
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%s-%s %d,%d]", piece, rotate, x, y);
    }
}
