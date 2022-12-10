package entry.common.kicks.factory;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import core.srs.RotateDirection;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class FileMinoRotationFactoryTest {
    @Test
    void loadNoKick90() {
        String properties = ClassLoader.getSystemResource("kicks/nokick90.properties").getPath();
        Path path = Paths.get(properties);
        FileMinoRotationFactory factory = FileMinoRotationFactory.load(path);
        MinoRotation minoRotation = factory.create();

        assertThat(minoRotation).returns(false, MinoRotation::supports180);

        // rotate right
        {
            Field field = FieldFactory.createSmallField();
            MinoFactory minoFactory = new MinoFactory();
            Mino before = minoFactory.create(Piece.T, Rotate.Spawn);
            Mino after = minoFactory.create(Piece.T, Rotate.Right);

            assertThat(minoRotation.getKicks(field, before, after, 1, 1, RotateDirection.Right))
                    .containsExactly(0, 0);

            assertThat(minoRotation.getKicks(field, before, after, 1, 0, RotateDirection.Right))
                    .isNull();
        }

        // rotate 180
        {
            Field field = FieldFactory.createSmallField();
            MinoFactory minoFactory = new MinoFactory();
            Mino before = minoFactory.create(Piece.T, Rotate.Spawn);
            Mino after = minoFactory.create(Piece.T, Rotate.Reverse);

            assertThatThrownBy(() -> minoRotation.getKicks(field, before, after, 1, 1, RotateDirection.Rotate180))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Test
    void loadNoKick180() {
        String properties = ClassLoader.getSystemResource("kicks/nokick180.properties").getPath();
        Path path = Paths.get(properties);
        FileMinoRotationFactory factory = FileMinoRotationFactory.load(path);
        MinoRotation minoRotation = factory.create();

        assertThat(minoRotation).returns(true, MinoRotation::supports180);

        // rotate right
        {
            Field field = FieldFactory.createSmallField();
            MinoFactory minoFactory = new MinoFactory();
            Mino before = minoFactory.create(Piece.T, Rotate.Spawn);
            Mino after = minoFactory.create(Piece.T, Rotate.Right);

            assertThat(minoRotation.getKicks(field, before, after, 1, 1, RotateDirection.Right))
                    .containsExactly(0, 0);

            assertThat(minoRotation.getKicks(field, before, after, 1, 0, RotateDirection.Right))
                    .isNull();
        }

        // rotate 180
        {
            Field field = FieldFactory.createSmallField();
            MinoFactory minoFactory = new MinoFactory();
            Mino before = minoFactory.create(Piece.T, Rotate.Spawn);
            Mino after = minoFactory.create(Piece.T, Rotate.Reverse);

            assertThat(minoRotation.getKicks(field, before, after, 1, 1, RotateDirection.Rotate180))
                    .containsExactly(0, 0);

            assertThat(minoRotation.getKicks(field, before, after, 1, 0, RotateDirection.Rotate180))
                    .isNull();
        }
    }

    @Test
    void loadSRS() {
        String properties = ClassLoader.getSystemResource("kicks/srs.properties").getPath();
        Path path = Paths.get(properties);
        FileMinoRotationFactory factory = FileMinoRotationFactory.load(path);
        MinoRotation minoRotation = factory.create();

        assertThat(minoRotation).returns(false, MinoRotation::supports180);

        MinoFactory minoFactory = new MinoFactory();
        MinoRotation defaultMinoRotation = SRSMinoRotationFactory.createDefault();
        for (Piece piece : Piece.values()) {
            for (Rotate rotate : Rotate.values()) {
                Mino mino = minoFactory.create(piece, rotate);
                for (RotateDirection direction : RotateDirection.valuesNo180()) {
                    assertThat(minoRotation.getPatternsFrom(mino, direction))
                            .isDeepEqualTo(defaultMinoRotation.getPatternsFrom(mino, direction));
                }
            }
        }
    }

    @Test
    void loadTwoLevelsOfReference() {
        String properties = ClassLoader.getSystemResource("kicks/two_levels_of_reference.properties").getPath();
        Path path = Paths.get(properties);
        FileMinoRotationFactory factory = FileMinoRotationFactory.load(path);
        MinoRotation minoRotation = factory.create();

        assertThat(minoRotation).returns(false, MinoRotation::supports180);

        MinoFactory minoFactory = new MinoFactory();
        MinoRotation defaultMinoRotation = SRSMinoRotationFactory.createDefault();
        for (Piece piece : Piece.values()) {
            for (Rotate rotate : Rotate.values()) {
                Mino mino = minoFactory.create(piece, rotate);
                for (RotateDirection direction : RotateDirection.valuesNo180()) {
                    assertThat(minoRotation.getPatternsFrom(mino, direction))
                            .isDeepEqualTo(defaultMinoRotation.getPatternsFrom(mino, direction));
                }
            }
        }
    }

    @Test
    void loadMissingIEN() {
        String properties = ClassLoader.getSystemResource("kicks/missing_I_EN.properties").getPath();
        Path path = Paths.get(properties);
        assertThatThrownBy(() -> FileMinoRotationFactory.load(path)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void loadSurplusIEN() {
        String properties = ClassLoader.getSystemResource("kicks/surplus_I_EW.properties").getPath();
        Path path = Paths.get(properties);
        assertThatThrownBy(() -> FileMinoRotationFactory.load(path)).isInstanceOf(IllegalArgumentException.class);
    }
}
