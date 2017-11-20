package _implements.parity_based_pack.step1;

import core.mino.Piece;

public class DeltaLimitedMino {
    private final Piece piece;
    private final DeltaLimit deltaLimit;

    public static DeltaLimitedMino create(Piece piece, DeltaLimit deltaLimit) {
        return new DeltaLimitedMino(piece, deltaLimit);
    }

    private DeltaLimitedMino(Piece piece, DeltaLimit deltaLimit) {
        this.piece = piece;
        this.deltaLimit = deltaLimit;
    }

    public Piece getPiece() {
        return piece;
    }

    public DeltaLimit getDeltaLimit() {
        return deltaLimit;
    }

    @Override
    public String toString() {
        return "DeltaLimitedMino{" +
                "piece=" + piece +
                ", deltaLimit=" + deltaLimit +
                '}';
    }
}
