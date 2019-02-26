package searcher.spins.results;

import common.datastore.PieceCounter;
import core.field.Field;
import core.field.FieldFactory;
import core.neighbor.SimpleOriginalPiece;

import java.util.stream.Stream;

public class EmptyResult extends Result {
    private final Field initField;
    private final PieceCounter reminderPieceCounter;
    private final int maxHeight;

    public EmptyResult(Field initField, PieceCounter reminderPieceCounter, int maxHeight) {
        this.initField = initField;
        this.reminderPieceCounter = reminderPieceCounter;
        this.maxHeight = maxHeight;
    }

    @Override
    Field getUsingField() {
        return FieldFactory.createField(maxHeight);
    }

    @Override
    public Field getAllMergedField() {
        return initField;
    }

    @Override
    public PieceCounter getRemainderPieceCounter() {
        return reminderPieceCounter;
    }

    @Override
    public Stream<SimpleOriginalPiece> operationStream() {
        return Stream.empty();
    }

    @Override
    public int getNumOfUsingPiece() {
        return 0;
    }

    @Override
    public Field getInitField() {
        return initField;
    }
}
