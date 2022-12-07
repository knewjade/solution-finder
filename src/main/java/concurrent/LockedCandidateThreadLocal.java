package concurrent;

import core.action.candidate.LockedCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

import java.util.function.Supplier;

public class LockedCandidateThreadLocal extends ThreadLocal<LockedCandidate> {
    private final Supplier<MinoRotation> minoRotationSupplier;
    private final int maxY;

    public LockedCandidateThreadLocal(Supplier<MinoRotation> minoRotationSupplier, int maxY) {
        this.minoRotationSupplier = minoRotationSupplier;
        this.maxY = maxY;
    }

    @Override
    protected LockedCandidate initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = minoRotationSupplier.get();
        return new LockedCandidate(minoFactory, minoShifter, minoRotation, maxY);
    }
}