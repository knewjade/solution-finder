package newfield.step3;

import core.mino.Mino;

public class MinoMaskFactory {
    public static MinoMask create(int maxHeight, Mino mino, int y, Long deleteKey) {
        assert 0 < maxHeight && maxHeight <= 24;
        if (maxHeight < 6)
            return new SmallMinoMask(mino, y, deleteKey);
        else if (maxHeight < 12)
            return new MiddleMinoMask(mino, y, deleteKey);
        return new LargeMinoMask(mino, y, deleteKey);
    }
}
