package searcher.pack.memento;

import common.datastore.PieceCounter;
import common.datastore.OperationWithKey;
import searcher.pack.mino_field.MinoField;
import searcher.pack.separable_mino.SeparableMino;

import java.util.stream.Stream;

public interface MinoFieldMemento {
    MinoFieldMemento concat(MinoField minoField);

    MinoFieldMemento skip();

    PieceCounter getSumBlockCounter();

    Stream<OperationWithKey> getRawOperationsStream();

    boolean isConcat();

    Stream<OperationWithKey> getOperationsStream(int width);

    Stream<SeparableMino> getSeparableMinoStream(int width);

}
