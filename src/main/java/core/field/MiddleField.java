package core.field;

import core.mino.Mino;

/**
 * フィールドの高さ height <= 12 であること
 * マルチスレッド非対応
 */
public class MiddleField implements Field {
    private static final int FIELD_WIDTH = 10;
    private static final int MAX_FIELD_HEIGHT = 12;
    private static final int FIELD_ROW_BOARDER_Y = 6;

    private long xBoardLow = 0; // x,y: 最下位 (0,0), (1,0),  ... , (9,0), (0,1), ... 最上位 // フィールド範囲外は必ず0であること
    private long xBoardHigh = 0; // x,y: 最下位 (0,0), (1,0),  ... , (9,0), (0,1), ... 最上位 // フィールド範囲外は必ず0であること

    public MiddleField() {
    }

    private MiddleField(MiddleField src) {
        this.xBoardLow = src.xBoardLow;
        this.xBoardHigh = src.xBoardHigh;
    }

    public MiddleField(long xBoardLow, long xBoardHigh) {
        this.xBoardLow = xBoardLow;
        this.xBoardHigh = xBoardHigh;
    }

    @Override
    public int getMaxFieldHeight() {
        return MAX_FIELD_HEIGHT;
    }

    @Override
    public void setBlock(int x, int y) {
        if (y < FIELD_ROW_BOARDER_Y)
            xBoardLow |= getXMask(x, y);
        else
            xBoardHigh |= getXMask(x, y - FIELD_ROW_BOARDER_Y);
    }

    @Override
    public void removeBlock(int x, int y) {
        if (y < FIELD_ROW_BOARDER_Y)
            xBoardLow &= ~getXMask(x, y);
        else
            xBoardHigh &= ~getXMask(x, y - FIELD_ROW_BOARDER_Y);
    }

    private long getXMask(int x, int y) {
        return 1L << x + y * FIELD_WIDTH;
    }

    @Override
    public void putMino(Mino mino, int x, int y) {
        // Lowの更新が必要
        if (y + mino.getMinY() < FIELD_ROW_BOARDER_Y)
            xBoardLow |= mino.getMask(x, y);

        // Highの更新が必要
        if (FIELD_ROW_BOARDER_Y <= y + mino.getMaxY())
            xBoardHigh |= mino.getMask(x, y - FIELD_ROW_BOARDER_Y);
    }

    @Override
    public void removeMino(Mino mino, int x, int y) {
        // Lowの更新が必要
        if (y + mino.getMinY() < FIELD_ROW_BOARDER_Y)
            xBoardLow &= ~mino.getMask(x, y);

        // Highの更新が必要
        if (FIELD_ROW_BOARDER_Y <= y + mino.getMaxY())
            xBoardHigh &= ~mino.getMask(x, y - FIELD_ROW_BOARDER_Y);
    }

    @Override
    public int getYOnHarddrop(Mino mino, int x, int startY) {
        int min = -mino.getMinY();
        for (int y = startY - 1; min <= y; y--)
            if (!canPutMino(mino, x, y))
                return y + 1;
        return min;
    }

    @Override
    public boolean canReachOnHarddrop(Mino mino, int x, int startY) {
        int max = MAX_FIELD_HEIGHT - mino.getMaxY();
        for (int y = startY + 1; y < max; y++)
            if (!canPutMino(mino, x, y))
                return false;
        return true;
    }

    @Override
    public boolean isEmpty(int x, int y) {
        if (y < FIELD_ROW_BOARDER_Y)
            return (xBoardLow & getXMask(x, y)) == 0L;
        else
            return (xBoardHigh & getXMask(x, y - FIELD_ROW_BOARDER_Y)) == 0L;
    }

    @Override
    public boolean existsAbove(int y) {
        if (MAX_FIELD_HEIGHT <= y) {
            return false;
        } else if (FIELD_ROW_BOARDER_Y <= y) {
            // Highで完結
            long mask = 0xffffffffffL << (y - FIELD_ROW_BOARDER_Y) * FIELD_WIDTH;
            return (xBoardHigh & mask) != 0L;
        } else {
            // すべて必要
            // Highのチェック
            if (xBoardHigh != 0L)
                return true;

            // Lowのチェック
            long mask = 0xffffffffffL << y * FIELD_WIDTH;
            return (xBoardLow & mask) != 0L;
        }
    }

    @Override
    public boolean isPerfect() {
        return xBoardLow == 0L && xBoardHigh == 0L;
    }

    @Override
    public boolean isFilledInColumn(int x, int maxY) {
        if (maxY == 0) {
            return true;
        } else if (maxY <= FIELD_ROW_BOARDER_Y) {
            // Lowで完結
            long mask = BitOperators.getColumnBelowY(maxY) << x;
            return (~xBoardLow & mask) == 0L;
        } else {
            // すべて必要
            // Lowのチェック
            long maskLow = BitOperators.getColumnBelowY(FIELD_ROW_BOARDER_Y) << x;
            if ((~xBoardLow & maskLow) != 0L)
                return false;

            // Highのチェック
            long maskHigh = BitOperators.getColumnBelowY(maxY - FIELD_ROW_BOARDER_Y) << x;
            return (~xBoardHigh & maskHigh) == 0L;
        }
    }

    @Override
    public boolean isWallBetweenLeft(int x, int maxY) {
        if (maxY == 0) {
            return true;
        } else if (maxY <= FIELD_ROW_BOARDER_Y) {
            // Lowで完結
            return isWallBetweenLeft(x, maxY, xBoardLow);
        } else {
            // すべて必要
            // Lowのチェック
            if (!isWallBetweenLeft(x, FIELD_ROW_BOARDER_Y, xBoardLow))
                return false;

            // Highのチェック
            return isWallBetweenLeft(x, maxY - FIELD_ROW_BOARDER_Y, xBoardHigh);
        }
    }

    private boolean isWallBetweenLeft(int x, int maxYHigh, long xBoardHigh) {
        long maskHigh = BitOperators.getColumnBelowY(maxYHigh);
        long reverseXBoardHigh = ~xBoardHigh;
        long columnHigh = maskHigh << x;
        long rightHigh = reverseXBoardHigh & columnHigh;
        long leftHigh = reverseXBoardHigh & (columnHigh >>> 1);
        return ((leftHigh << 1) & rightHigh) == 0L;
    }

    @Override
    public boolean canPutMino(Mino mino, int x, int y) {
        if (y + mino.getMaxY() < FIELD_ROW_BOARDER_Y) {
            // Lowで完結
            return (xBoardLow & mino.getMask(x, y)) == 0L;
        } else if (FIELD_ROW_BOARDER_Y <= y + mino.getMinY()) {
            // Highで完結
            return (xBoardHigh & mino.getMask(x, y - FIELD_ROW_BOARDER_Y)) == 0L;
        } else {
            // 分割
            return (xBoardLow & mino.getMask(x, y)) == 0L & (xBoardHigh & mino.getMask(x, y - FIELD_ROW_BOARDER_Y)) == 0L;
        }
    }

    @Override
    public boolean isOnGround(Mino mino, int x, int y) {
        return y <= -mino.getMinY() || !canPutMino(mino, x, y - 1);
    }

    @Override
    public int getBlockCountBelowOnX(int x, int maxY) {
        if (maxY <= FIELD_ROW_BOARDER_Y) {
            // Lowで完結
            long mask = BitOperators.getColumnBelowY(maxY) << x;
            return Long.bitCount(xBoardLow & mask);
        } else {
            // すべて必要
            long maskLow = BitOperators.getColumnBelowY(FIELD_ROW_BOARDER_Y) << x;
            long maskHigh = BitOperators.getColumnBelowY(maxY - FIELD_ROW_BOARDER_Y) << x;
            return Long.bitCount(xBoardLow & maskLow) + Long.bitCount(xBoardHigh & maskHigh);
        }
    }

    @Override
    public int getAllBlockCount() {
        return Long.bitCount(xBoardLow) + Long.bitCount(xBoardHigh);
    }

    @Override
    public int clearLine() {
        long deleteKey = clearLineReturnKey();
        return Long.bitCount(deleteKey);
    }

    @Override
    public long clearLineReturnKey() {
        long deleteKeyLow = KeyOperators.getDeleteKey(xBoardLow);
        long newXBoardLow = LongBoardMap.deleteLine(xBoardLow, deleteKeyLow);

        long deleteKeyHigh = KeyOperators.getDeleteKey(xBoardHigh);
        long newXBoardHigh = LongBoardMap.deleteLine(xBoardHigh, deleteKeyHigh);

        int deleteLineLow = Long.bitCount(deleteKeyLow);

        this.xBoardLow = (newXBoardLow | (newXBoardHigh << (6 - deleteLineLow) * 10)) & 0xfffffffffffffffL;
        this.xBoardHigh = newXBoardHigh >>> deleteLineLow * 10;

        return deleteKeyLow | (deleteKeyHigh << 1);
    }

    @Override
    public void insertBlackLineWithKey(long deleteKey) {
        long deleteKeyLow = deleteKey & 0x4010040100401L;
        int deleteLineLow = Long.bitCount(deleteKeyLow);
        int leftLineLow = 6 - deleteLineLow;
        long newXBoardLow = LongBoardMap.insertBlackLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLineLow), deleteKeyLow);

        long deleteKeyHigh = (deleteKey & 0x8020080200802L) >> 1;
        long newXBoardHigh = LongBoardMap.insertBlackLine((xBoardHigh << 10 * deleteLineLow) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLineLow)) >> 10 * leftLineLow), deleteKeyHigh);

        this.xBoardLow = newXBoardLow;
        this.xBoardHigh = newXBoardHigh & 0xfffffffffffffffL;
    }

    @Override
    public void insertWhiteLineWithKey(long deleteKey) {
        long deleteKeyLow = deleteKey & 0x4010040100401L;
        int deleteLineLow = Long.bitCount(deleteKeyLow);
        int leftLineLow = 6 - deleteLineLow;
        long newXBoardLow = LongBoardMap.insertWhiteLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLineLow), deleteKeyLow);

        long deleteKeyHigh = (deleteKey & 0x8020080200802L) >> 1;
        long newXBoardHigh = LongBoardMap.insertWhiteLine((xBoardHigh << 10 * deleteLineLow) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLineLow)) >> 10 * leftLineLow), deleteKeyHigh);

        this.xBoardLow = newXBoardLow;
        this.xBoardHigh = newXBoardHigh & 0xfffffffffffffffL;
    }

    @Override
    public int getBoardCount() {
        return 2;
    }

    @Override
    public long getBoard(int index) {
        if (index == 0)
            return xBoardLow;
        return xBoardHigh;
    }

    @Override
    public Field freeze(int maxHeight) {
        if (maxHeight <= 6)
            return new SmallField(xBoardLow);
        return new MiddleField(this);
    }

    @Override
    public void merge(Field other) {
        int otherBlockCount = other.getAllBlockCount();
        if (otherBlockCount == 1) {
            xBoardLow |= other.getBoard(0);
            return;
        } else if (otherBlockCount == 2) {
            xBoardLow |= other.getBoard(0);
            xBoardHigh |= other.getBoard(1);
            return;
        }
        throw new UnsupportedOperationException("too large field");
    }

    @Override
    public int getUpperYWith4Blocks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLowerY() {
        throw new UnsupportedOperationException();
    }
}
