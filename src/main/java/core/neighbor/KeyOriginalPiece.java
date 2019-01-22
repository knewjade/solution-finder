package core.neighbor;

import common.datastore.MinoOperation;
import core.mino.Mino;
import core.mino.Piece;
import core.srs.Rotate;

import java.util.Objects;

public class KeyOriginalPiece implements MinoOperation, Comparable<KeyOriginalPiece> {
    private final OriginalPiece originalPiece;
    private final int index;

    public KeyOriginalPiece(OriginalPiece originalPiece, int index) {
        this.originalPiece = originalPiece;
        this.index = index;
    }

    @Override
    public Piece getPiece() {
        return originalPiece.getPiece();
    }

    @Override
    public Rotate getRotate() {
        return originalPiece.getRotate();
    }

    @Override
    public int getX() {
        return originalPiece.getX();
    }

    @Override
    public int getY() {
        return originalPiece.getY();
    }

    @Override
    public Mino getMino() {
        return originalPiece.getMino();
    }

    public int getIndex() {
        return index;
    }

    public OriginalPiece getOriginalPiece() {
        return originalPiece;
    }

    @Override
    public int compareTo(KeyOriginalPiece o) {
        return Integer.compare(index, o.index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyOriginalPiece that = (KeyOriginalPiece) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    @Override
    public String toString() {
        return String.format("KeyOriginalPiece{%d;%s}", index, originalPiece);
    }
}
