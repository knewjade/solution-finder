package _experimental.allcomb.task;

import _experimental.allcomb.*;
import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.memento.MinoFieldMemento;

import java.util.Set;
import java.util.stream.Stream;

public class Field4x10MinoPackingTask extends MinoPackingBaseTask {
    public Field4x10MinoPackingTask(ListUpSearcher searcher, MementoFilter mementoFilter, ColumnField innerField, MinoFieldMemento memento, int index) {
        super(searcher, mementoFilter, innerField, memento, index);
    }

    @Override
    protected MinoPackingBaseTask createTask(ListUpSearcher searcher, MementoFilter mementoFilter, ColumnField innerField, MinoFieldMemento memento, int index) {
        return new Field4x10MinoPackingTask(searcher, mementoFilter, innerField, memento, index);
    }

    @Override
    protected Stream<Result> fixResult(ColumnField lastOuterField, MinoFieldMemento nextMemento) {
        Bit bit = searcher.getBit();
        long board = lastOuterField.getBoard(0) >> bit.maxBit;
        if (board == bit.fillBoard) {
            if (mementoFilter.test(nextMemento))
                return Stream.of(createResult(nextMemento));
            return Stream.empty();
        } else {
            ColumnSmallField nextInnerField = new ColumnSmallField(board);
            Set<MinoField> minoFields = searcher.getSolutions().get(nextInnerField);

            return minoFields.stream()
                    .map(nextMemento::concat)
                    .filter(mementoFilter::test)
                    .map(this::createResult);
        }
    }
}
