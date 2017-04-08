package concurrent;

import action.candidate.Candidate;
import action.candidate.LockedCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import searcher.common.action.Action;

public class CandidateThreadLocal extends ThreadLocal<Candidate<Action>> {
    private final int maxY;

    public CandidateThreadLocal(int maxY) {
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