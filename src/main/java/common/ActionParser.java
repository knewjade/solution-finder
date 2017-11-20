package common;

import common.datastore.Operation;
import common.datastore.SimpleOperation;
import common.datastore.action.Action;
import core.mino.Piece;
import core.srs.Rotate;

public class ActionParser {
    static int parseToInt(Piece piece, Action action) {
        return parseToInt(piece, action.getRotate(), action.getX(), action.getY());
    }

    public static int parseToInt(Piece piece, Rotate rotate, int x, int y) {
        return x + y * 10 + rotate.getNumber() * 240 + piece.getNumber() * 240 * 4;
    }

    static Operation parseToOperation(int value) {
        int x = value % 10;
        value /= 10;
        int y = value % 24;
        value /= 24;
        Rotate rotate = Rotate.getRotate(value % 4);
        value /= 4;
        Piece piece = Piece.getBlock(value);
        return new SimpleOperation(piece, rotate, x, y);
    }
}
