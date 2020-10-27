package common.tetfu.field;

import common.tetfu.common.ColorType;

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

    public static String toStringWithType(ColoredField field) {
        return toStringWithType(field, field.getMaxHeight());
    }

    public static String toStringWithType(ColoredField field, int maxHeight) {
        return toStringWithType(field, maxHeight, LINE_SEPARATOR);
    }

    public static String toStringWithType(ColoredField field, int maxHeight, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int y = maxHeight - 1; 0 <= y; y--) {
            for (int x = 0; x < FIELD_WIDTH; x++)
                builder.append(toShort(field.getColorType(x, y)));
            builder.append(separator);
        }
        return builder.toString();
    }

    private static String toShort(ColorType type) {
        switch (type) {
            case Empty:
                return "_";
            case I:
                return "I";
            case L:
                return "L";
            case O:
                return "O";
            case Z:
                return "Z";
            case T:
                return "T";
            case J:
                return "J";
            case S:
                return "S";
            case Gray:
                return "X";
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}
