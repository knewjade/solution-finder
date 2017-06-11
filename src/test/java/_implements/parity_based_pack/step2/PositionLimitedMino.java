package _implements.parity_based_pack.step2;

import core.mino.Mino;

class PositionLimitedMino {
    static PositionLimitedMino create(Mino mino, PositionLimit positionLimit) {
        return new PositionLimitedMino(mino, positionLimit);
    }

    private final Mino mino;
    private final PositionLimit positionLimit;

    private PositionLimitedMino(Mino mino, PositionLimit positionLimit) {
        this.mino = mino;
        this.positionLimit = positionLimit;
    }

    Mino getMino() {
        return mino;
    }

    PositionLimit getPositionLimit() {
        return positionLimit;
    }

    @Override
    public String toString() {
        return "PositionLimitedMino{" +
                "mino=" + mino +
                ", positionLimit=" + positionLimit +
                '}';
    }
}
