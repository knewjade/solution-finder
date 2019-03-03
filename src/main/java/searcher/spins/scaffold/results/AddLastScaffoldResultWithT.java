package searcher.spins.scaffold.results;

import common.datastore.PieceCounter;
import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.AddLastResult;
import searcher.spins.results.Result;

import java.util.List;
import java.util.stream.Stream;

public class AddLastScaffoldResultWithT extends ScaffoldResultWithT {
    private final ScaffoldResultWithT prev;
    private final SimpleOriginalPiece operation;
    private final Result result;
    private final int numOfUsingPiece;
    private final List<SimpleOriginalPiece> airOperations;
    private final Field notAllowed;

    public AddLastScaffoldResultWithT(ScaffoldResultWithT prev, SimpleOriginalPiece operation) {
        super();
        this.prev = prev;
        this.operation = operation;
        this.result = AddLastResult.create(prev.getLastResult(), operation);
        this.numOfUsingPiece = prev.getNumOfUsingPiece() + 1;
        this.airOperations = ScaffoldResultWithoutT.extractAirOperations(result, Stream.concat(prev.getAirOperations().stream(), Stream.of(operation)));

        Field freezeNotAllowed = prev.getNotAllowed().freeze();
        freezeNotAllowed.merge(operation.getMinoField());
        this.notAllowed = freezeNotAllowed;
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

    @Override
    public SimpleOriginalPiece getOperationT() {
        return prev.getOperationT();
    }

    @Override
    Field getNotAllowed() {
        return notAllowed;
    }
}
