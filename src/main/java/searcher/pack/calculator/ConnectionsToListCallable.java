package searcher.pack.calculator;

import core.column_field.ColumnField;
import core.field.Field;
import searcher.pack.mino_field.RecursiveMinoField;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class ConnectionsToListCallable implements Callable<List<RecursiveMinoField>> {
    private final ConnectionsToStreamCallable callable;

    public ConnectionsToListCallable(SolutionsCalculator calculator, ColumnField initColumnField, ColumnField outerColumnField, Field wallField, ColumnField limitOuterField) {
        this.callable = new ConnectionsToStreamCallable(calculator, initColumnField, outerColumnField, wallField, limitOuterField);
    }

    @Override
    public List<RecursiveMinoField> call() {
        return callable.call().collect(Collectors.toList());
    }
}
