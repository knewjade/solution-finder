package searcher.pack.memento;

import common.datastore.OperationWithKey;
import searcher.pack.MinoField;

import java.util.stream.Stream;

public class EmptyMinoFieldMemento implements MinoFieldMemento {
    @Override
    public MinoFieldMemento concat(MinoField minoField) {
        return new RecursiveMinoFieldMemento(minoField, null, minoField != null ? false : null, 1);
    }

    @Override
    public MinoFieldMemento skip() {
        return concat(null);
    }

    @Override
    public long getSumBlockCounter() {
        return 0L;
    }

    @Override
    public Stream<OperationWithKey> getRawOperationsStream() {
        return Stream.empty();
    }

    @Override
    public boolean isConcat() {
        return false;
    }

    @Override
    public Stream<OperationWithKey> getOperationsStream(int width) {
        return Stream.empty();
    }
}
