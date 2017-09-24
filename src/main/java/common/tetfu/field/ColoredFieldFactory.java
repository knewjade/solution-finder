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
                    ColorType colorType = get(mark);
                    field.setColorType(colorType, x, y);
                }
            }
        }

        return field;
    }

    // TODO: write unittest
    private static ColorType get(char mark) {
        switch (Character.toLowerCase(mark)) {
            case 'i':
                return ColorType.I;
            case 't':
                return ColorType.T;
            case 's':
                return ColorType.S;
            case 'z':
                return ColorType.Z;
            case 'j':
                return ColorType.J;
            case 'l':
                return ColorType.L;
            case 'o':
                return ColorType.O;
            case 'x':
                return ColorType.Gray;
            case ' ':
                return ColorType.Empty;
            case '_':
                return ColorType.Empty;
            default:
                return ColorType.Gray;
        }
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
