package searcher.spins.candidates;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;

public class SimpleCandidate implements Candidate {
    private final Result result;
    private final SimpleOriginalPiece tOperation;
    private final Field allMergedFieldWithoutT;
    private final long allMergedFilledLineWithoutT;

    public SimpleCandidate(Result result, SimpleOriginalPiece tOperation) {
        this.result = result;
        this.tOperation = tOperation;

        Field allMergedFieldWithoutT = result.getAllMergedField().freeze();
        allMergedFieldWithoutT.reduce(tOperation.getMinoField());
        this.allMergedFieldWithoutT = allMergedFieldWithoutT;
        this.allMergedFilledLineWithoutT = allMergedFieldWithoutT.getFilledLine();
    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public SimpleOriginalPiece getOperationT() {
        return tOperation;
    }

    @Override
    public Field getAllMergedFieldWithoutT() {
        return allMergedFieldWithoutT;
    }

    @Override
    public long getAllMergedFilledLineWithoutT() {
        return allMergedFilledLineWithoutT;
    }
}
