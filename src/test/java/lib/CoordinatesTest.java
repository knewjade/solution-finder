package lib;

import common.datastore.Coordinate;
import core.mino.Block;
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
        assertThat(Coordinates.walk(minoFactory.create(Block.I, Rotate.Spawn), 4)).hasSize(28);
        assertThat(Coordinates.walk(minoFactory.create(Block.I, Rotate.Left), 4)).hasSize(10);
        assertThat(Coordinates.walk(minoFactory.create(Block.I, Rotate.Reverse), 4)).hasSize(28);
        assertThat(Coordinates.walk(minoFactory.create(Block.I, Rotate.Right), 4)).hasSize(10);
    }

    @Test
    void walkSizeWithJ() {
        MinoFactory minoFactory = new MinoFactory();
        assertThat(Coordinates.walk(minoFactory.create(Block.J, Rotate.Spawn), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Block.J, Rotate.Left), 4)).hasSize(18);
        assertThat(Coordinates.walk(minoFactory.create(Block.J, Rotate.Reverse), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Block.J, Rotate.Right), 4)).hasSize(18);
    }

    @Test
    void walkSizeWithL() {
        MinoFactory minoFactory = new MinoFactory();
        assertThat(Coordinates.walk(minoFactory.create(Block.L, Rotate.Spawn), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Block.L, Rotate.Left), 4)).hasSize(18);
        assertThat(Coordinates.walk(minoFactory.create(Block.L, Rotate.Reverse), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Block.L, Rotate.Right), 4)).hasSize(18);
    }

    @Test
    void walkSizeWithO() {
        MinoFactory minoFactory = new MinoFactory();
        assertThat(Coordinates.walk(minoFactory.create(Block.O, Rotate.Spawn), 4)).hasSize(27);
        assertThat(Coordinates.walk(minoFactory.create(Block.O, Rotate.Left), 4)).hasSize(27);
        assertThat(Coordinates.walk(minoFactory.create(Block.O, Rotate.Reverse), 4)).hasSize(27);
        assertThat(Coordinates.walk(minoFactory.create(Block.O, Rotate.Right), 4)).hasSize(27);
    }

    @Test
    void walkSizeWithS() {
        MinoFactory minoFactory = new MinoFactory();
        assertThat(Coordinates.walk(minoFactory.create(Block.S, Rotate.Spawn), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Block.S, Rotate.Left), 4)).hasSize(18);
        assertThat(Coordinates.walk(minoFactory.create(Block.S, Rotate.Reverse), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Block.S, Rotate.Right), 4)).hasSize(18);
    }

    @Test
    void walkSizeWithZ() {
        MinoFactory minoFactory = new MinoFactory();
        assertThat(Coordinates.walk(minoFactory.create(Block.Z, Rotate.Spawn), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Block.Z, Rotate.Left), 4)).hasSize(18);
        assertThat(Coordinates.walk(minoFactory.create(Block.Z, Rotate.Reverse), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Block.Z, Rotate.Right), 4)).hasSize(18);
    }

    @Test
    void walkSizeWithT() {
        MinoFactory minoFactory = new MinoFactory();
        assertThat(Coordinates.walk(minoFactory.create(Block.T, Rotate.Spawn), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Block.T, Rotate.Left), 4)).hasSize(18);
        assertThat(Coordinates.walk(minoFactory.create(Block.T, Rotate.Reverse), 4)).hasSize(24);
        assertThat(Coordinates.walk(minoFactory.create(Block.T, Rotate.Right), 4)).hasSize(18);
    }

    @Test
    void walkContainsWithI() {
        MinoFactory minoFactory = new MinoFactory();
        Set<Coordinate> coordinates = Coordinates.walk(minoFactory.create(Block.I, Rotate.Spawn), 4)
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