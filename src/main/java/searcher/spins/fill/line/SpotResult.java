package searcher.spins.fill.line;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;

import java.util.List;

class SpotResult {
    private final List<SimpleOriginalPiece> operations;
    private final Field usingField;
    private final int startX;
    private final int usingBlockCount;

    SpotResult(List<SimpleOriginalPiece> operations, Field usingField, int startX, int usingBlockCount) {
        this.operations = operations;
        this.usingField = usingField;
        this.startX = startX;
        this.usingBlockCount = usingBlockCount;
    }

    List<SimpleOriginalPiece> getOperations() {
        return operations;
    }

    int getUsingBlockCount() {
        return usingBlockCount;
    }

    int getStartX() {
        return startX;
    }

    Field getUsingField() {
        return usingField;
    }
}
