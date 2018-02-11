package common.tetfu.field;

public class ColoredFieldView {
    private static final int FIELD_WIDTH = 10;
    private static final String LINE_SEPARATOR = System.lineSeparator();

    public static String toString(ColoredField field) {
        return toString(field, field.getMaxHeight());
    }

    public static String toString(ColoredField field, int maxHeight) {
        return toString(field, maxHeight, LINE_SEPARATOR);
    }

    public static String toString(ColoredField field, int maxHeight, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int y = maxHeight - 1; 0 <= y; y--) {
            for (int x = 0; x < FIELD_WIDTH; x++)
                builder.append(field.getBlockNumber(x, y));
            builder.append(separator);
        }
        return builder.toString();
    }
}
