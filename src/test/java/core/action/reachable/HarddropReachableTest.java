package core.action.reachable;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HarddropReachableTest {
    @Test
    void checks() {
        Field field = FieldFactory.createField("" +
                "_XX_____XX" +
                "__XX____XX" +
                "X_XX____XX" +
                "XXXXX___XX"
        );

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        HarddropReachable reachable = new HarddropReachable(minoFactory, minoShifter, 4);

        assertThat(reachable.checks(field, minoFactory.create(Piece.I, Rotate.Spawn), 5, 1, 4)).isTrue();
        assertThat(reachable.checks(field, minoFactory.create(Piece.I, Rotate.Spawn), 5, 2, 4)).isFalse();

        assertThat(reachable.checks(field, minoFactory.create(Piece.S, Rotate.Left), 1, 2, 4)).isFalse();
    }
}