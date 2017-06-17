package searcher.pack.task;

import searcher.pack.memento.MinoFieldMemento;

public class Result {
    private final MinoFieldMemento memento;

    public Result(MinoFieldMemento memento) {
        this.memento = memento;
    }

    public MinoFieldMemento getMemento() {
        return memento;
    }
}
