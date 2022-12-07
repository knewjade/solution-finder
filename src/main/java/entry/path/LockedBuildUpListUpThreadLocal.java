package entry.path;

import common.buildup.BuildUpStream;
import core.action.reachable.LockedReachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

import java.util.function.Supplier;

public class LockedBuildUpListUpThreadLocal extends ThreadLocal<BuildUpStream> {
    private final Supplier<MinoRotation> minoRotationSupplier;
    private final int height;

    public LockedBuildUpListUpThreadLocal(Supplier<MinoRotation> minoRotationSupplier, int height) {
        this.minoRotationSupplier = minoRotationSupplier;
        this.height = height;
    }

    @Override
    protected BuildUpStream initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = minoRotationSupplier.get();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);
        return new BuildUpStream(reachable, height);
    }
}