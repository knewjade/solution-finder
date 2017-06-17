package entry.path;

import common.buildup.BuildUpStream;
import core.action.reachable.LockedReachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class LockedBuildUpListUpThreadLocal extends ThreadLocal<BuildUpStream> {
    private final int height;

    public LockedBuildUpListUpThreadLocal(int height) {
        this.height = height;
    }

    @Override
    protected BuildUpStream initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);
        return new BuildUpStream(reachable, height);
    }
}