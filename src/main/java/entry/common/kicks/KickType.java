package entry.common.kicks;

import core.mino.Piece;
import core.srs.Rotate;

import java.util.Objects;

class KickType {
    private final Piece piece;
    private final Rotate rotateFrom;
    private final Rotate rotateTo;

    KickType(Piece piece, Rotate rotateFrom, Rotate rotateTo) {
        this.piece = piece;
        this.rotateFrom = rotateFrom;
        this.rotateTo = rotateTo;
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
