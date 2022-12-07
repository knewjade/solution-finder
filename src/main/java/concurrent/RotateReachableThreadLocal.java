package concurrent;

import core.action.reachable.RotateReachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

import java.util.function.Supplier;

public class RotateReachableThreadLocal extends ThreadLocal<RotateReachable> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final int maxY;

    public RotateReachableThreadLocal(Supplier<MinoRotation> minoRotationSupplier, int maxY) {
        this(new MinoFactory(), new MinoShifter(), minoRotationSupplier.get(), maxY);
    }

    public RotateReachableThreadLocal(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxY = maxY;
    }

    @Override
    protected RotateReachable initialValue() {
        return new RotateReachable(minoFactory, minoShifter, minoRotation, maxY);
    }
}
