package lib;

import common.datastore.Coordinate;
import core.mino.Piece;
import core.mino.MinoFactory;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinatesTest {
    @Test
    void walkSizeWithI() {
        MinoFactory minoFactory = new MinoFactory();
        assertThat(Coordinates.walk(minoFactory.create(Piece.I, Rotate.Spawn), 4)).hasSize(28);
        assertThat(Coordinates.walk(minoFactory.create(Piece.I, Rotate.Left), 4)).hasSize(10);
        assertThat(Coordinates.walk(minoFactory.create(Piece.I, Rotate.Reverse), 4)).hasSize(28);
        assertThat(Coordinates.walk(minoFactory.create(Piece.I, Rotate.Right), 4)).hasSize(10);
    }

    @Test
    void walkSizeWithJ() {
        MinoFactory minoFactory = new MinoFactory();
        assertThat(Coordinates.walk(minoFactory.create(Piece.J, Rotate.Spawn), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Piece.J, Rotate.Left), 4)).hasSize(18);
        assertThat(Coordinates.walk(minoFactory.create(Piece.J, Rotate.Reverse), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Piece.J, Rotate.Right), 4)).hasSize(18);
    }

    @Test
    void walkSizeWithL() {
        MinoFactory minoFactory = new MinoFactory();
        assertThat(Coordinates.walk(minoFactory.create(Piece.L, Rotate.Spawn), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Piece.L, Rotate.Left), 4)).hasSize(18);
        assertThat(Coordinates.walk(minoFactory.create(Piece.L, Rotate.Reverse), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Piece.L, Rotate.Right), 4)).hasSize(18);
    }

    @Test
    void walkSizeWithO() {
        MinoFactory minoFactory = new MinoFactory();
        assertThat(Coordinates.walk(minoFactory.create(Piece.O, Rotate.Spawn), 4)).hasSize(27);
        assertThat(Coordinates.walk(minoFactory.create(Piece.O, Rotate.Left), 4)).hasSize(27);
        assertThat(Coordinates.walk(minoFactory.create(Piece.O, Rotate.Reverse), 4)).hasSize(27);
        assertThat(Coordinates.walk(minoFactory.create(Piece.O, Rotate.Right), 4)).hasSize(27);
    }

    @Test
    void walkSizeWithS() {
        MinoFactory minoFactory = new MinoFactory();
        assertThat(Coordinates.walk(minoFactory.create(Piece.S, Rotate.Spawn), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Piece.S, Rotate.Left), 4)).hasSize(18);
        assertThat(Coordinates.walk(minoFactory.create(Piece.S, Rotate.Reverse), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Piece.S, Rotate.Right), 4)).hasSize(18);
    }

    @Test
    void walkSizeWithZ() {
        MinoFactory minoFactory = new MinoFactory();
        assertThat(Coordinates.walk(minoFactory.create(Piece.Z, Rotate.Spawn), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Piece.Z, Rotate.Left), 4)).hasSize(18);
        assertThat(Coordinates.walk(minoFactory.create(Piece.Z, Rotate.Reverse), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Piece.Z, Rotate.Right), 4)).hasSize(18);
    }

    @Test
    void walkSizeWithT() {
        MinoFactory minoFactory = new MinoFactory();
        assertThat(Coordinates.walk(minoFactory.create(Piece.T, Rotate.Spawn), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Piece.T, Rotate.Left), 4)).hasSize(18);
        assertThat(Coordinates.walk(minoFactory.create(Piece.T, Rotate.Reverse), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Piece.T, Rotate.Right), 4)).hasSize(18);
    }

    @Test
    void walkContainsWithI() {
        MinoFactory minoFactory = new MinoFactory();
        Set<Coordinate> coordinates = Coordinates.walk(minoFactory.create(Piece.I, Rotate.Spawn), 4)
                .collect(Collectors.toSet());

        assertThat(coordinates)
                .contains(Coordinate.create(1, 0))
                .contains(Coordinate.create(7, 0))
                .contains(Coordinate.create(1, 3))
                .contains(Coordinate.create(7, 3))
                .doesNotContain(Coordinate.create(0, 0))
                .doesNotContain(Coordinate.create(8, 0))
                .doesNotContain(Coordinate.create(0, 3))
                .doesNotContain(Coordinate.create(8, 3))
                .doesNotContain(Coordinate.create(1, -1))
                .doesNotContain(Coordinate.create(7, -1))
                .doesNotContain(Coordinate.create(1, 4))
                .doesNotContain(Coordinate.create(7, 4));
    }
}