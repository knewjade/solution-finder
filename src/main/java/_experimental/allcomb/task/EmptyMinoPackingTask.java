package _experimental.allcomb.task;

import _experimental.allcomb.memento.MinoFieldMemento;

import java.util.stream.Stream;

public class EmptyMinoPackingTask extends MinoPackingTask {
    private static final MinoFieldMemento EMPTY_MEMENTO = new MinoFieldMemento();

    @Override
    protected Stream<Result> compute() {
        return Stream.empty();
    }

    @Override
    public MinoFieldMemento getMemento() {
        return EMPTY_MEMENTO;
    }
}
