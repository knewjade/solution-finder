package core.field;

import core.mino.Mino;
import core.mino.Piece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LargeFieldTest {
    @Test
    void testGetMaxFieldHeight() throws Exception {
        Field field = FieldFactory.createMiddleField();
        assertThat(field.getMaxFieldHeight()).isEqualTo(24);
    }

    @Test
    void block() {
        LargeField field = FieldFactory.createLargeField();

        for (int index = 0; index < 24; index++)
            field.setBlock(index % 10, index);

        for (int y = 0; y < 24; y++)
            for (int x = 0; x < 10; x++)
                assertThat(field.isEmpty(x, y)).isEqualTo(x != y % 10);

        for (int index = 0; index < 24; index++)
            field.removeBlock(index % 10, index);

        for (int y = 0; y < 24; y++)
            for (int x = 0; x < 10; x++)
                assertThat(field.isEmpty(x, y)).isTrue();
    }

    @Test
    void testPutAndRemoveMino() throws Exception {
        for (int y = 1; y < 22; y++) {
            for (int x = 0; x < 8; x++) {
                Field field = FieldFactory.createLargeField();

                field.put(new Mino(Piece.T, Rotate.Right), x, y);
                assertThat(field.isEmpty(x, y)).isFalse();
                assertThat(field.isEmpty(x, y - 1)).isFalse();
                assertThat(field.isEmpty(x, y + 1)).isFalse();
                assertThat(field.isEmpty(x + 1, y)).isFalse();
            }
        }
    }
}