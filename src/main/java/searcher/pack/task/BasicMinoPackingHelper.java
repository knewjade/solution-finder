package searcher.pack.task;

import searcher.pack.MinoField;
import searcher.pack.PackSearcher;
import searcher.pack.SizedBit;
import searcher.pack.memento.MementoFilter;
import searcher.pack.memento.MinoFieldMemento;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;

import java.util.List;
import java.util.stream.Stream;

public class BasicMinoPackingHelper implements TaskResultHelper {
    @Override
    public Stream<Result> fixResult(PackSearcher searcher, ColumnField lastOuterField, MinoFieldMemento nextMemento) {
        SizedBit sizedBit = searcher.getSizedBit();
        MementoFilter mementoFilter = searcher.getMementoFilter();
        long board = lastOuterField.getBoard(0) >> sizedBit.getMaxBitDigit();
        if (board == sizedBit.getFillBoard()) {
            if (mementoFilter.testLast(nextMemento))
                return Stream.of(createResult(nextMemento));
            return Stream.empty();
        } else {
            ColumnSmallField nextInnerField = new ColumnSmallField(board);
            List<MinoField> minoFields = searcher.getSolutions().get(nextInnerField);

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
