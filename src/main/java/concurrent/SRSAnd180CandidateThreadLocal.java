package concurrent;

import core.action.candidate.SRSAnd180Candidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class SRSAnd180CandidateThreadLocal extends ThreadLocal<SRSAnd180Candidate> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final int maxY;

    public SRSAnd180CandidateThreadLocal(int maxY) {
        this(new MinoFactory(), new MinoShifter(), MinoRotation.create(), maxY);
    }

    public SRSAnd180CandidateThreadLocal(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY
    ) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxY = maxY;
    }

    @Override
    protected SRSAnd180Candidate initialValue() {
        return new SRSAnd180Candidate(minoFactory, minoShifter, minoRotation, maxY);
    }
}
