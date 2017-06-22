package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.field.Field;
import searcher.pack.ColumnFieldConnections;
import searcher.pack.SeparableMinos;
import searcher.pack.mino_fields.RecursiveMinoFields;

public interface SolutionsCalculator {
    int getHeight();

    ColumnFieldConnections getConnections(ColumnField columnField);

    Field parseInvertedOuterField(ColumnField outerColumnField);

    SeparableMinos getSeparableMinos();

    RecursiveMinoFields getRecursiveMinoFields(ColumnField columnField);
}
