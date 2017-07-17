package core.column_field;

import lib.Randoms;
import org.junit.jupiter.api.Test;
import searcher.pack.SizedBit;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnFieldViewTest {
    @Test
    void test() {
        int height = 4;
        int width = 3;
        SizedBit sizedBit = new SizedBit(width, height);

        ColumnSmallField field = new ColumnSmallField();
        field.setBlock(0, 0, height);
        field.setBlock(1, 1, height);
        field.setBlock(2, 2, height);
        field.setBlock(1, 3, height);

        String lineSeparator = System.lineSeparator();
        String expect = Stream.of(
                "_X_",
                "__X",
                "_X_",
                "X__"
        ).collect(Collectors.joining(lineSeparator));

        assertThat(ColumnFieldView.toString(field, sizedBit)).isEqualTo(expect);
    }

    @Test
    void testRandom() {
        Randoms randoms = new Randoms();
        String lineSeparator = System.lineSeparator();

        for (int count = 0; count < 10000; count++) {
            int width = randoms.nextIntClosed(1, 6);
            int height = randoms.nextIntClosed(1, 10);
            SizedBit sizedBit = new SizedBit(width, height);

            // create fields
            boolean[][] fields = new boolean[height][width];
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    fields[y][x] = randoms.nextBoolean();

            // parse to long
            long board = 0L;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    boolean isEmpty = fields[y][x];
                    board += isEmpty ? 0 : (1L << (x * height + y));
                }
            }
            ColumnSmallField field = ColumnFieldFactory.createField(board);

            // parse to strings
            StringBuilder builder = new StringBuilder();
            for (int y = height - 1; 0 <= y; y--) {
                for (int x = 0; x < width; x++) {
                    boolean isEmpty = fields[y][x];
                    builder.append(isEmpty ? '_' : 'X');
                }
                if (y != 0)
                    builder.append(lineSeparator);
            }
            String expect = builder.toString();

            assertThat(ColumnFieldView.toString(field, sizedBit)).isEqualTo(expect);
        }
    }
}