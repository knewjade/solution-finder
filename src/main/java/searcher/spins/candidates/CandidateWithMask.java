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
    private final long onePieceFilledKeyWithoutT;

    public CandidateWithMask(Result result, SimpleOriginalPiece operationT, Field notAllowed) {
        this.result = result;
        this.tOperation = operationT;
        this.notAllowed = notAllowed;

        Field allMergedFieldWithoutT = result.getAllMergedField().freeze();
        allMergedFieldWithoutT.reduce(operationT.getMinoField());
        this.allMergedFieldWithoutT = allMergedFieldWithoutT;
        this.allMergedFilledLineWithoutT = allMergedFieldWithoutT.getFilledLine();
        this.onePieceFilledKeyWithoutT = result.getOnePieceFilledKey() & ~operationT.getUsingKey();
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

    public long getOnePieceFilledKeyWithoutT() {
        return onePieceFilledKeyWithoutT;
    }
}
