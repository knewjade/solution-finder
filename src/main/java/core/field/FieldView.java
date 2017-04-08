package core.field;

public class FieldView {
    private static final int FIELD_WIDTH = 10;
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final char EMPTY_CHAR = '_';
    private static final char EXISTS_CHAR = 'X';

    public static String toString(Field field) {
        return toString(field, field.getMaxFieldHeight());
    }

    public static String toString(Field field, int maxHeight) {
        StringBuilder builder = new StringBuilder();
        for (int y = maxHeight - 1; y >= 0; y--) {
            for (int x = 0; x < FIELD_WIDTH; x++)
                builder.append(field.isEmpty(x, y) ? EMPTY_CHAR : EXISTS_CHAR);

            if (y != 0)
                builder.append(LINE_SEPARATOR);
        }
        return builder.toString();
    }
}
