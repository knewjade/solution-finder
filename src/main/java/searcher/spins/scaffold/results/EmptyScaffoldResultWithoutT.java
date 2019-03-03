package searcher.spins.scaffold.results;

import common.datastore.PieceCounter;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;

import java.util.List;
import java.util.stream.Stream;

public class EmptyScaffoldResultWithoutT extends ScaffoldResultWithoutT {
    private final Result result;
    private final List<SimpleOriginalPiece> operations;
    private final List<SimpleOriginalPiece> airOperations;

    // operations = 空中に浮かないようにするミノの一覧
    public EmptyScaffoldResultWithoutT(Result result, List<SimpleOriginalPiece> operations) {
        super();
        this.result = result;
        this.operations = operations;
        this.airOperations = ScaffoldResultWithoutT.extractAirOperations(result, operations.stream());
    }

    @Override
    public PieceCounter getReminderPieceCounter() {
        return result.getRemainderPieceCounter();
    }

    @Override
    public int getNumOfUsingPiece() {
        return result.getNumOfUsingPiece();
    }

    @Override
    public boolean canPut(SimpleOriginalPiece piece) {
        return result.getAllMergedField().canMerge(piece.getMinoField());
    }

    @Override
    public Stream<Long> toKeyStream() {
        return Stream.empty();
    }

    @Override
    public Result getLastResult() {
        return result;
    }

    @Override
    public boolean existsAllOnGround() {
        return airOperations.isEmpty();
    }

    @Override
    public List<SimpleOriginalPiece> getAirOperations() {
        return airOperations;
    }

    @Override
    public Stream<SimpleOriginalPiece> targetOperationStream() {
        return operations.stream();
    }
}
