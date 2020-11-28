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
public class TSpinAndHarddropReachable implements Reachable {
    private final HarddropReachable harddropReachable;
    private final int required;
    private final SpinChecker spinChecker;

    /*
     * @param required Tスピン時に最低限必要な消去ライン数。ただし、0以下を指定しても「ライン消去が伴わないTスピン」は許可されない。
     *                 `1 <= required`のとき、Regular T-Spinのみとなる。
     *                 `required <= 0`のとき、Miniを含むすべてのT-Spinが許可される。
     */
    public TSpinAndHarddropReachable(
            MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY, int required
    ) {
        this.harddropReachable = new HarddropReachable(minoFactory, minoShifter, maxY);
        LockedReachable lockedReachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);
        this.spinChecker = new SpinChecker(minoFactory, new MinoRotationDetail(minoFactory, minoRotation), lockedReachable);
        this.required = required;
    }

    @Override
    public boolean checks(Field field, Mino mino, int x, int y, int validHeight) {
        assert field.canPut(mino, x, y);
        assert field.getFilledLine() == 0L;

        if (mino.getPiece() == Piece.T) {
            Field freeze = field.freeze(validHeight);
            freeze.put(mino, x, y);
            int clearLine = freeze.clearLine();

            if (0 < clearLine) {
                SimpleOperation operation = new SimpleOperation(mino.getPiece(), mino.getRotate(), x, y);
                Optional<Spin> spin = spinChecker.check(field, operation, validHeight, clearLine);
                if (spin.isPresent()) {
                    if (required <= 0) {
                        // required=0以下のときは、Miniを許可
                        return true;
                    }

                    if (spin.get().getSpin() != TSpins.Mini) {
                        if (required <= clearLine) {
                            return true;
                        }
                    }
                }
            }
        }

        return harddropReachable.checks(field, mino, x, y, validHeight);
    }
}