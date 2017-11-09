package concurrent;

import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.action.candidate.LockedNeighborCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.neighbor.Neighbors;
import core.neighbor.OriginalPieceFactory;
import core.srs.MinoRotation;

public class LockedNeighborCandidateThreadLocal extends ThreadLocal<Candidate<? extends Action>> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final Neighbors neighbors;

    public LockedNeighborCandidateThreadLocal(int maxY) {
        MinoRotation minoRotation = new MinoRotation();
        OriginalPieceFactory pieceFactory = new OriginalPieceFactory(maxY + 3);

        this.minoFactory = new MinoFactory();
        this.minoShifter = new MinoShifter();
        this.neighbors = new Neighbors(minoFactory, minoRotation, pieceFactory);
    }

    @Override
    protected LockedNeighborCandidate initialValue() {
        return new LockedNeighborCandidate(minoFactory, minoShifter, neighbors);
    }
}