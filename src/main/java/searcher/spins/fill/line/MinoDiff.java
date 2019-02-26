package searcher.spins.fill.line;

import common.datastore.SimpleMinoOperation;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.Piece;
import core.srs.Rotate;

import java.util.Objects;

class MinoDiff {
    private static final int MAX_HEIGHT = 7;
    private static final int FILL_X = 3;
    private static final int FILL_Y = 3;

    private final SimpleMinoOperation operation;
    private final Field minoField;
    private final int blockCount;
    private final int leftMargin;
    private final int rightMargin;

    MinoDiff(Mino mino, int minXOnTarget, int dy, int blockCount) {
        this.operation = new SimpleMinoOperation(mino, FILL_X - minXOnTarget, FILL_Y - dy);
        this.blockCount = blockCount;

        Field minoField = FieldFactory.createField(MAX_HEIGHT);
        minoField.put(operation.getMino(), operation.getX(), operation.getY());
        this.minoField = minoField;

        this.leftMargin = minXOnTarget - mino.getMinX();
        this.rightMargin = mino.getMaxX() - minXOnTarget + 1 - blockCount;
    }

    Piece getPiece() {
        return operation.getPiece();
    }

    Rotate getRotate() {
        return operation.getRotate();
    }

    int calcCx(int minX) {
        return operation.getX() - FILL_X + minX;
    }

    int getY() {
        return operation.getY();
    }

    Field getMinoField() {
        return minoField;
    }

    int getBlockCount() {
        return blockCount;
    }

    int getLeftMargin() {
        return leftMargin;
    }

    int getRightMargin() {
        return rightMargin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinoDiff minoDiff = (MinoDiff) o;
        return operation.equals(minoDiff.operation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation);
    }

    @Override
    public String toString() {
        return "MinoDiff{" +
                "operation=" + operation +
                ", blockCount=" + blockCount +
                '}';
    }
}
