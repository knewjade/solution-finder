package core.action.reachable;

import core.field.Field;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;

/**
 * マルチスレッド非対応
 */
public class SoftdropTOnlyReachable implements Reachable {
    private final HarddropReachable harddropReachable;
    private final ILockedReachable lockedReachable;

    public SoftdropTOnlyReachable(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY, boolean use180Rotation
    ) {
        this.harddropReachable = new HarddropReachable(minoFactory, minoShifter, maxY);
        this.lockedReachable = ReachableFacade.createLocked(minoFactory, minoShifter, minoRotation, maxY, use180Rotation);
    }

    @Override
    public boolean checks(Field field, Mino mino, int x, int y, int validHeight) {
        if (mino.getPiece() == Piece.T) {
            return lockedReachable.checks(field, mino, x, y, validHeight);
        } else {
            return harddropReachable.checks(field, mino, x, y, validHeight);
        }
    }
}