package searcher.spins.candidates;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;

public class CandidateWithMask {
    private final Result result;
    private final SimpleOriginalPiece tOperation;
    private final Field notAllowed;
    private final Field allMergedFieldWithoutT;
    private final long allMergedFilledLineWithoutT;

    public CandidateWithMask(Result result, SimpleOriginalPiece operationT, Field notAllowed) {
        this.result = result;
        this.tOperation = operationT;
        this.notAllowed = notAllowed;

        Field allMergedFieldWithoutT = result.freezeAllMergedField();
        allMergedFieldWithoutT.reduce(operationT.getMinoField());
        this.allMergedFieldWithoutT = allMergedFieldWithoutT;
        this.allMergedFilledLineWithoutT = allMergedFieldWithoutT.getFilledLine();
    }

    public Result getResult() {
        return result;
    }

    public SimpleOriginalPiece getOperationT() {
        return tOperation;
    }

    public Field getAllMergedFieldWithoutT() {
        return allMergedFieldWithoutT;
    }

    public Field getNotAllowed() {
        return notAllowed;
    }

    public long getAllMergedFilledLineWithoutT() {
        return allMergedFilledLineWithoutT;
    }
}
