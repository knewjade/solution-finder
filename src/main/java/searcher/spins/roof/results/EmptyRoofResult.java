package searcher.spins.roof.results;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.candidates.CandidateWithMask;
import searcher.spins.results.Result;

import java.util.stream.Stream;

public class EmptyRoofResult extends RoofResult {
    private final CandidateWithMask candidateWithMask;
    private final Field notAllowedWithT;

    public EmptyRoofResult(CandidateWithMask candidateWithMask) {
        this.candidateWithMask = candidateWithMask;

        Field notAllowed = candidateWithMask.getNotAllowed().freeze();
        notAllowed.merge(candidateWithMask.getOperationT().getMinoField());
        this.notAllowedWithT = notAllowed;
    }

    @Override
    public Result getLastResult() {
        return candidateWithMask.getResult();
    }

    @Override
    public int getNumOfUsingPiece() {
        return this.getLastResult().getNumOfUsingPiece();
    }

    @Override
    public Stream<SimpleOriginalPiece> targetOperationStream() {
        return Stream.empty();
    }

    @Override
    public SimpleOriginalPiece getOperationT() {
        return candidateWithMask.getOperationT();
    }

    @Override
    public Field getAllMergedFieldWithoutT() {
        return candidateWithMask.getAllMergedFieldWithoutT();
    }

    @Override
    public Field getNotAllowedWithT() {
        return notAllowedWithT;
    }

    @Override
    public Stream<Long> toKeyStream() {
        return Stream.empty();
    }
}
