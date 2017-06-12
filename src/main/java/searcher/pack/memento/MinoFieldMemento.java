package searcher.pack.memento;

import common.datastore.OperationWithKey;
import searcher.pack.MinoField;

import java.util.stream.Stream;

public interface MinoFieldMemento {
    MinoFieldMemento concat(MinoField minoField);

    MinoFieldMemento skip();

    long getSumBlockCounter();

    Stream<OperationWithKey> getRawOperationsStream();

    boolean isConcat();

    int getIndex();

    Stream<OperationWithKey> getOperationsStream(int width);
}
