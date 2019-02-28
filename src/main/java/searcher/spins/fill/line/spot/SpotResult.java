package searcher.spins.fill.line.spot;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;

import java.util.List;

public class SpotResult {
    private final List<SimpleOriginalPiece> operations;
    private final Field usingField;
    private final int startX;
    private final int usingBlockCount;
    private final int rightX;
    private final int minY;

    SpotResult(List<SimpleOriginalPiece> operations, Field usingField, int startX, int usingBlockCount, int rightX, int minY) {
        this.operations = operations;
        this.usingField = usingField;
        this.startX = startX;
        this.usingBlockCount = usingBlockCount;
        this.rightX = rightX;
        this.minY = minY;
    }

    public List<SimpleOriginalPiece> getOperations() {
        return operations;
    }

    int getUsingBlockCount() {
        return usingBlockCount;
    }

    public int getStartX() {
        return startX;
    }

    public Field getUsingField() {
        return usingField;
    }

    // マージンを含めた最も右のブロックのX座標
    public int getRightX() {
        return rightX;
    }

    public int getMinY() {
        return minY;
    }
}
