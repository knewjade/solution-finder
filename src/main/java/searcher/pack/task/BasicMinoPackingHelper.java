package searcher.pack.task;

import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.SizedBit;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.SolutionFilter;

import java.util.stream.Stream;

public class BasicMinoPackingHelper implements TaskResultHelper {
    @Override
    public Stream<Result> fixResult(PackSearcher searcher, ColumnField lastOuterField, MinoFieldMemento nextMemento) {
        SizedBit sizedBit = searcher.getSizedBit();
        SolutionFilter solutionFilter = searcher.getSolutionFilter();
        long fillBoard = sizedBit.getFillBoard();
        long innerFieldBoard = lastOuterField.getBoard(0) >> sizedBit.getMaxBitDigit();
        ColumnSmallField over = new ColumnSmallField(innerFieldBoard & ~fillBoard);

        long board = innerFieldBoard & fillBoard;
        if (board == fillBoard) {
            if (solutionFilter.testLast(nextMemento))
                return Stream.of(createResult(nextMemento));
            return Stream.empty();
        } else {
            ColumnSmallField nextInnerField = new ColumnSmallField(board);
            MinoFields minoFields = searcher.getSolutions().parse(nextInnerField);

            return minoFields.stream()
                    .filter(minoField -> over.canMerge(minoField.getOuterField()))
                    .map(nextMemento::concat)
                    .filter(solutionFilter::testLast)
                    .map(this::createResult);
        }
    }

    private Result createResult(MinoFieldMemento memento) {
        return new Result(memento);
    }
}
