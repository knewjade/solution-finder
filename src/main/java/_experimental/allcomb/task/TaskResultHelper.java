package _experimental.allcomb.task;

import _experimental.allcomb.ColumnField;
import _experimental.allcomb.ListUpSearcher;
import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.memento.MinoFieldMemento;

import java.util.stream.Stream;

public interface TaskResultHelper {
    Stream<Result> fixResult(ListUpSearcher searcher, MementoFilter mementoFilter, ColumnField lastOuterField, MinoFieldMemento nextMemento);
}
