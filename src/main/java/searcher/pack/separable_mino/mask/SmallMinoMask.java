package searcher.pack.separable_mino.mask;

import core.field.Field;
import core.field.SmallField;
import core.mino.Mino;

// TODO: unittest: write
public class SmallMinoMask implements MinoMask {
    private final long low;
    private final int x;

    public SmallMinoMask(Mino mino, int y, long deleteKey) {
        SmallField field = new SmallField();
        this.x = -mino.getMinX();
        field.put(mino, x, y);
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
