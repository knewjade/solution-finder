package common.datastore;

import core.mino.Piece;
import core.srs.Rotate;

public interface OperationWithKey extends Operation {
    static long toUniqueKey(OperationWithKey operation) {
        return toUniqueKey(
                operation.getPiece(), operation.getRotate(), operation.getX(), operation.getY(), operation.getNeedDeletedKey()
        );
    }

    public static long toUniqueKey(Piece piece, Rotate rotate, int x, int y, long needDeletedKey) {
        long maskLow = 0b111111111111111111111111111111L;
        long maskHigh = 0b111111111111111111111111111111000000000000000000000000000000L;
        long uniqueDeletedKey = (needDeletedKey & maskHigh) | (needDeletedKey & maskLow) << 35;

        return uniqueDeletedKey + Operation.toUniqueKey(piece, rotate, x, y);
    }

    long getUsingKey();

    long getNeedDeletedKey();

    default long toUniqueKey() {
        return OperationWithKey.toUniqueKey(this);
    }
}
