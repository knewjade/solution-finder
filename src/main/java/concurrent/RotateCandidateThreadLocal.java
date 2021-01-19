package concurrent;

import core.action.candidate.RotateCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class RotateCandidateThreadLocal extends ThreadLocal<RotateCandidate> {
    private final int maxY;

    public RotateCandidateThreadLocal(int maxY) {
        this.maxY = maxY;
    }

    @Override
    protected RotateCandidate initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = MinoRotation.create();
        return new RotateCandidate(minoFactory, minoShifter, minoRotation, maxY);
    }
}