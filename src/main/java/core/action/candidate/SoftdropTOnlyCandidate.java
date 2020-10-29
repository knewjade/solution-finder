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
    private final LockedCandidate lockedCandidate;

    public SoftdropTOnlyCandidate(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY) {
        this.harddropCandidate = new HarddropCandidate(minoFactory, minoShifter);
        this.lockedCandidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxY);
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