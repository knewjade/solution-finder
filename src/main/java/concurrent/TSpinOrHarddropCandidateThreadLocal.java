package concurrent;

import core.action.candidate.TSpinOrHarddropCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class TSpinOrHarddropCandidateThreadLocal extends ThreadLocal<TSpinOrHarddropCandidate> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final int maxY;
    private final int required;

    public TSpinOrHarddropCandidateThreadLocal(int maxY, int required) {
        this(new MinoFactory(), new MinoShifter(), MinoRotation.create(), maxY, required);
    }

    public TSpinOrHarddropCandidateThreadLocal(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY, int required
    ) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxY = maxY;
        this.required = required;
    }

    @Override
    protected TSpinOrHarddropCandidate initialValue() {
        return new TSpinOrHarddropCandidate(minoFactory, minoShifter, minoRotation, maxY, required, true);
    }
}
