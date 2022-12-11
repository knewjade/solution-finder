package common.cover.reachable;

import core.action.reachable.ILockedReachable;
import core.action.reachable.Reachable;
import core.action.reachable.ReachableFacade;
import core.field.Field;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class LastSoftdropReachableForCover implements ReachableForCover {
    private final Reachable reachable;
    private final ILockedReachable lockedReachable;
    private final int allowDepth;

    public LastSoftdropReachableForCover(Reachable reachable, MinoRotation minoRotation, int maxY, int allowDepth, boolean use180Rotation) {
        assert 1 <= allowDepth;
        this.reachable = reachable;
        this.lockedReachable = ReachableFacade.createLocked(new MinoFactory(), new MinoShifter(), minoRotation, maxY, use180Rotation);
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
