package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.field.Field;
import searcher.pack.RecursiveMinoField;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class ConnectionsToListCallable implements Callable<List<RecursiveMinoField>> {
    private final ConnectionsToStreamCallable callable;

    ConnectionsToListCallable(SolutionsCalculator calculator, ColumnField initColumnField, ColumnField outerColumnField, Field wallField) {
        this.callable = new ConnectionsToStreamCallable(calculator, initColumnField, outerColumnField, wallField);
    }

    @Override
    public List<RecursiveMinoField> call() throws Exception {
        return callable.call().collect(Collectors.toList());
    }
}
