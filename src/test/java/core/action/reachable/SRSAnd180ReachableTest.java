package core.action.reachable;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.common.kicks.factory.DefaultMinoRotationFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SRSAnd180ReachableTest {
    @Test
    void testSearch1() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = DefaultMinoRotationFactory.createDefault();
        Reachable reachable = new SRSAnd180Reachable(minoFactory, minoShifter, minoRotation, 6);

        String marks = "" +
                "__________" +
                "XXX__XXXXX" +
                "XXX___XXXX" +
                "X___XXXXXX" +
                "XX_XXXXXXX";
        Field field = FieldFactory.createField(marks);

        boolean checks = reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, 6);
        assertThat(checks).isTrue();
    }
}