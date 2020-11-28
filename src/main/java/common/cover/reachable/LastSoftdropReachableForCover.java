package common.cover.reachable;

import core.action.reachable.LockedReachable;
import core.action.reachable.Reachable;
import core.field.Field;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class LastSoftdropReachableForCover implements ReachableForCover {
    private final Reachable reachable;
    private final LockedReachable lockedReachable;
    private final int allowDepth;

    public LastSoftdropReachableForCover(Reachable reachable, int maxY, int allowDepth) {
        assert 1 <= allowDepth;
        this.reachable = reachable;
        this.lockedReachable = new LockedReachable(new MinoFactory(), new MinoShifter(), MinoRotation.create(), maxY);
        this.allowDepth = allowDepth;
    }

    @Override
    public boolean checks(Field field, Mino mino, int x, int y, int validHeight, int remainingDepth) {
        if (remainingDepth <= allowDepth) {
            return lockedReachable.checks(field, mino, x, y, validHeight);
        }
        return reachable.checks(field, mino, x, y, validHeight);
    }
}
