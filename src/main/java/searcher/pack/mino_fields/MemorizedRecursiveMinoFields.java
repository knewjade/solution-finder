package searcher.pack.mino_fields;

import searcher.pack.calculator.ConnectionsToListCallable;
import searcher.pack.mino_field.MinoField;
import searcher.pack.mino_field.RecursiveMinoField;

import java.util.List;
import java.util.stream.Stream;

public class MemorizedRecursiveMinoFields implements RecursiveMinoFields {
    private final ConnectionsToListCallable callable;
    private List<RecursiveMinoField> result;

    public MemorizedRecursiveMinoFields(ConnectionsToListCallable callable) {
        this.callable = callable;
    }

    @Override
    public Stream<? extends MinoField> stream() {
        return recursiveStream();
    }

    @Override
    public Stream<RecursiveMinoField> recursiveStream() {
        if (result == null) {
            result = callable.call();
        }
        return result.stream();
    }
}