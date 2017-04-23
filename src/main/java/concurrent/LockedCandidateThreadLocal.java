package concurrent;

import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import searcher.common.action.Action;

public class LockedCandidateThreadLocal extends ThreadLocal<Candidate<Action>> {
    private final int maxY;

    public LockedCandidateThreadLocal(int maxY) {
        this.maxY = maxY;
    }

    @Override
    protected Candidate<Action> initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        return new LockedCandidate(minoFactory, minoShifter, minoRotation, maxY);
    }
}