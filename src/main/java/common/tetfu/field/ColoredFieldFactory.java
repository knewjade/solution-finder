package common.tetfu.field;

import common.tetfu.common.ColorType;
import core.field.Field;

// TODO: write unittest
public class ColoredFieldFactory {
    private static final int MAX_HEIGHT = 24;

    public static ColoredField createField(int maxHeight) {
        return new ArrayColoredField(maxHeight);
    }

    public static ColoredField createColoredField(String marks) {
        if (marks.length() % 10 != 0)
            throw new IllegalArgumentException("length of marks should be 'mod 10'");

        int maxY = marks.length() / 10;
        ColoredField field = new ArrayColoredField(MAX_HEIGHT);
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < 10; x++) {
                char mark = marks.charAt((maxY - y - 1) * 10 + x);
                if (mark != ' ' && mark != '_') {
                    field.setColorType(get(mark), x, y);
                }
            }
        }

        return field;
    }

    private static ColorType get(char mark) {
        switch (mark) {
            case 'I':
                return ColorType.I;
            case 'T':
                return ColorType.T;
            case 'S':
                return ColorType.S;
            case 'Z':
                return ColorType.Z;
            case 'J':
                return ColorType.J;
            case 'L':
                return ColorType.L;
            case 'O':
                return ColorType.O;
            case 'X':
                return ColorType.Gray;
        }
        throw new IllegalStateException("No reachable");
    }

    public static ColoredField createGrayField(Field field) {
        ColoredField coloredField = new ArrayColoredField(MAX_HEIGHT);
        for (int y = 0; y < field.getMaxFieldHeight(); y++)
            for (int x = 0; x < 10; x++)
                if (!field.isEmpty(x, y))
                    coloredField.setColorType(ColorType.Gray, x, y);
        return coloredField;
    }
}
