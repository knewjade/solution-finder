package searcher.pack.memento;

import common.datastore.OperationWithKey;
import searcher.pack.mino_field.MinoField;

import java.util.stream.Stream;

public interface MinoFieldMemento {
    MinoFieldMemento concat(MinoField minoField);

    MinoFieldMemento skip();

    long getSumBlockCounter();

    Stream<OperationWithKey> getRawOperationsStream();

    boolean isConcat();

    Stream<OperationWithKey> getOperationsStream(int width);
}
