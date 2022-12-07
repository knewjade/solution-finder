package concurrent;

import core.action.reachable.LockedReachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

import java.util.function.Supplier;

public class LockedReachableThreadLocal extends ThreadLocal<LockedReachable> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final int maxY;

    public LockedReachableThreadLocal(MinoRotation minoRotation, int maxY) {
        this(new MinoFactory(), new MinoShifter(), minoRotation, maxY);
    }

    public LockedReachableThreadLocal(Supplier<MinoRotation> minoRotationSupplier, int maxY) {
        this(minoRotationSupplier.get(), maxY);
    }

    public LockedReachableThreadLocal(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxY = maxY;
    }

    @Override
    protected LockedReachable initialValue() {
        return new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);
    }
}
