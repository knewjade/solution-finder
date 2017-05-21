package _experimental.allcomb.task;

import _experimental.allcomb.*;
import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.memento.MinoFieldMemento;

import java.util.Set;
import java.util.stream.Stream;

public class BasicMinoPackingHelper implements TaskResultHelper {
    @Override
    public Stream<Result> fixResult(ListUpSearcher searcher, MementoFilter mementoFilter, ColumnField lastOuterField, MinoFieldMemento nextMemento) {
        Bit bit = searcher.getBit();
        long board = lastOuterField.getBoard(0) >> bit.maxBit;
        if (board == bit.fillBoard) {
            if (mementoFilter.testLast(nextMemento))
                return Stream.of(createResult(nextMemento));
            return Stream.empty();
        } else {
            ColumnSmallField nextInnerField = new ColumnSmallField(board);
            Set<MinoField> minoFields = searcher.getSolutions().get(nextInnerField);

            return minoFields.stream()
                    .map(nextMemento::concat)
                    .filter(mementoFilter::testLast)
                    .map(this::createResult);
        }
    }

    private Result createResult(MinoFieldMemento memento) {
        return new Result(memento);
    }
}
