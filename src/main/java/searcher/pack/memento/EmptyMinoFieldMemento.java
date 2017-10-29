package searcher.pack.memento;

import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import searcher.pack.mino_field.MinoField;
import searcher.pack.separable_mino.SeparableMino;

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
    public BlockCounter getSumBlockCounter() {
        return BlockCounter.EMPTY;
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

    @Override
    public Stream<SeparableMino> getSeparableMinoStream(int width) {
        return Stream.empty();
    }
}
