package common;

import common.datastore.SimpleOperation;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.MinoRotationDetail;
import core.srs.Rotate;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import org.junit.jupiter.api.Test;
import searcher.spins.spin.Spin;
import searcher.spins.spin.TSpins;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SpinCheckerTest {
    @Test
    void spin() {
        int maxY = 24;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        MinoRotationDetail minoRotationDetail = new MinoRotationDetail(minoFactory, minoRotation);
        LockedReachable lockedReachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);
        SpinChecker spinChecker = new SpinChecker(minoFactory, minoRotationDetail, lockedReachable);

        Field field = FieldFactory.createField("" +
                "X__XX_X___" +
                "X___XXXXXX" +
                "XX_XXXXXXX"
        );

        SimpleOperation operation = new SimpleOperation(Piece.T, Rotate.Reverse, 2, 1);
        Optional<Spin> spin = spinChecker.check(field, operation, maxY, 2);

        assertThat(spin.isPresent()).isTrue();
        assertThat(spin.get())
                .returns(TSpins.Regular, Spin::getSpin)
                .returns(2, Spin::getClearedLine);
    }
}