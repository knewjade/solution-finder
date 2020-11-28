package concurrent;

import core.action.reachable.TSpinOrHarddropReachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class TSpinOrHarddropReachableThreadLocal extends ThreadLocal<TSpinOrHarddropReachable> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final int maxY;
    private final int required;

    public TSpinOrHarddropReachableThreadLocal(int maxY, int required) {
        this(new MinoFactory(), new MinoShifter(), MinoRotation.create(), maxY, required);
    }

    public TSpinOrHarddropReachableThreadLocal(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY, int required
    ) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxY = maxY;
        this.required = required;
    }

    @Override
    protected TSpinOrHarddropReachable initialValue() {
        return new TSpinOrHarddropReachable(minoFactory, minoShifter, minoRotation, maxY, required);
    }
}
