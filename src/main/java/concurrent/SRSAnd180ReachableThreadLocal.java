package concurrent;

import core.action.reachable.SRSAnd180Reachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class SRSAnd180ReachableThreadLocal extends ThreadLocal<SRSAnd180Reachable> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final int maxY;

    public SRSAnd180ReachableThreadLocal(int maxY) {
        this(new MinoFactory(), new MinoShifter(), MinoRotation.create(), maxY);
    }

    public SRSAnd180ReachableThreadLocal(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY) {
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
