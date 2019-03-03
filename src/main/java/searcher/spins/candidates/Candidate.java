package searcher.spins.candidates;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;

public class Candidate {
    private final Result result;
    private final SimpleOriginalPiece tOperation;
    private final Field allMergedFieldWithoutT;

    public Candidate(Result result, SimpleOriginalPiece tOperation) {
        this.result = result;
        this.tOperation = tOperation;

        Field allMergedFieldWithoutT = result.getAllMergedField();
        allMergedFieldWithoutT.reduce(tOperation.getMinoField());
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
}
