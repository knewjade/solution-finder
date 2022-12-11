package core.action.candidate;

import common.datastore.action.Action;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;

import java.util.Set;

public class SoftdropTOnlyCandidate implements Candidate<Action> {
    private final HarddropCandidate harddropCandidate;
    private final ILockedCandidate lockedCandidate;

    public SoftdropTOnlyCandidate(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY, boolean use180Rotation) {
        this.harddropCandidate = new HarddropCandidate(minoFactory, minoShifter);
        this.lockedCandidate = CandidateFacade.createLocked(minoFactory, minoShifter, minoRotation, maxY, use180Rotation);
    }

    @Override
    public Set<Action> search(Field field, Piece piece, int validHeight) {
        if (piece == Piece.T) {
            return lockedCandidate.search(field, piece, validHeight);
        } else {
            return harddropCandidate.search(field, piece, validHeight);
        }
    }
}