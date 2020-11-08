package entry.spin;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;

import java.util.List;
import java.util.stream.Stream;

public class SimpleOutputCandidate implements OutputCandidate {
    private final SimpleOriginalPiece operationT;
    private final Field allMergedField;
    private final List<SimpleOriginalPiece> operation;
    private final long allMergedFilledLine;
    private final Field allMergedFieldWithoutT;
    private final long allMergedFilledLineWithoutT;

    public SimpleOutputCandidate(
            SimpleOriginalPiece operationT,
            Field allMergedField,
            List<SimpleOriginalPiece> operation,
            long allMergedFilledLine,
            Field allMergedFieldWithoutT,
            long allMergedFilledLineWithoutT
    ) {
        this.operationT = operationT;
        this.allMergedField = allMergedField;
        this.operation = operation;
        this.allMergedFilledLine = allMergedFilledLine;
        this.allMergedFieldWithoutT = allMergedFieldWithoutT;
        this.allMergedFilledLineWithoutT = allMergedFilledLineWithoutT;
    }

    @Override
    public SimpleOriginalPiece getOperationT() {
        return operationT;
    }

    @Override
    public Field getAllMergedField() {
        return allMergedField;
    }

    @Override
    public Stream<SimpleOriginalPiece> operationStream() {
        return operation.stream();
    }

    @Override
    public long getAllMergedFilledLine() {
        return allMergedFilledLine;
    }

    @Override
    public Field getAllMergedFieldWithoutT() {
        return allMergedFieldWithoutT;
    }

    @Override
    public long getAllMergedFilledLineWithoutT() {
        return allMergedFilledLineWithoutT;
    }
}
