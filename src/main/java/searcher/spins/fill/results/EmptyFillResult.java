package searcher.spins.fill.results;

import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;

import java.util.stream.Stream;

public class EmptyFillResult implements FillResult {
    private final Result result;

    public EmptyFillResult(Result result) {
        this.result = result;
    }

    @Override
    public Result getLastResult() {
        return result;
    }

    @Override
    public Stream<SimpleOriginalPiece> operationStream() {
        return Stream.empty();
    }

    @Override
    public boolean containsT() {
        return false;
    }

    @Override
    public Stream<SimpleOriginalPiece> tOperationStream() {
        return Stream.empty();
    }
}
