package entry.path;

import common.buildup.BuildUpListUp;
import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.action.reachable.LockedReachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class LockedBuildUpListUpThreadLocal extends ThreadLocal<BuildUpListUp> {
    private final int height;

    public LockedBuildUpListUpThreadLocal(int height) {
        this.height = height;
    }

    @Override
    protected BuildUpListUp initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);
        return new BuildUpListUp(reachable, height);
    }
}