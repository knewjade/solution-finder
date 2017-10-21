package entry.path;

import common.buildup.BuildUpStream;
import core.action.reachable.HarddropReachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;

public class HarddropBuildUpListUpThreadLocal extends ThreadLocal<BuildUpStream> {
    private final int height;

    public HarddropBuildUpListUpThreadLocal(int height) {
        this.height = height;
    }

    @Override
    protected BuildUpStream initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        HarddropReachable reachable = new HarddropReachable(minoFactory, minoShifter, height);
        return new BuildUpStream(reachable, height);
    }
}