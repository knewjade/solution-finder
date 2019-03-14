package searcher.spins.scaffold.results;

import common.datastore.PieceCounter;
import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.candidates.CandidateWithMask;
import searcher.spins.results.Result;

import java.util.List;
import java.util.stream.Stream;

public class EmptyScaffoldResultWithTFromCandidate extends ScaffoldResultWithT {
    private final Result result;
    private final CandidateWithMask candidateWithMask;
    private final List<SimpleOriginalPiece> operations;
    private final List<SimpleOriginalPiece> airOperations;
    private final Field notAllowed;

    // operations = 空中に浮かないようにするミノの一覧
    public EmptyScaffoldResultWithTFromCandidate(CandidateWithMask candidateWithMask, List<SimpleOriginalPiece> operations) {
        super();
        this.result = candidateWithMask.getResult();
        this.candidateWithMask = candidateWithMask;
        this.operations = operations;
        this.airOperations = ScaffoldResultWithT.extractAirOperations(result, candidateWithMask.getOperationT(), operations.stream());

        Field freezeNotAllowed = result.getAllMergedField().freeze();
        freezeNotAllowed.merge(candidateWithMask.getNotAllowed());
        this.notAllowed = freezeNotAllowed;
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
        return notAllowed.canMerge(piece.getMinoField());
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

    @Override
    public SimpleOriginalPiece getOperationT() {
        return candidateWithMask.getOperationT();
    }

    @Override
    Field getNotAllowed() {
        return notAllowed;
    }
}
