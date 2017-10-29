package common.datastore;

import core.mino.Piece;
import core.srs.Rotate;

public interface Operation {
    Piece getPiece();

    Rotate getRotate();

    int getX();

    int getY();
}
