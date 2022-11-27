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
    private final boolean regularOnly;

    public TSpinOrHarddropReachableThreadLocal(int maxY, int required, boolean regularOnly) {
        this(new MinoFactory(), new MinoShifter(), MinoRotation.create(), maxY, required, regularOnly);
    }

    public TSpinOrHarddropReachableThreadLocal(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY, int required, boolean regularOnly
    ) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxY = maxY;
        this.required = required;
        this.regularOnly = regularOnly;
    }

    @Override
    protected TSpinOrHarddropReachable initialValue() {
        return new TSpinOrHarddropReachable(minoFactory, minoShifter, minoRotation, maxY, required, regularOnly);
    }
}
