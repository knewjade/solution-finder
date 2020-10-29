package concurrent;

import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class LockedCandidateThreadLocal extends ThreadLocal<LockedCandidate> {
    private final int maxY;

    public LockedCandidateThreadLocal(int maxY) {
        this.maxY = maxY;
    }

    @Override
    protected LockedCandidate initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = MinoRotation.create();
        return new LockedCandidate(minoFactory, minoShifter, minoRotation, maxY);
    }
}