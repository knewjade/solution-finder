package newfield.step2;

import core.mino.Mino;

public class FullLimitedMino {
    private final Mino mino;
    private final PositionLimit positionLimit;
    private final DeleteKey deleteKey;

    public static FullLimitedMino create(Mino mino, PositionLimit positionLimit, DeleteKey deleteKey) {
        return new FullLimitedMino(mino, positionLimit, deleteKey);
    }

    private FullLimitedMino(Mino mino, PositionLimit positionLimit, DeleteKey deleteKey) {
        this.mino = mino;
        this.positionLimit = positionLimit;
        this.deleteKey = deleteKey;
    }
}
