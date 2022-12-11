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
    private final boolean use180Rotation;

    public SoftdropTOnlyCandidateThreadLocal(Supplier<MinoRotation> minoRotationSupplier, int maxY, boolean use180Rotation) {
        this(new MinoFactory(), new MinoShifter(), minoRotationSupplier.get(), maxY, use180Rotation);
    }

    public SoftdropTOnlyCandidateThreadLocal(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY, boolean use180Rotation) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxY = maxY;
        this.use180Rotation = use180Rotation;
    }

    @Override
    protected SoftdropTOnlyCandidate initialValue() {
        return new SoftdropTOnlyCandidate(minoFactory, minoShifter, minoRotation, maxY, use180Rotation);
    }
}
