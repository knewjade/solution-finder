package searcher.spins.results;

import common.datastore.PieceCounter;
import core.field.Field;
import core.neighbor.SimpleOriginalPiece;

import java.util.stream.Stream;

public class AddLastResult extends Result {
    public static AddLastResult create(Result prev, SimpleOriginalPiece operation) {
        PieceCounter reminderPieceCounter = prev.getRemainderPieceCounter().removeAndReturnNew(PieceCounter.getSinglePieceCounter(operation.getPiece()));

        // すでに使われているブロックを計算
        Field usingField = prev.getUsingField().freeze();
        usingField.merge(operation.getMinoField());

        return new AddLastResult(prev, operation, reminderPieceCounter, usingField);
    }

    private final Result prev;
    private final SimpleOriginalPiece operation;
    private final PieceCounter reminderPieceCounter;
    private final Field usingField;
    private final Field allMergedField;
    private final long allMergedFilledLine;
    private final long usingKey;
    private final long onePieceFilledKey;

    private AddLastResult(Result prev, SimpleOriginalPiece operation, PieceCounter reminderPieceCounter, Field usingField) {
        super();
        this.prev = prev;
        this.operation = operation;
        this.reminderPieceCounter = reminderPieceCounter;
        this.usingField = usingField;

        Field allMergedField = getInitField().freeze();
        allMergedField.merge(usingField);
        this.allMergedField = allMergedField;
        this.allMergedFilledLine = allMergedField.getFilledLine();

        long prevUsingKey = prev.getUsingKey();
        this.usingKey = prevUsingKey | operation.getUsingKey();

        long currentOnePieceFilledKey = allMergedFilledLine & operation.getUsingKey() & ~prevUsingKey;
        this.onePieceFilledKey = prev.getOnePieceFilledKey() | currentOnePieceFilledKey;
    }

    @Override
    public Field getInitField() {
        return prev.getInitField();
    }

    @Override
    public Field getUsingField() {
        return usingField;
    }

    @Override
    public Field getAllMergedField() {
        return allMergedField;
    }

    @Override
    public PieceCounter getRemainderPieceCounter() {
        return reminderPieceCounter;
    }

    @Override
    public Stream<SimpleOriginalPiece> operationStream() {
        return Stream.concat(prev.operationStream(), Stream.of(operation));
    }

    @Override
    public int getNumOfUsingPiece() {
        return prev.getNumOfUsingPiece() + 1;
    }

    @Override
    public long getAllMergedFilledLine() {
        return allMergedFilledLine;
    }

    @Override
    public long getUsingKey() {
        return usingKey;
    }

    @Override
    public long getOnePieceFilledKey() {
        return onePieceFilledKey;
    }

    SimpleOriginalPiece getCurrentOperation() {
        return operation;
    }
}
