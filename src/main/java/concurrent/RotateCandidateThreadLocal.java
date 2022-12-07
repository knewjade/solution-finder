package concurrent;

import core.action.candidate.RotateCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

import java.util.function.Supplier;

public class RotateCandidateThreadLocal extends ThreadLocal<RotateCandidate> {
    private final Supplier<MinoRotation> minoRotationSupplier;
    private final int maxY;

    public RotateCandidateThreadLocal(Supplier<MinoRotation> minoRotationSupplier, int maxY) {
        this.minoRotationSupplier = minoRotationSupplier;
        this.maxY = maxY;
    }

    @Override
    protected RotateCandidate initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = minoRotationSupplier.get();
        return new RotateCandidate(minoFactory, minoShifter, minoRotation, maxY);
    }
}