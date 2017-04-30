package newfield.step2;

import core.mino.Mino;
import newfield.step3.MinoMask;
import newfield.step3.MinoMaskFactory;

class DeleteKey {
    public static DeleteKey create(Mino mino, long deleteKey, int lowerY, int upperY) {
        return new DeleteKey(mino, deleteKey, lowerY, upperY);
    }

    private final Long deleteKey;
    private final int y;
    private final MinoMask minoMask;

    private DeleteKey(Mino mino, Long deleteKey, int lowerY, int upperY) {
        this.deleteKey = deleteKey;
        this.y = lowerY - mino.getMinY();

        int maxHeight = upperY + 1;
        this.minoMask = MinoMaskFactory.create(maxHeight, mino, y, deleteKey);
    }

    Long getDeleteKey() {
        return deleteKey;
    }

    int getY() {
        return y;
    }

    MinoMask getMinoMask() {
        return minoMask;
    }
}
