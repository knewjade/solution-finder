package concurrent;

import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.action.candidate.HarddropCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;

public class HarddropCandidateThreadLocal extends ThreadLocal<Candidate<? extends Action>> {
    @Override
    protected HarddropCandidate initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        return new HarddropCandidate(minoFactory, minoShifter);
    }
}