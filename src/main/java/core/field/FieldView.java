package core.field;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldView {
    private static final int FIELD_WIDTH = 10;
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final char EMPTY_CHAR = '_';
    private static final char EXISTS_CHAR = 'X';

    public static String toString(Field field) {
        return toString(field, field.getMaxFieldHeight());
    }

    public static String toString(Field field, int maxHeight) {
        return toString(field, maxHeight, LINE_SEPARATOR);
    }

    public static String toString(Field field, int maxHeight, String separator) {
        return toStrings(field, maxHeight).stream().collect(Collectors.joining(separator));
    }

    // TODO: write unittest
    public static List<String> toStrings(Field field, int maxHeight) {
        assert maxHeight <= field.getBoardCount() * 6 : field.getBoardCount() * 6;
        ArrayList<String> lines = new ArrayList<>();
        for (int y = maxHeight - 1; y >= 0; y--) {
            StringBuilder builder = new StringBuilder();
            for (int x = 0; x < FIELD_WIDTH; x++)
                builder.append(field.isEmpty(x, y) ? EMPTY_CHAR : EXISTS_CHAR);
            lines.add(builder.toString());
        }
        return lines;
    }
}
