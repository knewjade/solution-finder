package core.field;

import lib.Randoms;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FieldFactoryTest {
    @Test
    void testSmall() {
        String marks = "" +
                "XXXXX_XXXX" +
                "XXXX_XXXXX" +
                "XXX_XXXXXX" +
                "XX_XXXXXXX" +
                "X_XXXXXXXX" +
                "_XXXXXXXXX";
        Field field = FieldFactory.createField(marks);

        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 10; x++) {
                boolean isEmpty = x == y;
                assertThat(field.isEmpty(x, y)).isEqualTo(isEmpty);
            }
        }
    }

    @Test
    void testMiddle() {
        String marks = "" +
                "X_XXXXXXXX" +
                "_XXXXXXXXX" +
                "XXXXXXXXX_" +
                "XXXXXXXX_X" +
                "XXXXXXX_XX" +
                "XXXXXX_XXX" +
                "XXXXX_XXXX" +
                "XXXX_XXXXX" +
                "XXX_XXXXXX" +
                "XX_XXXXXXX" +
                "X_XXXXXXXX" +
                "_XXXXXXXXX";
        Field field = FieldFactory.createField(marks);

        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 10; x++) {
                boolean isEmpty = x == (y % 10);
                assertThat(field.isEmpty(x, y)).isEqualTo(isEmpty);
            }
        }
    }

    @Test
    void testRandom() {
        Randoms randoms = new Randoms();
        int width = 10;
        for (int count = 0; count < 10000; count++) {
            int height = randoms.nextIntClosed(1, 12);
            boolean[][] fields = new boolean[height][width];
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    fields[y][x] = randoms.nextBoolean();

            StringBuilder builder = new StringBuilder();
            for (int y = height - 1; 0 <= y; y--) {
                for (int x = 0; x < width; x++) {
                    boolean isEmpty = fields[y][x];
                    builder.append(isEmpty ? '_' : 'X');
                }
            }
            String marks = builder.toString();

            Field field = FieldFactory.createField(marks);
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    assertThat(field.isEmpty(x, y)).isEqualTo(fields[y][x]);
        }
    }
}