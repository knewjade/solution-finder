package common.datastore;

import common.datastore.action.Action;
import core.mino.Piece;
import core.srs.Rotate;

public interface Operation extends Action {
    static long toUniqueKey(Operation operation) {
        return toUniqueKey(
                operation.getPiece(), operation.getRotate(),
                operation.getX(), operation.getY()
        );
    }

    static long toUniqueKey(Piece piece, Rotate rotate, int x, int y) {
        return piece.getNumber() * 4 * 24 * 10
                + rotate.getNumber() * 24 * 10
                + y * 10
                + x;
    }

    static int defaultHashCode(Piece piece, Rotate rotate, int x, int y) {
        int result = y;
        result = 10 * result + x;
        result = 7 * result + piece.getNumber();
        result = 4 * result + rotate.getNumber();
        return result;
    }

    Piece getPiece();

    default long toUniqueKey() {
        return Operation.toUniqueKey(this);
    }
}
