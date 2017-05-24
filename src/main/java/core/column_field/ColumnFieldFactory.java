package core.column_field;

import java.util.List;

public class ColumnFieldFactory {
    public static ColumnField createField(List<Long> boards) {
        switch (boards.size()) {
            case 0:
                return new ColumnSmallField();
            case 1:
                return new ColumnSmallField(boards.get(0));
        }
        throw new UnsupportedOperationException("Too much boards");
    }
}
