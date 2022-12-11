package core.action.candidate;

import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class CandidateFacade {
    public static ILockedCandidate create90Locked(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY
    ) {
        return createLocked(minoFactory, minoShifter, minoRotation, maxY, false);
    }

    public static ILockedCandidate create180Locked(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY
    ) {
        return createLocked(minoFactory, minoShifter, minoRotation, maxY, true);
    }

    public static ILockedCandidate createLocked(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY, boolean use180Rotation
    ) {
        if (use180Rotation) {
            return new Locked180Candidate(minoFactory, minoShifter, minoRotation, maxY);
        } else {
            return new LockedCandidate(minoFactory, minoShifter, minoRotation, maxY);
        }
    }
}
