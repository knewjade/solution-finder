package common.datastore;

import common.datastore.action.Action;
import core.mino.Piece;

public interface Operation extends Action {
    Piece getPiece();
}
