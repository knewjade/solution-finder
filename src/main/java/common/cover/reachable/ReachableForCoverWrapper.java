package common.cover.reachable;

import core.action.reachable.Reachable;
import core.field.Field;
import core.mino.Mino;

public class ReachableForCoverWrapper implements ReachableForCover {
    private final Reachable reachable;

    public ReachableForCoverWrapper(Reachable reachable) {
        this.reachable = reachable;
    }

    @Override
    public boolean checks(Field field, Mino mino, int x, int y, int validHeight, int remainingDepth) {
        return reachable.checks(field, mino, x, y, validHeight);
    }
}
