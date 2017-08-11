package core.mino;

import core.srs.Rotate;
import common.datastore.action.Action;
import common.datastore.action.MinimalAction;

import java.util.Collections;
import java.util.List;

public class PassedMinoShifter extends MinoShifter {
    public Action createTransformedAction(Block block, Action action) {
        return action;
    }

    public Action createTransformedAction(Block block, Rotate rotate, int x, int y) {
        return MinimalAction.create(x, y, rotate);
    }

    public List<Action> enumerateSameOtherActions(Block block, Rotate rotate, int x, int y) {
        return Collections.emptyList();
    }

    public Rotate createTransformedRotate(Block block, Rotate rotate) {
        return rotate;
    }
}
