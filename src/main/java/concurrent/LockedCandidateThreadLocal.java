package concurrent;

import core.action.candidate.CandidateFacade;
import core.action.candidate.ILockedCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

import java.util.function.Supplier;

public class LockedCandidateThreadLocal extends ThreadLocal<ILockedCandidate> {
    private final Supplier<MinoRotation> minoRotationSupplier;
    private final int maxY;
    private final boolean use180Rotation;

    public LockedCandidateThreadLocal(Supplier<MinoRotation> minoRotationSupplier, int maxY, boolean use180Rotation) {
        this.minoRotationSupplier = minoRotationSupplier;
        this.maxY = maxY;
        this.use180Rotation = use180Rotation;
    }

    @Override
    protected ILockedCandidate initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = minoRotationSupplier.get();
        return CandidateFacade.createLocked(minoFactory, minoShifter, minoRotation, maxY, use180Rotation);
    }
}