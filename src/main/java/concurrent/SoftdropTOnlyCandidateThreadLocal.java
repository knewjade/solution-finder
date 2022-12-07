package concurrent;

import core.action.candidate.SoftdropTOnlyCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

import java.util.function.Supplier;

public class SoftdropTOnlyCandidateThreadLocal extends ThreadLocal<SoftdropTOnlyCandidate> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final int maxY;

    public SoftdropTOnlyCandidateThreadLocal(Supplier<MinoRotation> minoRotationSupplier, int maxY) {
        this(new MinoFactory(), new MinoShifter(), minoRotationSupplier.get(), maxY);
    }

    public SoftdropTOnlyCandidateThreadLocal(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxY = maxY;
    }

    @Override
    protected SoftdropTOnlyCandidate initialValue() {
        return new SoftdropTOnlyCandidate(minoFactory, minoShifter, minoRotation, maxY);
    }
}
