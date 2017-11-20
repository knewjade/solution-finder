package concurrent;

import core.action.reachable.HarddropReachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;

public class HarddropReachableThreadLocal extends ThreadLocal<HarddropReachable> {
    private final int maxY;

    public HarddropReachableThreadLocal(int maxY) {
        this.maxY = maxY;
    }

    @Override
    protected HarddropReachable initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        return new HarddropReachable(minoFactory, minoShifter, maxY);
    }
}