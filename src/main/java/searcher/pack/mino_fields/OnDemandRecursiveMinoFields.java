package searcher.pack.mino_fields;

import searcher.pack.MinoField;
import searcher.pack.RecursiveMinoField;
import searcher.pack.solutions.ConnectionsToStreamCallable;

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