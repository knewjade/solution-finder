package searcher.pack.calculator;

import core.column_field.ColumnField;
import core.field.Field;
import searcher.pack.connections.ColumnFieldConnections;
import searcher.pack.SeparableMinos;
import searcher.pack.mino_fields.RecursiveMinoFields;

public interface SolutionsCalculator {
    int getHeight();

    boolean isFilled(ColumnField columnField);

    ColumnFieldConnections getConnections(ColumnField columnField);

    Field parseInvertedOuterField(ColumnField outerColumnField);

    SeparableMinos getSeparableMinos();

    RecursiveMinoFields getRecursiveMinoFields(ColumnField columnField);
}
