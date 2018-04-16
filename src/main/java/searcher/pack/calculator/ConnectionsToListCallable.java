package searcher.pack.calculator;

import core.column_field.ColumnField;
import searcher.pack.mino_field.RecursiveMinoField;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectionsToListCallable implements Callable<List<RecursiveMinoField>> {
    private final ConnectionsToStreamCallable callable;

    public ConnectionsToListCallable(SolutionsCalculator calculator, ColumnField initColumnField, ColumnField outerColumnField, ColumnField limitOuterField) {
        this.callable = new ConnectionsToStreamCallable(calculator, initColumnField, outerColumnField, limitOuterField);
    }

    @Override
    public List<RecursiveMinoField> call() {
        Stream<RecursiveMinoField> call = callable.call();

        List<RecursiveMinoField> list = call.collect(Collectors.toList());

        return list;
    }
}
