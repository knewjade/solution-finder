package core.mino;

import core.srs.Rotate;
import common.datastore.action.Action;
import common.datastore.action.MinimalAction;

import java.util.Collections;
import java.util.List;

public class PassedMinoShifter extends MinoShifter {
    public Action createTransformedAction(Piece piece, Action action) {
        return action;
    }

    public Action createTransformedAction(Piece piece, Rotate rotate, int x, int y) {
        return MinimalAction.create(x, y, rotate);
    }

    public List<Action> enumerateSameOtherActions(Piece piece, Rotate rotate, int x, int y) {
        return Collections.emptyList();
    }

    public Rotate createTransformedRotate(Piece piece, Rotate rotate) {
        return rotate;
    }
}
