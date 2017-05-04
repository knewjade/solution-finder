package common.tetfu.field;

public class ColoredFieldView {
    private static final int FIELD_WIDTH = 10;

    public static String toString(ColoredField field) {
        return toString(field, field.getMaxHeight());
    }

    public static String toString(ColoredField field, int maxHeight) {
        StringBuilder builder = new StringBuilder();
        for (int y = maxHeight - 1; 0 <= y; y--) {
            for (int x = 0; x < FIELD_WIDTH; x++)
                builder.append(field.getBlockNumber(x, y));
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }
}
