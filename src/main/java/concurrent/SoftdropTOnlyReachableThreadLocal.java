package concurrent;

import core.action.reachable.SoftdropTOnlyReachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

import java.util.function.Supplier;

public class SoftdropTOnlyReachableThreadLocal extends ThreadLocal<SoftdropTOnlyReachable> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final int maxY;

    public SoftdropTOnlyReachableThreadLocal(Supplier<MinoRotation> minoRotationSupplier, int maxY) {
        this(new MinoFactory(), new MinoShifter(), minoRotationSupplier.get(), maxY);
    }

    public SoftdropTOnlyReachableThreadLocal(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxY = maxY;
    }

    @Override
    protected SoftdropTOnlyReachable initialValue() {
        return new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, maxY);
    }
}
