package searcher.spins.results;

import common.datastore.PieceCounter;
import core.field.Field;
import core.neighbor.SimpleOriginalPiece;

import java.util.List;
import java.util.stream.Stream;

public class AddLastsResult extends Result {
    public static AddLastsResult create(Result prev, List<SimpleOriginalPiece> operations) {
        // 残りのミノ
        PieceCounter usingPiceCounter = new PieceCounter(operations.stream().map(SimpleOriginalPiece::getPiece));
        PieceCounter reminderPieceCounter = prev.getRemainderPieceCounter().removeAndReturnNew(usingPiceCounter);

        // すでに使われているブロックを計算
        Field usingField = prev.getUsingField().freeze();
        long prevUsingKey = prev.getUsingKey();
        long allUsingKey = prevUsingKey;
        long duplicateUsingKey = 0L;
        for (SimpleOriginalPiece operation : operations) {
            usingField.merge(operation.getMinoField());

            long usingKey = operation.getUsingKey();
            duplicateUsingKey |= allUsingKey & usingKey;
            allUsingKey |= usingKey;
        }

        long newOnePieceUsingKey = allUsingKey - (duplicateUsingKey | prevUsingKey);  // 新しく追加された、1ミノしかないライン
        return new AddLastsResult(prev, operations, reminderPieceCounter, usingField, allUsingKey, newOnePieceUsingKey);
    }

    private final Result prev;
    private final List<SimpleOriginalPiece> operations;
    private final PieceCounter reminderPieceCounter;
    private final Field usingField;
    private final Field allMergedField;
    private final long allMergedFilledLine;
    private final long usingKey;
    private final long onePieceFilledKey;

    private AddLastsResult(Result prev, List<SimpleOriginalPiece> operations, PieceCounter reminderPieceCounter, Field usingField, long usingKey, long newOnePieceUsingKey) {
        super();
        this.prev = prev;
        this.operations = operations;
        this.reminderPieceCounter = reminderPieceCounter;
        this.usingField = usingField;

        Field allMergedField = getInitField().freeze();
        allMergedField.merge(usingField);
        this.allMergedField = allMergedField;
        this.allMergedFilledLine = allMergedField.getFilledLine();

        this.usingKey = usingKey;
        this.onePieceFilledKey = prev.getOnePieceFilledKey() | (allMergedFilledLine & newOnePieceUsingKey);
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
        return Stream.concat(prev.operationStream(), operations.stream());
    }

    @Override
    public int getNumOfUsingPiece() {
        return prev.getNumOfUsingPiece() + operations.size();
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
}
