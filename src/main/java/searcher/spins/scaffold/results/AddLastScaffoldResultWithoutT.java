package searcher.spins.scaffold.results;

import common.datastore.PieceCounter;
import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.AddLastResult;
import searcher.spins.results.Result;

import java.util.List;
import java.util.stream.Stream;

public class AddLastScaffoldResultWithoutT extends ScaffoldResultWithoutT {
    private final ScaffoldResult prev;
    private final SimpleOriginalPiece operation;
    private final Result result;
    private final Field notAllowed;
    private final int numOfUsingPiece;
    private final List<SimpleOriginalPiece> airOperations;

    public AddLastScaffoldResultWithoutT(ScaffoldResultWithoutT prev, SimpleOriginalPiece operation) {
        super();
        this.prev = prev;
        this.operation = operation;
        this.result = AddLastResult.create(prev.getLastResult(), operation);
        this.notAllowed = result.getAllMergedField();
        this.numOfUsingPiece = prev.getNumOfUsingPiece() + 1;
        this.airOperations = ScaffoldResultWithoutT.extractAirOperations(result, Stream.concat(prev.getAirOperations().stream(), Stream.of(operation)));
    }

    @Override
    public PieceCounter getReminderPieceCounter() {
        return result.getRemainderPieceCounter();
    }

    @Override
    public int getNumOfUsingPiece() {
        return numOfUsingPiece;
    }

    @Override
    public boolean canPut(SimpleOriginalPiece piece) {
        return notAllowed.canMerge(piece.getMinoField());
    }

    @Override
    public Stream<Long> toKeyStream() {
        return Stream.concat(prev.toKeyStream(), Stream.of(operation.toUniqueKey()));
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
        return Stream.concat(prev.targetOperationStream(), Stream.of(operation));
    }
}
