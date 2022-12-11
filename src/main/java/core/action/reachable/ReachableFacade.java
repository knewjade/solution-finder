package core.action.reachable;

import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class ReachableFacade {
    public static ILockedReachable create90Locked(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY
    ) {
        return createLocked(minoFactory, minoShifter, minoRotation, maxY, false);
    }

    public static ILockedReachable create180Locked(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY
    ) {
        return createLocked(minoFactory, minoShifter, minoRotation, maxY, true);
    }

    public static ILockedReachable createLocked(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY, boolean use180Rotation
    ) {
        if (use180Rotation) {
            return new Locked180Reachable(minoFactory, minoShifter, minoRotation, maxY);
        } else {
            return new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);
        }
    }
}
