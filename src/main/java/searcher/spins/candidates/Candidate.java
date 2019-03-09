package searcher.spins.candidates;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;

public class Candidate {
    private final Result result;
    private final SimpleOriginalPiece tOperation;
    private final Field allMergedFieldWithoutT;
    private final long allMergedFilledLineWithoutT;

    public Candidate(Result result, SimpleOriginalPiece tOperation) {
        this.result = result;
        this.tOperation = tOperation;

        Field allMergedFieldWithoutT = result.getAllMergedField().freeze();
        allMergedFieldWithoutT.reduce(tOperation.getMinoField());
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

    public long getAllMergedFilledLineWithoutT() {
        return allMergedFilledLineWithoutT;
    }
}
