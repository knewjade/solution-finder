package core.column_field;

import java.util.List;

public class ColumnFieldFactory {
    public static ColumnSmallField createField() {
        return new ColumnSmallField();
    }

    public static ColumnSmallField createField(long board) {
        return new ColumnSmallField(board);
    }

    public static ColumnSmallField createField(List<Long> boards) {
        switch (boards.size()) {
            case 0:
                return new ColumnSmallField();
            case 1:
                return new ColumnSmallField(boards.get(0));
        }
        throw new UnsupportedOperationException("Too much boards");
    }

    public static ColumnSmallField createField(String marks, int height) {
        int max = height * 6;
        if (max < marks.length())
            throw new IllegalArgumentException(String.format("length of marks should be < %d in height %d", max, height));

        if (marks.length() % height != 0)
            throw new IllegalArgumentException(String.format("length of marks should be 'mod %d'", height));

        ColumnSmallField field = new ColumnSmallField();
        int width = marks.length() / height;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char mark = marks.charAt((height - y - 1) * width + x);
                if (mark != ' ' && mark != '_')
                    field.setBlock(x, y, height);
            }
        }

        return field;
    }
}
