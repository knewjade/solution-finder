package core.field;

public class FieldFactory {
    public static Field createField(int maxHeight) {
        if (maxHeight < 6)
            return new SmallField();
        else if (maxHeight < 12)
            return new MiddleField();
        else if (maxHeight < 24)
            return new LargeField();
        throw new IllegalArgumentException("MaxHeight check too large. Should be equal or less than 12");
    }

    public static Field createField(String marks) {
        if (marks.length() % 10 != 0)
            throw new IllegalArgumentException("length of marks should be 'mod 10'");

        int maxY = marks.length() / 10;

        if (maxY <= 6)
            return createSmallField(marks);
        else if (maxY <= 12)
            return FieldFactory.createMiddleField(marks);
        else if (maxY <= 24)
            return FieldFactory.createLargeField(marks);

        throw new UnsupportedOperationException("Too large field height: " + maxY);
    }

    static SmallField createSmallField() {
        return new SmallField();
    }

    public static SmallField createSmallField(String marks) {
        if (marks.length() % 10 != 0)
            throw new IllegalArgumentException("length of marks should be 'mod 10'");

        int maxY = marks.length() / 10;
        SmallField field = new SmallField();
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < 10; x++) {
                char mark = marks.charAt((maxY - y - 1) * 10 + x);
                if (mark != ' ' && mark != '_')
                    field.setBlock(x, y);
            }
        }

        return field;
    }

    static Field createMiddleField() {
        return new MiddleField();
    }

    public static MiddleField createMiddleField(String marks) {
        if (marks.length() % 10 != 0)
            throw new IllegalArgumentException("length of marks should be 'mod 10'");

        int maxY = marks.length() / 10;
        MiddleField field = new MiddleField();
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < 10; x++) {
                char mark = marks.charAt((maxY - y - 1) * 10 + x);
                if (mark != ' ' && mark != '_')
                    field.setBlock(x, y);
            }
        }

        return field;
    }

    static Field createLargeField() {
        return new MiddleField();
    }

    static LargeField createLargeField(String marks) {
        if (marks.length() % 10 != 0)
            throw new IllegalArgumentException("length of marks should be 'mod 10'");

        int maxY = marks.length() / 10;
        LargeField field = new LargeField();
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < 10; x++) {
                char mark = marks.charAt((maxY - y - 1) * 10 + x);
                if (mark != ' ' && mark != '_')
                    field.setBlock(x, y);
            }
        }

        return field;
    }
}
