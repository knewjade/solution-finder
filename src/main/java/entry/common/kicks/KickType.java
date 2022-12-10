package entry.common.kicks;

import core.mino.Piece;
import core.srs.Rotate;

import java.util.Objects;

public class KickType {
    private final Piece piece;
    private final Rotate rotateFrom;
    private final Rotate rotateTo;

    public KickType(Piece piece, Rotate rotateFrom, Rotate rotateTo) {
        this.piece = piece;
        this.rotateFrom = rotateFrom;
        this.rotateTo = rotateTo;
    }

    public Piece getPiece() {
        return piece;
    }

    public Rotate getRotateFrom() {
        return rotateFrom;
    }

    public Rotate getRotateTo() {
        return rotateTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KickType kickType = (KickType) o;
        return piece == kickType.piece && rotateFrom == kickType.rotateFrom && rotateTo == kickType.rotateTo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, rotateFrom, rotateTo);
    }
}
