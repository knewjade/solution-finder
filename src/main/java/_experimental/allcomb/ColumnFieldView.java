package _experimental.allcomb;

public class ColumnFieldView {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final char EMPTY_CHAR = '_';
    private static final char EXISTS_CHAR = 'X';

    public static String toString(ColumnField field, int maxWidth, int maxHeight) {
        StringBuilder builder = new StringBuilder();
        for (int y = maxHeight - 1; y >= 0; y--) {
            for (int x = 0; x < maxWidth; x++)
                builder.append(field.isEmpty(x, y, maxHeight) ? EMPTY_CHAR : EXISTS_CHAR);

            if (y != 0)
                builder.append(LINE_SEPARATOR);
        }
        return builder.toString();
    }
}
