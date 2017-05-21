package _experimental.allcomb.task;

import _experimental.allcomb.memento.MinoFieldMemento;

public class Result {
    private final MinoFieldMemento memento;

    public Result(MinoFieldMemento memento) {
//            System.out.println(memento.getRawOperations().size());
        this.memento = memento;
    }

    public MinoFieldMemento getMemento() {
        return memento;
    }
}
