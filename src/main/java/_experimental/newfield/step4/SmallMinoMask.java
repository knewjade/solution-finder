package _experimental.newfield.step4;

import core.field.Field;
import core.field.SmallField;
import core.mino.Mino;

public class SmallMinoMask implements MinoMask {
    private final long low;
    private final int x;

    public SmallMinoMask(Mino mino, int y, long deleteKey) {
        SmallField field = new SmallField();
        this.x = -mino.getMinX();
        field.putMino(mino, x, y);
        field.insertWhiteLineWithKey(deleteKey);
        this.low = field.getBoard(0);
    }

    @Override
    public Field getMinoMask(int x) {
        assert this.x <= x : x;
        int slide = x - this.x;
        long newLow = low << slide;
        return new SmallField(newLow);
    }
}
