package _experimental.allcomb.task;

import _experimental.allcomb.memento.MinoFieldMemento;

import java.util.concurrent.RecursiveTask;
import java.util.stream.Stream;

public abstract class MinoPackingTask extends RecursiveTask<Stream<Result>> {
    public abstract MinoFieldMemento getMemento();
}
