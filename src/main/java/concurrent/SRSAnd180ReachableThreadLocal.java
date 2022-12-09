package concurrent;

import core.action.reachable.SRSAnd180Reachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

import java.util.function.Supplier;

public class SRSAnd180ReachableThreadLocal extends ThreadLocal<SRSAnd180Reachable> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final int maxY;

    public SRSAnd180ReachableThreadLocal(Supplier<MinoRotation> minoRotationSupplier, int maxY) {
        this(new MinoFactory(), new MinoShifter(), minoRotationSupplier.get(), maxY);
    }

    public SRSAnd180ReachableThreadLocal(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY) {
        if (minoRotation.noSupports180()) {
            throw new IllegalArgumentException("kicks do not support 180");
        }

        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxY = maxY;
    }

    @Override
    protected SRSAnd180Reachable initialValue() {
        return new SRSAnd180Reachable(minoFactory, minoShifter, minoRotation, maxY);
    }
}
