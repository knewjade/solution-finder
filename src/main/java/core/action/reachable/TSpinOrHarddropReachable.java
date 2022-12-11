package core.action.reachable;

import common.SpinChecker;
import common.datastore.SimpleOperation;
import core.field.Field;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.MinoRotationDetail;
import searcher.spins.spin.Spin;
import searcher.spins.spin.TSpins;

import java.util.Optional;

/**
 * マルチスレッド非対応
 */
public class TSpinOrHarddropReachable implements Reachable {
    private final HarddropReachable harddropReachable;
    private final int required;
    private final SpinChecker spinChecker;
    private final boolean regularOnly;

    /*
     * @param required Tスピン時に最低限必要な消去ライン数
     */
    public TSpinOrHarddropReachable(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY, int required, boolean regularOnly, boolean use180Rotation
    ) {
        this.harddropReachable = new HarddropReachable(minoFactory, minoShifter, maxY);
        ILockedReachable lockedReachable = ReachableFacade.createLocked(minoFactory, minoShifter, minoRotation, maxY, use180Rotation);
        this.spinChecker = new SpinChecker(minoFactory, new MinoRotationDetail(minoFactory, minoRotation), lockedReachable);
        this.required = required;
        this.regularOnly = regularOnly;
    }

    @Override
    public boolean checks(Field field, Mino mino, int x, int y, int validHeight) {
        assert field.canPut(mino, x, y);
        assert field.getFilledLine() == 0L;

        if (mino.getPiece() == Piece.T) {
            Field freeze = field.freeze();
            freeze.put(mino, x, y);
            int clearLine = freeze.clearLine();

            if (required <= clearLine) {
                SimpleOperation operation = new SimpleOperation(mino.getPiece(), mino.getRotate(), x, y);
                Optional<Spin> spin = spinChecker.check(field, operation, validHeight, clearLine);
                if (spin.isPresent()) {
                    if (regularOnly) {
                        return spin.get().getSpin() == TSpins.Regular;
                    } else {
                        return true;
                    }
                }
            }

            return false;
        }

        return harddropReachable.checks(field, mino, x, y, validHeight);
    }
}