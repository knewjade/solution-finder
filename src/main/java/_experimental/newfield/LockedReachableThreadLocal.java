package _experimental.newfield;

import core.action.reachable.LockedReachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class LockedReachableThreadLocal extends ThreadLocal<LockedReachable> {
    private final int maxY;

    public LockedReachableThreadLocal(int maxY) {
        this.maxY = maxY;
    }

    @Override
    protected LockedReachable initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        return new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);
    }
}
