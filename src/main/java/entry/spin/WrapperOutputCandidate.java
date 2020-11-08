package entry.spin;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.candidates.Candidate;

import java.util.stream.Stream;

public class WrapperOutputCandidate implements OutputCandidate {
    private final Candidate candidate;

    public WrapperOutputCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    @Override
    public SimpleOriginalPiece getOperationT() {
        return candidate.getOperationT();
    }

    @Override
    public Field getAllMergedField() {
        return candidate.getResult().getAllMergedField();
    }

    @Override
    public Stream<SimpleOriginalPiece> operationStream() {
        return candidate.getResult().operationStream();
    }

    @Override
    public long getAllMergedFilledLine() {
        return candidate.getResult().getAllMergedFilledLine();
    }

    @Override
    public Field getAllMergedFieldWithoutT() {
        return candidate.getAllMergedFieldWithoutT();
    }

    @Override
    public long getAllMergedFilledLineWithoutT() {
        return candidate.getAllMergedFilledLineWithoutT();
    }
}
