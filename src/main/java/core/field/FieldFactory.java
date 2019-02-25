package core.field;

public class FieldFactory {
    public static Field createField(int maxHeight) {
        if (maxHeight <= 6)
            return new SmallField();
        else if (maxHeight <= 12)
            return new MiddleField();
        else if (maxHeight <= 24)
            return new LargeField();
        throw new IllegalArgumentException("Field height should be equal or less than 24: height=" + maxHeight);
    }

    public static Field createField(String marks, int maxHeight) {
        Field field = createField(maxHeight);
        field.merge(createField(marks));
        return field;
    }

    public static Field createField(String marks) {
        if (marks.length() % 10 != 0)
            throw new IllegalArgumentException("length of marks should be 'mod 10'");

        int maxY = marks.length() / 10;

        if (maxY <= 6)
            return createSmallField(marks, true);
        else if (maxY <= 12)
            return FieldFactory.createMiddleField(marks);

        throw new UnsupportedOperationException("Too large field height: " + maxY);
    }

    public static SmallField createSmallField() {
        return new SmallField();
    }

    public static SmallField createSmallField(String marks) {
        return createSmallField(marks, true);
    }

    private static SmallField createSmallField(String marks, boolean isBlock) {
        if (60 < marks.length())
            throw new IllegalArgumentException("length of marks should be <= 60");

        if (marks.length() % 10 != 0)
            throw new IllegalArgumentException("length of marks should be 'mod 10'");

        int maxY = marks.length() / 10;
        SmallField field = new SmallField();
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < 10; x++) {
                char mark = marks.charAt((maxY - y - 1) * 10 + x);
                if (mark != ' ' && mark != '_') {
                    if (isBlock)
                        field.setBlock(x, y);
                } else {
                    if (!isBlock)
                        field.setBlock(x, y);
                }
            }
        }

        return field;
    }

    public static MiddleField createMiddleField() {
        return new MiddleField();
    }

    public static MiddleField createMiddleField(String marks) {
        return createMiddleField(marks, true);
    }

    private static MiddleField createMiddleField(String marks, boolean isBlock) {
        if (120 < marks.length())
            throw new IllegalArgumentException("length of marks should be <= 120");


        if (marks.length() % 10 != 0)
            throw new IllegalArgumentException("length of marks should be 'mod 10'");

        int maxY = marks.length() / 10;
        MiddleField field = new MiddleField();
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < 10; x++) {
                char mark = marks.charAt((maxY - y - 1) * 10 + x);
                if (mark != ' ' && mark != '_') {
                    if (isBlock)
                        field.setBlock(x, y);
                } else {
                    if (!isBlock)
                        field.setBlock(x, y);
                }
            }
        }

        return field;
    }

    public static LargeField createLargeField() {
        return new LargeField();
    }

    public static LargeField createLargeField(String marks) {
        return createLargeField(marks, true);
    }

    private static LargeField createLargeField(String marks, boolean isBlock) {
        if (240 < marks.length())
            throw new IllegalArgumentException("length of marks should be <= 240");


        if (marks.length() % 10 != 0)
            throw new IllegalArgumentException("length of marks should be 'mod 10'");

        int maxY = marks.length() / 10;
        LargeField field = new LargeField();
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < 10; x++) {
                char mark = marks.charAt((maxY - y - 1) * 10 + x);
                if (mark != ' ' && mark != '_') {
                    if (isBlock)
                        field.setBlock(x, y);
                } else {
                    if (!isBlock)
                        field.setBlock(x, y);
                }
            }
        }

        return field;
    }

    public static Field createInverseField(String marks) {
        if (marks.length() % 10 != 0)
            throw new IllegalArgumentException("length of marks should be 'mod 10'");

        int maxY = marks.length() / 10;

        if (maxY <= 6)
            return createSmallField(marks, false);
        else if (maxY <= 12)
            return createMiddleField(marks, false);

        throw new UnsupportedOperationException("Too large field height: " + maxY);
    }
}
