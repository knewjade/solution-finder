package searcher.pack.mino_fields;

import searcher.pack.mino_field.MinoField;
import searcher.pack.mino_field.RecursiveMinoField;
import searcher.pack.calculator.ConnectionsToStreamCallable;

import java.util.stream.Stream;

public class OnDemandRecursiveMinoFields implements RecursiveMinoFields {
    private final ConnectionsToStreamCallable callable;

    public OnDemandRecursiveMinoFields(ConnectionsToStreamCallable callable) {
        this.callable = callable;
    }

    @Override
    public Stream<? extends MinoField> stream() {
        return recursiveStream();
    }

    @Override
    public Stream<RecursiveMinoField> recursiveStream() {
        try {
            return callable.call();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("internal error: task cannot execute");
        }
    }
}