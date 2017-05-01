package newfield.step4;

import core.field.Field;
import core.field.MiddleField;
import core.mino.Mino;

public class MiddleMinoMask implements MinoMask {
    private final long low;
    private final long high;
    private final int x;

    public MiddleMinoMask(Mino mino, int y, long deleteKey) {
        MiddleField field = new MiddleField();
        x = -mino.getMinX();
        field.putMino(mino, x, y);
        field.insertWhiteLineWithKey(deleteKey);
        this.low = field.getBoard(0);
        this.high = field.getBoard(1);
    }

    @Override
    public Field getMinoMask(int x) {
        assert this.x <= x : x;
        int slide = x - this.x;
        long newLow = low << slide;
        long newHigh = high << slide;
        return new MiddleField(newLow, newHigh);
    }
}
