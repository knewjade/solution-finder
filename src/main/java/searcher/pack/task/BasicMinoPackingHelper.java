package searcher.pack.task;

import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnSmallField;
import searcher.pack.SizedBit;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.MinoFields;

import java.util.stream.Stream;

public class BasicMinoPackingHelper implements TaskResultHelper {
    @Override
    public Stream<Result> fixResult(PackSearcher searcher, long innerFieldBoard, MinoFieldMemento nextMemento) {
        SizedBit sizedBit = searcher.getSizedBit();
        SolutionFilter solutionFilter = searcher.getSolutionFilter();
        long fillBoard = sizedBit.getFillBoard();
        ColumnSmallField over = ColumnFieldFactory.createField(innerFieldBoard & ~fillBoard);

        long board = innerFieldBoard & fillBoard;
        if (board == fillBoard) {
            if (solutionFilter.testLast(nextMemento))
                return Stream.of(createResult(nextMemento));
            return Stream.empty();
        } else {
            ColumnSmallField nextInnerField = ColumnFieldFactory.createField(board);
            int lastIndex = searcher.getLastIndex();
            MinoFields minoFields = searcher.getSolutions(lastIndex).parse(nextInnerField);

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
