package searcher.spins.candidates;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;

public interface Candidate {
    Result getResult();

    SimpleOriginalPiece getOperationT();

    Field getAllMergedFieldWithoutT();

    long getAllMergedFilledLineWithoutT();
}
