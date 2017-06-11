package searcher.pack.separable_mino.mask;

import core.mino.Mino;

// TODO: unittest: write
public class MinoMaskFactory {
    public static MinoMask create(int maxHeight, Mino mino, int y, Long deleteKey) {
        assert 0 < maxHeight && maxHeight <= 12 : maxHeight;
        if (maxHeight <= 6)
            return new SmallMinoMask(mino, y, deleteKey);
        else if (maxHeight <= 12)
            return new MiddleMinoMask(mino, y, deleteKey);
        throw new UnsupportedOperationException("height < 12");
    }
}
