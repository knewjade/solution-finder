package concurrent;

import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class LockedCandidateThreadLocal extends ThreadLocal<Candidate<? extends Action>> {
    private final int maxY;
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;

    public LockedCandidateThreadLocal(int maxY) {
        this.maxY = maxY;
        this.minoFactory = new MinoFactory();
        this.minoShifter = new MinoShifter();
        this.minoRotation = new MinoRotation();
    }

    @Override
    protected LockedCandidate initialValue() {
        return new LockedCandidate(minoFactory, minoShifter, minoRotation, maxY);
    }
}