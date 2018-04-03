package searcher.pack.task;

import searcher.pack.memento.MinoFieldMemento;

import java.util.stream.Stream;

public interface TaskResultHelper {
    Stream<Result> fixResult(PackSearcher searcher, long innerFieldBoard, MinoFieldMemento nextMemento);
}
