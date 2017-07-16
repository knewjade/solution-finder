package core.field;

import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class FieldViewTest {
    @Test
    void testRandom() {
        Randoms randoms = new Randoms();
        String lineSeparator = System.lineSeparator();

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
                if (y != 0)
                    builder.append(lineSeparator);
            }
            String marks = builder.toString();

            Field field = FieldFactory.createField(marks.replace(lineSeparator, ""));
            String string = FieldView.toString(field, height);
            assertThat(string).isEqualTo(marks);
        }
    }

    @Test
    void testRandomWithHeight() {
        Randoms randoms = new Randoms();
        String lineSeparator = System.lineSeparator();

        int width = 10;
        for (int count = 0; count < 10000; count++) {
            int height = randoms.nextIntClosed(1, 12);

            // create field
            boolean[][] fields = new boolean[height][width];
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    fields[y][x] = randoms.nextBoolean();

            // parse to string for factory
            StringBuilder builder = new StringBuilder();
            for (int y = height - 1; 0 <= y; y--) {
                for (int x = 0; x < width; x++) {
                    boolean isEmpty = fields[y][x];
                    builder.append(isEmpty ? '_' : 'X');
                }
                if (y != 0)
                    builder.append(lineSeparator);
            }
            String marks = builder.toString();

            // parse to string for assertion
            int emptyLine = (12 - height) % 6;
            ArrayList<String> list = new ArrayList<>();
            for (int y = 0; y < emptyLine; y++)
                list.add("__________");
            list.add(marks);
            String expected = list.stream().collect(Collectors.joining(lineSeparator));

            // check
            Field field = FieldFactory.createField(marks.replace(lineSeparator, ""));
            String string = FieldView.toString(field);
            assertThat(string).isEqualTo(expected);
        }
    }
}