package searcher.spins.candidates;

import core.field.Field;
import core.field.FieldFactory;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;

public class CandidateWithMask {
    private final Result result;
    private final SimpleOriginalPiece tOperation;
    private final Field notAllowed;
    private final Field allMergedFieldWithoutT;

    public CandidateWithMask(Result result, SimpleOriginalPiece operationT, Field notAllowed) {
        this.result = result;
        this.tOperation = operationT;
        this.notAllowed = notAllowed;

        Field allMergedFieldWithoutT = result.getAllMergedField();
        allMergedFieldWithoutT.reduce(operationT.getMinoField());
        this.allMergedFieldWithoutT = allMergedFieldWithoutT;
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
}
