package concurrent;

import core.action.candidate.SRSAnd180Candidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

import java.util.function.Supplier;

public class SRSAnd180CandidateThreadLocal extends ThreadLocal<SRSAnd180Candidate> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final int maxY;

    public SRSAnd180CandidateThreadLocal(Supplier<MinoRotation> minoRotationSupplier, int maxY) {
        this(new MinoFactory(), new MinoShifter(), minoRotationSupplier.get(), maxY);
    }

    public SRSAnd180CandidateThreadLocal(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY
    ) {
        if (minoRotation.noSupports180()) {
            throw new IllegalArgumentException("mino rotation does not support 180");
        }

        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxY = maxY;
    }

    @Override
    protected SRSAnd180Candidate initialValue() {
        return new SRSAnd180Candidate(minoFactory, minoShifter, minoRotation, maxY);
    }
}
