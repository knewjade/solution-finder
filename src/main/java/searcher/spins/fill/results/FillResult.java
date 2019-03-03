package searcher.spins.fill.results;

import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;

import java.util.stream.Stream;

public interface FillResult {
    Result getLastResult();

    Stream<SimpleOriginalPiece> operationStream();

    boolean containsT();

    Stream<SimpleOriginalPiece> tOperationStream();
}
