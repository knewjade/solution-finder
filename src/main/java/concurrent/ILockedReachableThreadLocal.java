package concurrent;

import core.action.reachable.ILockedReachable;
import core.action.reachable.ReachableFacade;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

import java.util.function.Supplier;

public class ILockedReachableThreadLocal extends ThreadLocal<ILockedReachable> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final int maxY;
    private final boolean use180Rotation;

    public ILockedReachableThreadLocal(MinoRotation minoRotation, int maxY, boolean use180Rotation) {
        this(new MinoFactory(), new MinoShifter(), minoRotation, maxY, use180Rotation);
    }

    public ILockedReachableThreadLocal(Supplier<MinoRotation> minoRotationSupplier, int maxY, boolean use180Rotation) {
        this(minoRotationSupplier.get(), maxY, use180Rotation);
    }

    public ILockedReachableThreadLocal(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY, boolean use180Rotation) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxY = maxY;
        this.use180Rotation = use180Rotation;
    }

    @Override
    protected ILockedReachable initialValue() {
        return ReachableFacade.createLocked(minoFactory, minoShifter, minoRotation, maxY, use180Rotation);
    }
}
