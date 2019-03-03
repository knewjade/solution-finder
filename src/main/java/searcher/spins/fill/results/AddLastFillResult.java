package searcher.spins.fill.results;

import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.AddLastsResult;
import searcher.spins.results.Result;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AddLastFillResult implements FillResult {
    private final FillResult prevFillResult;
    private final Result result;
    private final List<SimpleOriginalPiece> operations;
    private final List<SimpleOriginalPiece> tOperations;

    public AddLastFillResult(FillResult prevFillResult, List<SimpleOriginalPiece> operations) {
        this.prevFillResult = prevFillResult;
        this.result = AddLastsResult.create(prevFillResult.getLastResult(), operations);
        this.operations = operations;
        this.tOperations = operations.stream().filter(it -> it.getPiece() == Piece.T).collect(Collectors.toList());
    }

    @Override
    public Result getLastResult() {
        return result;
    }

    @Override
    public Stream<SimpleOriginalPiece> operationStream() {
        return Stream.concat(prevFillResult.operationStream(), operations.stream());
    }

    @Override
    public boolean containsT() {
        return prevFillResult.containsT() || !tOperations.isEmpty();
    }

    @Override
    public Stream<SimpleOriginalPiece> tOperationStream() {
        return Stream.concat(prevFillResult.tOperationStream(), tOperations.stream());
    }
}
