package searcher.spins.fill.results;

import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;
import searcher.spins.scaffold.results.ScaffoldResult;

import java.util.stream.Stream;

public class AddScaffoldFillResult implements FillResult {
    private final FillResult prevFillResult;
    private final ScaffoldResult scaffoldResult;

    public AddScaffoldFillResult(FillResult prevFillResult, ScaffoldResult scaffoldResult) {
        this.prevFillResult = prevFillResult;
        this.scaffoldResult = scaffoldResult;
    }

    @Override
    public Result getLastResult() {
        return scaffoldResult.getLastResult();
    }

    @Override
    public Stream<SimpleOriginalPiece> operationStream() {
        return prevFillResult.operationStream();
    }

    @Override
    public boolean containsT() {
        return prevFillResult.containsT();
    }

    @Override
    public Stream<SimpleOriginalPiece> tOperationStream() {
        return prevFillResult.tOperationStream();
    }
}
