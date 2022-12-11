package entry.path;

import common.buildup.BuildUpStream;
import core.action.reachable.ILockedReachable;
import core.action.reachable.ReachableFacade;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

import java.util.function.Supplier;

public class LockedBuildUpListUpThreadLocal extends ThreadLocal<BuildUpStream> {
    private final Supplier<MinoRotation> minoRotationSupplier;
    private final int height;
    private final boolean use180Rotation;

    public LockedBuildUpListUpThreadLocal(Supplier<MinoRotation> minoRotationSupplier, int height, boolean use180Rotation) {
        this.minoRotationSupplier = minoRotationSupplier;
        this.height = height;
        this.use180Rotation = use180Rotation;
    }

    @Override
    protected BuildUpStream initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = minoRotationSupplier.get();
        ILockedReachable reachable = ReachableFacade.createLocked(minoFactory, minoShifter, minoRotation, height, use180Rotation);
        return new BuildUpStream(reachable, height);
    }
}