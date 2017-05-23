package _experimental.allcomb.task;

import core.column_field.ColumnField;
import _experimental.allcomb.PackSearcher;
import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.memento.MinoFieldMemento;

import java.util.stream.Stream;

public interface TaskResultHelper {
    Stream<Result> fixResult(PackSearcher searcher, ColumnField lastOuterField, MinoFieldMemento nextMemento);
}
