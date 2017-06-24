package searcher.pack.calculator;

import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.Field;
import searcher.pack.connections.ColumnFieldConnections;

import java.util.stream.Stream;

public interface BasicReference {
    Field parseInnerField(ColumnField field);

    Field parseInvertedOuterField(ColumnField field);

    ColumnFieldConnections getConnections(ColumnField columnField);

    Stream<ColumnSmallField> getBasicFieldsSortedByBitCount();
}
