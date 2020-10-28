package concurrent;

import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.action.candidate.SoftdropTOnlyCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class SoftdropTOnlyCandidateThreadLocal extends ThreadLocal<SoftdropTOnlyCandidate> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final int maxY;

    public SoftdropTOnlyCandidateThreadLocal(int maxY) {
        this(new MinoFactory(), new MinoShifter(), MinoRotation.create(), maxY);
    }

    public SoftdropTOnlyCandidateThreadLocal(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxY = maxY;
    }

    @Override
    protected SoftdropTOnlyCandidate initialValue() {
        return new SoftdropTOnlyCandidate(minoFactory, minoShifter, minoRotation, maxY);
    }
}
