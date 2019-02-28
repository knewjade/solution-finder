package core.field;

import common.comparator.FieldComparator;
import core.mino.Mino;
import core.neighbor.OriginalPiece;

/**
 * フィールドの高さ height <= 24 であること
 * マルチスレッド非対応
 */
public class LargeField implements Field {
    private static final int FIELD_WIDTH = 10;
    private static final int MAX_FIELD_HEIGHT = 24;
    private static final int FIELD_ROW_MID_LOW_BOARDER_Y = 6;
    private static final int FIELD_ROW_MID_HIGH_BOARDER_Y = 12;
    private static final int FIELD_ROW_HIGH_BOARDER_Y = 18;
    private static final long VALID_BOARD_RANGE = 0xfffffffffffffffL;

    // x,y: 最下位 (0,0), (1,0),  ... , (9,0), (0,1), ... 最上位 // フィールド範囲外は必ず0であること
    private long xBoardLow = 0;
    private long xBoardMidLow = 0;
    private long xBoardMidHigh = 0;
    private long xBoardHigh = 0;

    public LargeField() {
    }

    private LargeField(LargeField src) {
        this.xBoardLow = src.xBoardLow;
        this.xBoardMidLow = src.xBoardMidLow;
        this.xBoardMidHigh = src.xBoardMidHigh;
        this.xBoardHigh = src.xBoardHigh;
    }

    public LargeField(long xBoardLow, long xBoardMidLow, long xBoardMidHigh, long xBoardHigh) {
        this.xBoardLow = xBoardLow;
        this.xBoardMidLow = xBoardMidLow;
        this.xBoardMidHigh = xBoardMidHigh;
        this.xBoardHigh = xBoardHigh;
    }

    long getXBoardLow() {
        return xBoardLow;
    }

    long getXBoardMidLow() {
        return xBoardMidLow;
    }

    long getXBoardMidHigh() {
        return xBoardMidHigh;
    }

    long getXBoardHigh() {
        return xBoardHigh;
    }

    @Override
    public int getMaxFieldHeight() {
        return MAX_FIELD_HEIGHT;
    }

    @Override
    public void setBlock(int x, int y) {
        switch (select(y)) {
            case Low:
                xBoardLow |= getXMask(x, y);
                return;
            case MidLow:
                xBoardMidLow |= getXMask(x, y - FIELD_ROW_MID_LOW_BOARDER_Y);
                return;
            case MidHigh:
                xBoardMidHigh |= getXMask(x, y - FIELD_ROW_MID_HIGH_BOARDER_Y);
                return;
            case High:
                xBoardHigh |= getXMask(x, y - FIELD_ROW_HIGH_BOARDER_Y);
                return;
        }
        throw new IllegalStateException("Unreachable");
    }

    private Position select(int y) {
        if (y < FIELD_ROW_MID_HIGH_BOARDER_Y) {
            if (y < FIELD_ROW_MID_LOW_BOARDER_Y)
                return Position.Low;
            else
                return Position.MidLow;
        } else {
            if (y < FIELD_ROW_HIGH_BOARDER_Y)
                return Position.MidHigh;
            else
                return Position.High;
        }
    }

    private long getXMask(int x, int y) {
        return 1L << x + y * FIELD_WIDTH;
    }

    @Override
    public void removeBlock(int x, int y) {
        switch (select(y)) {
            case Low:
                xBoardLow &= ~getXMask(x, y);
                return;
            case MidLow:
                xBoardMidLow &= ~getXMask(x, y - FIELD_ROW_MID_LOW_BOARDER_Y);
                return;
            case MidHigh:
                xBoardMidHigh &= ~getXMask(x, y - FIELD_ROW_MID_HIGH_BOARDER_Y);
                return;
            case High:
                xBoardHigh &= ~getXMask(x, y - FIELD_ROW_HIGH_BOARDER_Y);
                return;
        }
        throw new IllegalStateException("Unreachable");
    }

    @Override
    public void put(Mino mino, int x, int y) {
        switch (select(y)) {
            case Low: {
                int y2 = y;

                xBoardLow |= mino.getMask(x, y);

                // MidLowの更新が必要
                if (6 <= y2 + mino.getMaxY())
                    xBoardMidLow |= mino.getMask(x, y2 - 6);

                return;
            }
            case MidLow: {
                int y2 = y - FIELD_ROW_MID_LOW_BOARDER_Y;

                xBoardMidLow |= mino.getMask(x, y2);

                // Lowの更新が必要
                if (y2 + mino.getMinY() < 0)
                    xBoardLow |= mino.getMask(x, y2 + 6);

                // MidHighの更新が必要
                if (6 <= y2 + mino.getMaxY())
                    xBoardMidHigh |= mino.getMask(x, y2 - 6);

                return;
            }
            case MidHigh: {
                int y2 = y - FIELD_ROW_MID_HIGH_BOARDER_Y;

                xBoardMidHigh |= mino.getMask(x, y2);

                // MidLowの更新が必要
                if (y2 + mino.getMinY() < 0)
                    xBoardMidLow |= mino.getMask(x, y2 + 6);

                // Highの更新が必要
                if (6 <= y2 + mino.getMaxY())
                    xBoardHigh |= mino.getMask(x, y2 - 6);

                return;
            }
            case High: {
                int y2 = y - FIELD_ROW_HIGH_BOARDER_Y;

                xBoardHigh |= mino.getMask(x, y2);

                // MidHighの更新が必要
                if (y2 + mino.getMinY() < 0)
                    xBoardMidHigh |= mino.getMask(x, y2 + 6);

                return;
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    @Override
    public void put(OriginalPiece piece) {
        merge(piece.getMinoField());
    }

    @Override
    public boolean canPut(Mino mino, int x, int y) {
        switch (select(y)) {
            case Low: {
                int y2 = y;

                if (6 <= y2 + mino.getMaxY()) {
                    // Low & MidLow
                    return (xBoardLow & mino.getMask(x, y2)) == 0L & (xBoardMidLow & mino.getMask(x, y2 - 6)) == 0L;
                }

                // Low
                return (xBoardLow & mino.getMask(x, y2)) == 0L;
            }
            case MidLow: {
                int y2 = y - FIELD_ROW_MID_LOW_BOARDER_Y;

                if (6 <= y2 + mino.getMaxY()) {
                    // MidLow & MidHigh
                    return (xBoardMidLow & mino.getMask(x, y2)) == 0L & (xBoardMidHigh & mino.getMask(x, y2 - 6)) == 0L;
                } else if (y2 + mino.getMinY() < 0) {
                    // MidLow & Low
                    return (xBoardMidLow & mino.getMask(x, y2)) == 0L & (xBoardLow & mino.getMask(x, y2 + 6)) == 0L;
                }

                // MidLow
                return (xBoardMidLow & mino.getMask(x, y2)) == 0L;
            }
            case MidHigh: {
                int y2 = y - FIELD_ROW_MID_HIGH_BOARDER_Y;

                if (6 <= y2 + mino.getMaxY()) {
                    // MidHigh & High
                    return (xBoardMidHigh & mino.getMask(x, y2)) == 0L & (xBoardHigh & mino.getMask(x, y2 - 6)) == 0L;
                } else if (y2 + mino.getMinY() < 0) {
                    // MidHigh & MidLow
                    return (xBoardMidHigh & mino.getMask(x, y2)) == 0L & (xBoardMidLow & mino.getMask(x, y2 + 6)) == 0L;
                }

                // MidHigh
                return (xBoardMidHigh & mino.getMask(x, y2)) == 0L;
            }
            case High: {
                int y2 = y - FIELD_ROW_HIGH_BOARDER_Y;

                if (y2 + mino.getMinY() < 0) {
                    // High & MidHigh
                    return (xBoardHigh & mino.getMask(x, y2)) == 0L & (xBoardMidHigh & mino.getMask(x, y2 + 6)) == 0L;
                }

                // High
                return (xBoardHigh & mino.getMask(x, y2)) == 0L;
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    @Override
    public boolean canPut(OriginalPiece piece) {
        return canMerge(piece.getMinoField());
    }

    @Override
    public void remove(Mino mino, int x, int y) {
        switch (select(y)) {
            case Low: {
                int y2 = y;

                xBoardLow &= ~mino.getMask(x, y);

                // MidLowの更新が必要
                if (6 <= y2 + mino.getMaxY())
                    xBoardMidLow &= ~mino.getMask(x, y2 - 6);

                return;
            }
            case MidLow: {
                int y2 = y - FIELD_ROW_MID_LOW_BOARDER_Y;

                xBoardMidLow &= ~mino.getMask(x, y2);

                // Lowの更新が必要
                if (y2 + mino.getMinY() < 0)
                    xBoardLow &= ~mino.getMask(x, y2 + 6);

                // MidHighの更新が必要
                if (6 <= y2 + mino.getMaxY())
                    xBoardMidHigh &= ~mino.getMask(x, y2 - 6);

                return;
            }
            case MidHigh: {
                int y2 = y - FIELD_ROW_MID_HIGH_BOARDER_Y;

                xBoardMidHigh &= ~mino.getMask(x, y2);

                // MidLowの更新が必要
                if (y2 + mino.getMinY() < 0)
                    xBoardMidLow &= ~mino.getMask(x, y2 + 6);

                // Highの更新が必要
                if (6 <= y2 + mino.getMaxY())
                    xBoardHigh &= ~mino.getMask(x, y2 - 6);

                return;
            }
            case High: {
                int y2 = y - FIELD_ROW_HIGH_BOARDER_Y;

                xBoardHigh &= ~mino.getMask(x, y2);

                // MidHighの更新が必要
                if (y2 + mino.getMinY() < 0)
                    xBoardMidHigh &= ~mino.getMask(x, y2 + 6);

                return;
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    @Override
    public void remove(OriginalPiece piece) {
        reduce(piece.getMinoField());
    }

    @Override
    public int getYOnHarddrop(Mino mino, int x, int startY) {
        int min = -mino.getMinY();
        for (int y = startY - 1; min <= y; y--)
            if (!canPut(mino, x, y))
                return y + 1;
        return min;
    }

    @Override
    public boolean canReachOnHarddrop(Mino mino, int x, int y) {
        int max = MAX_FIELD_HEIGHT - mino.getMinY();
        for (int yIndex = y + 1; yIndex < max; yIndex++)
            if (!canPut(mino, x, yIndex))
                return false;
        return true;
    }

    @Override
    public boolean canReachOnHarddrop(OriginalPiece piece) {
        Field collider = piece.getHarddropCollider();
        return canMerge(collider);
    }

    @Override
    public boolean isEmpty(int x, int y) {
        switch (select(y)) {
            case Low:
                return (xBoardLow & getXMask(x, y)) == 0L;
            case MidLow:
                return (xBoardMidLow & getXMask(x, y - FIELD_ROW_MID_LOW_BOARDER_Y)) == 0L;
            case MidHigh:
                return (xBoardMidHigh & getXMask(x, y - FIELD_ROW_MID_HIGH_BOARDER_Y)) == 0L;
            case High:
                return (xBoardHigh & getXMask(x, y - FIELD_ROW_HIGH_BOARDER_Y)) == 0L;
        }
        throw new IllegalStateException("Unreachable");
    }

    @Override
    public boolean exists(int x, int y) {
        return !isEmpty(x, y);
    }

    @Override
    public boolean existsAbove(int y) {
        if (MAX_FIELD_HEIGHT <= y) {
            return false;
        }

        switch (select(y)) {
            case Low: {
                // すべて必要
                // High & MidHigh & MidLowのチェック
                if (xBoardHigh != 0L || xBoardMidHigh != 0L || xBoardMidLow != 0L)
                    return true;

                // Lowのチェック
                long mask = VALID_BOARD_RANGE << y * FIELD_WIDTH;
                return (xBoardLow & mask) != 0L;
            }
            case MidLow: {
                // High & MidHighのチェック
                if (xBoardHigh != 0L || xBoardMidHigh != 0L)
                    return true;

                // MidLowのチェック
                long mask = VALID_BOARD_RANGE << (y - FIELD_ROW_MID_LOW_BOARDER_Y) * FIELD_WIDTH;
                return (xBoardMidLow & mask) != 0L;
            }
            case MidHigh: {
                // Highのチェック
                if (xBoardHigh != 0L)
                    return true;

                // MidHighのチェック
                long mask = VALID_BOARD_RANGE << (y - FIELD_ROW_MID_HIGH_BOARDER_Y) * FIELD_WIDTH;
                return (xBoardMidHigh & mask) != 0L;
            }
            case High: {
                // Highで完結
                long mask = VALID_BOARD_RANGE << (y - FIELD_ROW_HIGH_BOARDER_Y) * FIELD_WIDTH;
                return (xBoardHigh & mask) != 0L;
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    @Override
    public boolean isPerfect() {
        return xBoardLow == 0L && xBoardMidLow == 0L && xBoardMidHigh == 0L && xBoardHigh == 0L;
    }

    @Override
    public boolean isFilledInColumn(int x, int maxY) {
        if (maxY == 0) {
            return true;
        }

        switch (select(maxY)) {
            case Low: {
                // Lowで完結
                long mask = BitOperators.getColumnOneLineBelowY(maxY) << x;
                return (~xBoardLow & mask) == 0L;
            }
            case MidLow: {
                // Lowのチェック
                long maskFull = BitOperators.getColumnOneLineBelowY(6) << x;
                if ((~xBoardLow & maskFull) != 0L)
                    return false;

                // MidLowのチェック
                long maskMidLow = BitOperators.getColumnOneLineBelowY(maxY - FIELD_ROW_MID_LOW_BOARDER_Y) << x;
                return (~xBoardMidLow & maskMidLow) == 0L;
            }
            case MidHigh: {
                // Lowのチェック
                long maskFull = BitOperators.getColumnOneLineBelowY(6) << x;
                if ((~xBoardLow & maskFull) != 0L)
                    return false;

                // MidLowのチェック
                if ((~xBoardMidLow & maskFull) != 0L)
                    return false;

                // MidHighのチェック
                long maskMidHigh = BitOperators.getColumnOneLineBelowY(maxY - FIELD_ROW_MID_HIGH_BOARDER_Y) << x;
                return (~xBoardMidHigh & maskMidHigh) == 0L;
            }
            case High: {
                // Lowのチェック
                long maskFull = BitOperators.getColumnOneLineBelowY(6) << x;
                if ((~xBoardLow & maskFull) != 0L)
                    return false;

                // MidLowのチェック
                if ((~xBoardMidLow & maskFull) != 0L)
                    return false;

                // MidHighのチェック
                if ((~xBoardMidHigh & maskFull) != 0L)
                    return false;

                // Highのチェック
                long maskHigh = BitOperators.getColumnOneLineBelowY(maxY - FIELD_ROW_HIGH_BOARDER_Y) << x;
                return (~xBoardHigh & maskHigh) == 0L;
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    @Override
    public boolean isWallBetweenLeft(int x, int maxY) {
        if (maxY == 0) {
            return true;
        }

        switch (select(maxY)) {
            case Low: {
                // Lowで完結
                return BitOperators.isWallBetweenLeft(x, maxY, xBoardLow);
            }
            case MidLow: {
                // Lowのチェック
                if (!BitOperators.isWallBetweenLeft(x, 6, xBoardLow))
                    return false;

                // MidLowのチェック
                return BitOperators.isWallBetweenLeft(x, maxY - FIELD_ROW_MID_LOW_BOARDER_Y, xBoardMidLow);
            }
            case MidHigh: {
                // Lowのチェック
                if (!BitOperators.isWallBetweenLeft(x, 6, xBoardLow))
                    return false;

                // MidLowのチェック
                if (!BitOperators.isWallBetweenLeft(x, 6, xBoardMidLow))
                    return false;

                // MidHighのチェック
                return BitOperators.isWallBetweenLeft(x, maxY - FIELD_ROW_MID_HIGH_BOARDER_Y, xBoardMidHigh);
            }
            case High: {
                // Lowのチェック
                if (!BitOperators.isWallBetweenLeft(x, 6, xBoardLow))
                    return false;

                // MidLowのチェック
                if (!BitOperators.isWallBetweenLeft(x, 6, xBoardMidLow))
                    return false;

                // MidHighのチェック
                if (!BitOperators.isWallBetweenLeft(x, 6, xBoardMidHigh))
                    return false;

                // Highのチェック
                return BitOperators.isWallBetweenLeft(x, maxY - FIELD_ROW_HIGH_BOARDER_Y, xBoardHigh);
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    @Override
    public boolean isOnGround(Mino mino, int x, int y) {
        return y <= -mino.getMinY() || !canPut(mino, x, y - 1);
    }

    @Override
    public int getBlockCountBelowOnX(int x, int maxY) {
        switch (select(maxY)) {
            case Low: {
                // Low
                long mask = BitOperators.getColumnOneLineBelowY(maxY) << x;
                return Long.bitCount(xBoardLow & mask);
            }
            case MidLow: {
                // Low + MidLow
                long fullMask = BitOperators.getColumnOneLineBelowY(6) << x;
                long mask = BitOperators.getColumnOneLineBelowY(maxY - FIELD_ROW_MID_LOW_BOARDER_Y) << x;
                return Long.bitCount(xBoardLow & fullMask)
                        + Long.bitCount(xBoardMidLow & mask);
            }
            case MidHigh: {
                // Low + MidLow + MidHigh
                long fullMask = BitOperators.getColumnOneLineBelowY(6) << x;
                long mask = BitOperators.getColumnOneLineBelowY(maxY - FIELD_ROW_MID_HIGH_BOARDER_Y) << x;
                return Long.bitCount(xBoardLow & fullMask)
                        + Long.bitCount(xBoardMidLow & fullMask)
                        + Long.bitCount(xBoardMidHigh & mask);
            }
            case High: {
                // Low + MidLow + MidHigh + High
                long fullMask = BitOperators.getColumnOneLineBelowY(6) << x;
                long mask = BitOperators.getColumnOneLineBelowY(maxY - FIELD_ROW_HIGH_BOARDER_Y) << x;
                return Long.bitCount(xBoardLow & fullMask)
                        + Long.bitCount(xBoardMidLow & fullMask)
                        + Long.bitCount(xBoardMidHigh & fullMask)
                        + Long.bitCount(xBoardHigh & mask);
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    @Override
    public int getBlockCountOnY(int y) {
        switch (select(y)) {
            case Low: {
                int y2 = y;
                long mask = 0x3ffL << y2 * FIELD_WIDTH;
                return Long.bitCount(xBoardLow & mask);
            }
            case MidLow: {
                int y2 = y - FIELD_ROW_MID_LOW_BOARDER_Y;
                long mask = 0x3ffL << y2 * FIELD_WIDTH;
                return Long.bitCount(xBoardMidLow & mask);
            }
            case MidHigh: {
                int y2 = y - FIELD_ROW_MID_HIGH_BOARDER_Y;
                long mask = 0x3ffL << y2 * FIELD_WIDTH;
                return Long.bitCount(xBoardMidHigh & mask);
            }
            case High: {
                int y2 = y - FIELD_ROW_HIGH_BOARDER_Y;
                long mask = 0x3ffL << y2 * FIELD_WIDTH;
                return Long.bitCount(xBoardHigh & mask);
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    @Override
    public boolean existsBlockCountOnY(int y) {
        switch (select(y)) {
            case Low: {
                int y2 = y;
                long mask = 0x3ffL << y2 * FIELD_WIDTH;
                return (xBoardLow & mask) != 0;
            }
            case MidLow: {
                int y2 = y - FIELD_ROW_MID_LOW_BOARDER_Y;
                long mask = 0x3ffL << y2 * FIELD_WIDTH;
                return (xBoardMidLow & mask) != 0;
            }
            case MidHigh: {
                int y2 = y - FIELD_ROW_MID_HIGH_BOARDER_Y;
                long mask = 0x3ffL << y2 * FIELD_WIDTH;
                return (xBoardMidHigh & mask) != 0;
            }
            case High: {
                int y2 = y - FIELD_ROW_HIGH_BOARDER_Y;
                long mask = 0x3ffL << y2 * FIELD_WIDTH;
                return (xBoardHigh & mask) != 0;
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    @Override
    public int getNumOfAllBlocks() {
        return Long.bitCount(xBoardLow)
                + Long.bitCount(xBoardMidLow)
                + Long.bitCount(xBoardMidHigh)
                + Long.bitCount(xBoardHigh);
    }

    @Override
    public int clearLine() {
        long deleteKey = clearLineReturnKey();
        return Long.bitCount(deleteKey);
    }

    @Override
    public long clearLineReturnKey() {
        long deleteKeyLow = KeyOperators.getDeleteKey(xBoardLow);
        long deleteKeyMidLow = KeyOperators.getDeleteKey(xBoardMidLow);
        long deleteKeyMidHigh = KeyOperators.getDeleteKey(xBoardMidHigh);
        long deleteKeyHigh = KeyOperators.getDeleteKey(xBoardHigh);

        deleteLine(deleteKeyLow, deleteKeyMidLow, deleteKeyMidHigh, deleteKeyHigh);

        return deleteKeyLow | (deleteKeyMidLow << 1) | (deleteKeyMidHigh << 2) | (deleteKeyHigh << 3);
    }

    @Override
    public long getFilledLine() {
        long deleteKeyLow = KeyOperators.getDeleteKey(xBoardLow);
        long deleteKeyMidLow = KeyOperators.getDeleteKey(xBoardMidLow);
        long deleteKeyMidHigh = KeyOperators.getDeleteKey(xBoardMidHigh);
        long deleteKeyHigh = KeyOperators.getDeleteKey(xBoardHigh);

        return deleteKeyLow | (deleteKeyMidLow << 1) | (deleteKeyMidHigh << 2) | (deleteKeyHigh << 3);
    }

    @Override
    public long getUsingKey() {
        long usingKeyLow = KeyOperators.getUsingKey(xBoardLow);
        long usingKeyMidLow = KeyOperators.getUsingKey(xBoardMidLow);
        long usingKeyMidHigh = KeyOperators.getUsingKey(xBoardMidHigh);
        long usingKeyHigh = KeyOperators.getUsingKey(xBoardHigh);

        return usingKeyLow | (usingKeyMidLow << 1) | (usingKeyMidHigh << 2) | (usingKeyHigh << 3);
    }

    private void deleteLine(long deleteKeyLow, long deleteKeyMidLow, long deleteKeyMidHigh, long deleteKeyHigh) {
        // 下半分
        long newXBoardLow = LongBoardMap.deleteLine(xBoardLow, deleteKeyLow);

        long newXBoardMidLow = LongBoardMap.deleteLine(xBoardMidLow, deleteKeyMidLow);

        int deleteLineLow = Long.bitCount(deleteKeyLow);

        long low = (newXBoardLow | (newXBoardMidLow << (6 - deleteLineLow) * 10)) & VALID_BOARD_RANGE;
        long midLow = newXBoardMidLow >>> deleteLineLow * 10;

        int deleteLineMidLow = Long.bitCount(deleteKeyMidLow);
        int deleteLineBottom = deleteLineLow + deleteLineMidLow;

        // 上半分
        long newXBoardMidHigh = LongBoardMap.deleteLine(xBoardMidHigh, deleteKeyMidHigh);

        long newXBoardHigh = LongBoardMap.deleteLine(xBoardHigh, deleteKeyHigh);

        int deleteLineMidHigh = Long.bitCount(deleteKeyMidHigh);

        long midHigh = (newXBoardMidHigh | (newXBoardHigh << (6 - deleteLineMidHigh) * 10)) & VALID_BOARD_RANGE;
        long high = newXBoardHigh >>> deleteLineMidHigh * 10;

        // 上半分と下半分をマージ
        if (deleteLineBottom < 6) {
            this.xBoardLow = low;
            this.xBoardMidLow = (midLow | (midHigh << (6 - deleteLineBottom) * 10)) & VALID_BOARD_RANGE;
            this.xBoardMidHigh = ((midHigh >>> deleteLineBottom * 10) | (high << (6 - deleteLineBottom) * 10)) & VALID_BOARD_RANGE;
            this.xBoardHigh = high >>> deleteLineBottom * 10;
        } else {
            int slide = deleteLineBottom - 6;
            this.xBoardLow = (low | (midHigh << (6 - slide) * 10)) & VALID_BOARD_RANGE;
            this.xBoardMidLow = ((midHigh >>> slide * 10) | (high << (6 - slide) * 10)) & VALID_BOARD_RANGE;
            this.xBoardMidHigh = high >>> slide * 10;
            this.xBoardHigh = 0L;
        }
    }

    @Override
    public void insertBlackLineWithKey(long deleteKey) {
        long deleteKeyLow = extractDeleteKeyLow(deleteKey);
        int deleteLineLow = Long.bitCount(deleteKeyLow);

        long deleteKeyMidLow = extractDeleteKeyMidLow(deleteKey);
        int deleteLineMidLow = Long.bitCount(deleteKeyMidLow);

        long deleteKeyMidHigh = extractDeleteKeyMidHigh(deleteKey);
        int deleteLineMidHigh = Long.bitCount(deleteKeyMidHigh);

        long deleteKeyHigh = extractDeleteKeyHigh(deleteKey);

        int deleteLine1 = deleteLineLow;
        int deleteLine2 = deleteLineLow + deleteLineMidLow;
        int deleteLine3 = deleteLine2 + deleteLineMidHigh;

        if (deleteLine3 < 6) {
            // Low & MidLow & MidHigh & High
            int leftLine3 = 6 - deleteLine3;
            long newXBoardHigh = LongBoardMap.insertBlackLine(
                    (xBoardHigh << 10 * deleteLine3) | ((xBoardMidHigh & BitOperators.getRowMaskAboveY(leftLine3)) >> 10 * leftLine3), deleteKeyHigh
            );

            int leftLine2 = 6 - deleteLine2;
            long newXBoardMidHigh = LongBoardMap.insertBlackLine(
                    (xBoardMidHigh << 10 * deleteLine2) | ((xBoardMidLow & BitOperators.getRowMaskAboveY(leftLine2)) >> 10 * leftLine2), deleteKeyMidHigh
            );

            int leftLine1 = 6 - deleteLine1;
            long newXBoardMidLow = LongBoardMap.insertBlackLine(
                    (xBoardMidLow << 10 * deleteLine1) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine1)) >> 10 * leftLine1), deleteKeyMidLow
            );

            long newXBoardLow = LongBoardMap.insertBlackLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLine1), deleteKeyLow);

            this.xBoardLow = newXBoardLow;
            this.xBoardMidLow = newXBoardMidLow & VALID_BOARD_RANGE;
            this.xBoardMidHigh = newXBoardMidHigh & VALID_BOARD_RANGE;
            this.xBoardHigh = newXBoardHigh & VALID_BOARD_RANGE;
        } else if (deleteLine3 < 12) {
            // Low & MidLow & MidHigh
            int deleteLine3_6 = deleteLine3 - 6;
            int leftLine3 = 6 - deleteLine3_6;
            long newXBoardHigh = LongBoardMap.insertBlackLine(
                    (xBoardMidHigh << 10 * deleteLine3_6) | ((xBoardMidLow & BitOperators.getRowMaskAboveY(leftLine3)) >> 10 * leftLine3), deleteKeyHigh
            );

            if (deleteLine2 < 6) {
                // Low & MidLow & MidHigh
                int leftLine2 = 6 - deleteLine2;
                long newXBoardMidHigh = LongBoardMap.insertBlackLine(
                        (xBoardMidHigh << 10 * deleteLine2) | ((xBoardMidLow & BitOperators.getRowMaskAboveY(leftLine2)) >> 10 * leftLine2), deleteKeyMidHigh
                );

                int leftLine1 = 6 - deleteLine1;
                long newXBoardMidLow = LongBoardMap.insertBlackLine(
                        (xBoardMidLow << 10 * deleteLine1) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine1)) >> 10 * leftLine1), deleteKeyMidLow
                );

                long newXBoardLow = LongBoardMap.insertBlackLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLine1), deleteKeyLow);

                this.xBoardLow = newXBoardLow;
                this.xBoardMidLow = newXBoardMidLow & VALID_BOARD_RANGE;
                this.xBoardMidHigh = newXBoardMidHigh & VALID_BOARD_RANGE;
                this.xBoardHigh = newXBoardHigh & VALID_BOARD_RANGE;
            } else {
                // Low & MidLow
                int deleteLine2_6 = deleteLine2 - 6;
                int leftLine2 = 6 - deleteLine2_6;
                long newXBoardMidHigh = LongBoardMap.insertBlackLine(
                        (xBoardMidLow << 10 * deleteLine2_6) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine2)) >> 10 * leftLine2), deleteKeyMidHigh
                );

                int leftLine1 = 6 - deleteLine1;
                long newXBoardMidLow = LongBoardMap.insertBlackLine(
                        (xBoardMidLow << 10 * deleteLine1) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine1)) >> 10 * leftLine1), deleteKeyMidLow
                );

                long newXBoardLow = LongBoardMap.insertBlackLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLine1), deleteKeyLow);

                this.xBoardLow = newXBoardLow;
                this.xBoardMidLow = newXBoardMidLow & VALID_BOARD_RANGE;
                this.xBoardMidHigh = newXBoardMidHigh & VALID_BOARD_RANGE;
                this.xBoardHigh = newXBoardHigh & VALID_BOARD_RANGE;
            }
        } else {
            // Low & MidLow
            int deleteLine3_12 = deleteLine3 - 12;
            int leftLine3 = 6 - deleteLine3_12;
            long newXBoardHigh = LongBoardMap.insertBlackLine(
                    (xBoardMidLow << 10 * deleteLine3_12) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine3)) >> 10 * leftLine3), deleteKeyHigh
            );

            int deleteLine2_6 = deleteLine2 - 6;
            int leftLine2 = 6 - deleteLine2_6;
            long newXBoardMidHigh = LongBoardMap.insertBlackLine(
                    (xBoardMidLow << 10 * deleteLine2_6) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine2)) >> 10 * leftLine2), deleteKeyMidHigh
            );

            int leftLine1 = 6 - deleteLine1;
            long newXBoardMidLow = LongBoardMap.insertBlackLine(
                    (xBoardMidLow << 10 * deleteLine1) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine1)) >> 10 * leftLine1), deleteKeyMidLow
            );

            long newXBoardLow = LongBoardMap.insertBlackLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLine1), deleteKeyLow);

            this.xBoardLow = newXBoardLow;
            this.xBoardMidLow = newXBoardMidLow & VALID_BOARD_RANGE;
            this.xBoardMidHigh = newXBoardMidHigh & VALID_BOARD_RANGE;
            this.xBoardHigh = newXBoardHigh & VALID_BOARD_RANGE;
        }
    }

    private long extractDeleteKeyLow(long deleteKey) {
        return deleteKey & 0x4010040100401L;
    }

    private long extractDeleteKeyMidLow(long deleteKey) {
        return (deleteKey & 0x8020080200802L) >> 1;
    }

    private long extractDeleteKeyMidHigh(long deleteKey) {
        return (deleteKey & 0x10040100401004L) >> 2;
    }

    private long extractDeleteKeyHigh(long deleteKey) {
        return (deleteKey & 0x20080200802008L) >> 3;
    }

    @Override
    public void insertWhiteLineWithKey(long deleteKey) {
        long deleteKeyLow = extractDeleteKeyLow(deleteKey);
        int deleteLineLow = Long.bitCount(deleteKeyLow);

        long deleteKeyMidLow = extractDeleteKeyMidLow(deleteKey);
        int deleteLineMidLow = Long.bitCount(deleteKeyMidLow);

        long deleteKeyMidHigh = extractDeleteKeyMidHigh(deleteKey);
        int deleteLineMidHigh = Long.bitCount(deleteKeyMidHigh);

        long deleteKeyHigh = extractDeleteKeyHigh(deleteKey);

        int deleteLine1 = deleteLineLow;
        int deleteLine2 = deleteLineLow + deleteLineMidLow;
        int deleteLine3 = deleteLine2 + deleteLineMidHigh;

        if (deleteLine3 < 6) {
            // Low & MidLow & MidHigh & High
            int leftLine3 = 6 - deleteLine3;
            long newXBoardHigh = LongBoardMap.insertWhiteLine(
                    (xBoardHigh << 10 * deleteLine3) | ((xBoardMidHigh & BitOperators.getRowMaskAboveY(leftLine3)) >> 10 * leftLine3), deleteKeyHigh
            );

            int leftLine2 = 6 - deleteLine2;
            long newXBoardMidHigh = LongBoardMap.insertWhiteLine(
                    (xBoardMidHigh << 10 * deleteLine2) | ((xBoardMidLow & BitOperators.getRowMaskAboveY(leftLine2)) >> 10 * leftLine2), deleteKeyMidHigh
            );

            int leftLine1 = 6 - deleteLine1;
            long newXBoardMidLow = LongBoardMap.insertWhiteLine(
                    (xBoardMidLow << 10 * deleteLine1) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine1)) >> 10 * leftLine1), deleteKeyMidLow
            );

            long newXBoardLow = LongBoardMap.insertWhiteLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLine1), deleteKeyLow);

            this.xBoardLow = newXBoardLow;
            this.xBoardMidLow = newXBoardMidLow & VALID_BOARD_RANGE;
            this.xBoardMidHigh = newXBoardMidHigh & VALID_BOARD_RANGE;
            this.xBoardHigh = newXBoardHigh & VALID_BOARD_RANGE;
        } else if (deleteLine3 < 12) {
            // Low & MidLow & MidHigh
            int deleteLine3_6 = deleteLine3 - 6;
            int leftLine3 = 6 - deleteLine3_6;
            long newXBoardHigh = LongBoardMap.insertWhiteLine(
                    (xBoardMidHigh << 10 * deleteLine3_6) | ((xBoardMidLow & BitOperators.getRowMaskAboveY(leftLine3)) >> 10 * leftLine3), deleteKeyHigh
            );

            if (deleteLine2 < 6) {
                // Low & MidLow & MidHigh
                int leftLine2 = 6 - deleteLine2;
                long newXBoardMidHigh = LongBoardMap.insertWhiteLine(
                        (xBoardMidHigh << 10 * deleteLine2) | ((xBoardMidLow & BitOperators.getRowMaskAboveY(leftLine2)) >> 10 * leftLine2), deleteKeyMidHigh
                );

                int leftLine1 = 6 - deleteLine1;
                long newXBoardMidLow = LongBoardMap.insertWhiteLine(
                        (xBoardMidLow << 10 * deleteLine1) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine1)) >> 10 * leftLine1), deleteKeyMidLow
                );

                long newXBoardLow = LongBoardMap.insertWhiteLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLine1), deleteKeyLow);

                this.xBoardLow = newXBoardLow;
                this.xBoardMidLow = newXBoardMidLow & VALID_BOARD_RANGE;
                this.xBoardMidHigh = newXBoardMidHigh & VALID_BOARD_RANGE;
                this.xBoardHigh = newXBoardHigh & VALID_BOARD_RANGE;
            } else {
                // Low & MidLow
                int deleteLine2_6 = deleteLine2 - 6;
                int leftLine2 = 6 - deleteLine2_6;
                long newXBoardMidHigh = LongBoardMap.insertWhiteLine(
                        (xBoardMidLow << 10 * deleteLine2_6) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine2)) >> 10 * leftLine2), deleteKeyMidHigh
                );

                int leftLine1 = 6 - deleteLine1;
                long newXBoardMidLow = LongBoardMap.insertWhiteLine(
                        (xBoardMidLow << 10 * deleteLine1) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine1)) >> 10 * leftLine1), deleteKeyMidLow
                );

                long newXBoardLow = LongBoardMap.insertWhiteLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLine1), deleteKeyLow);

                this.xBoardLow = newXBoardLow;
                this.xBoardMidLow = newXBoardMidLow & VALID_BOARD_RANGE;
                this.xBoardMidHigh = newXBoardMidHigh & VALID_BOARD_RANGE;
                this.xBoardHigh = newXBoardHigh & VALID_BOARD_RANGE;
            }
        } else {
            // Low & MidLow
            int deleteLine3_12 = deleteLine3 - 12;
            int leftLine3 = 6 - deleteLine3_12;
            long newXBoardHigh = LongBoardMap.insertWhiteLine(
                    (xBoardMidLow << 10 * deleteLine3_12) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine3)) >> 10 * leftLine3), deleteKeyHigh
            );

            int deleteLine2_6 = deleteLine2 - 6;
            int leftLine2 = 6 - deleteLine2_6;
            long newXBoardMidHigh = LongBoardMap.insertWhiteLine(
                    (xBoardMidLow << 10 * deleteLine2_6) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine2)) >> 10 * leftLine2), deleteKeyMidHigh
            );

            int leftLine1 = 6 - deleteLine1;
            long newXBoardMidLow = LongBoardMap.insertWhiteLine(
                    (xBoardMidLow << 10 * deleteLine1) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLine1)) >> 10 * leftLine1), deleteKeyMidLow
            );

            long newXBoardLow = LongBoardMap.insertWhiteLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLine1), deleteKeyLow);

            this.xBoardLow = newXBoardLow;
            this.xBoardMidLow = newXBoardMidLow & VALID_BOARD_RANGE;
            this.xBoardMidHigh = newXBoardMidHigh & VALID_BOARD_RANGE;
            this.xBoardHigh = newXBoardHigh & VALID_BOARD_RANGE;
        }
    }

    @Override
    public void deleteLineWithKey(long deleteKey) {
        long deleteKeyLow = extractDeleteKeyLow(deleteKey);
        long deleteKeyMidLow = extractDeleteKeyMidLow(deleteKey);
        long deleteKeyMidHigh = extractDeleteKeyMidHigh(deleteKey);
        long deleteKeyHigh = extractDeleteKeyHigh(deleteKey);
        deleteLine(deleteKeyLow, deleteKeyMidLow, deleteKeyMidHigh, deleteKeyHigh);
    }

    @Override
    public void fillLine(int y) {
        switch (select(y)) {
            case Low: {
                xBoardLow |= getLineMask(y);
                return;
            }
            case MidLow: {
                xBoardMidLow |= getLineMask(y - FIELD_ROW_MID_LOW_BOARDER_Y);
                return;
            }
            case MidHigh: {
                xBoardMidHigh |= getLineMask(y - FIELD_ROW_MID_HIGH_BOARDER_Y);
                return;
            }
            case High: {
                xBoardHigh |= getLineMask(y - FIELD_ROW_HIGH_BOARDER_Y);
                return;
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    private long getLineMask(int y) {
        return 0x3ffL << y * FIELD_WIDTH;
    }

    @Override
    public int getBoardCount() {
        return 4;
    }

    @Override
    public long getBoard(int index) {
        switch (index) {
            case 0:
                return xBoardLow;
            case 1:
                return xBoardMidLow;
            case 2:
                return xBoardMidHigh;
            case 3:
                return xBoardHigh;
            default:
                return 0L;
        }
    }

    @Override
    public Field freeze(int maxHeight) {
        assert 0 < maxHeight && maxHeight <= 24;
        if (maxHeight <= 6)
            return new SmallField(xBoardLow);
        else if (maxHeight <= 12)
            return new MiddleField(xBoardLow, xBoardMidLow);
        return new LargeField(this);
    }

    @Override
    public Field freeze() {
        return new LargeField(this);
    }

    @Override
    public void merge(Field other) {
        int otherBoardCount = other.getBoardCount();
        assert 0 < otherBoardCount && otherBoardCount <= 4 : otherBoardCount;

        switch (otherBoardCount) {
            case 1: {
                xBoardLow |= other.getBoard(0);
                break;
            }
            case 2: {
                xBoardLow |= other.getBoard(0);
                xBoardMidLow |= other.getBoard(1);
                break;
            }
            default: {
                xBoardLow |= other.getBoard(0);
                xBoardMidLow |= other.getBoard(1);
                xBoardMidHigh |= other.getBoard(2);
                xBoardHigh |= other.getBoard(3);
                break;
            }
        }
    }

    @Override
    public void reduce(Field other) {
        int otherBoardCount = other.getBoardCount();
        assert 0 < otherBoardCount && otherBoardCount <= 4 : otherBoardCount;

        switch (otherBoardCount) {
            case 1: {
                xBoardLow &= ~other.getBoard(0);
                break;
            }
            case 2: {
                xBoardLow &= ~other.getBoard(0);
                xBoardMidLow &= ~other.getBoard(1);
                break;
            }
            default: {
                xBoardLow &= ~other.getBoard(0);
                xBoardMidLow &= ~other.getBoard(1);
                xBoardMidHigh &= ~other.getBoard(2);
                xBoardHigh &= ~other.getBoard(3);
                break;
            }
        }
    }

    @Override
    public boolean canMerge(Field other) {
        int otherBoardCount = other.getBoardCount();
        assert 0 < otherBoardCount && otherBoardCount <= 4 : otherBoardCount;

        switch (otherBoardCount) {
            case 1: {
                return (xBoardLow & other.getBoard(0)) == 0L;
            }
            case 2: {
                return (xBoardLow & other.getBoard(0)) == 0L
                        && (xBoardMidLow & other.getBoard(1)) == 0L;
            }
            default: {
                return (xBoardLow & other.getBoard(0)) == 0L
                        && (xBoardMidLow & other.getBoard(1)) == 0L
                        && (xBoardMidHigh & other.getBoard(2)) == 0L
                        && (xBoardHigh & other.getBoard(3)) == 0L;
            }
        }
    }

    @Override
    public int getUpperYWith4Blocks() {
        assert Long.bitCount(xBoardLow) + Long.bitCount(xBoardMidLow) + Long.bitCount(xBoardMidHigh) + Long.bitCount(xBoardHigh) == 4;
        if (xBoardHigh != 0L) {
            return getUpperY(xBoardHigh) + FIELD_ROW_HIGH_BOARDER_Y;
        } else if (xBoardMidHigh != 0L) {
            return getUpperY(xBoardMidHigh) + FIELD_ROW_MID_HIGH_BOARDER_Y;
        } else if (xBoardMidLow != 0L) {
            return getUpperY(xBoardMidLow) + FIELD_ROW_MID_LOW_BOARDER_Y;
        } else {
            return getUpperY(xBoardLow);
        }
    }

    private int getUpperY(long initBoard) {
        // initBoardを下から順にオフする
        long prevBoard = initBoard;
        long board = initBoard & (initBoard - 1);
        while (board != 0L) {
            prevBoard = board;
            board = board & (board - 1);
        }
        return BitOperators.bitToY(prevBoard);
    }

    @Override
    public int getLowerY() {
        if (xBoardLow != 0L) {
            long lowerBit = xBoardLow & (-xBoardLow);
            return BitOperators.bitToY(lowerBit);
        } else if (xBoardMidLow != 0L) {
            long lowerBit = xBoardMidLow & (-xBoardMidLow);
            return BitOperators.bitToY(lowerBit) + FIELD_ROW_MID_LOW_BOARDER_Y;
        } else if (xBoardMidHigh != 0L) {
            long lowerBit = xBoardMidHigh & (-xBoardMidHigh);
            return BitOperators.bitToY(lowerBit) + FIELD_ROW_MID_HIGH_BOARDER_Y;
        } else if (xBoardHigh != 0L) {
            long lowerBit = xBoardHigh & (-xBoardHigh);
            return BitOperators.bitToY(lowerBit) + FIELD_ROW_HIGH_BOARDER_Y;
        } else {
            return -1;
        }
    }

    @Override
    public void slideLeft(int slide) {
        assert 0 <= slide;
        long mask = BitOperators.getColumnMaskRightX(slide);
        xBoardLow = (xBoardLow & mask) >> slide;
        xBoardMidLow = (xBoardMidLow & mask) >> slide;
        xBoardMidHigh = (xBoardMidHigh & mask) >> slide;
        xBoardHigh = (xBoardHigh & mask) >> slide;
    }

    @Override
    public void slideRight(int slide) {
        assert 0 <= slide;
        long mask = BitOperators.getColumnMaskLeftX(FIELD_WIDTH - slide);
        xBoardLow = (xBoardLow & mask) << slide;
        xBoardMidLow = (xBoardMidLow & mask) << slide;
        xBoardMidHigh = (xBoardMidHigh & mask) << slide;
        xBoardHigh = (xBoardHigh & mask) << slide;
    }

    @Override
    public void slideDown() {
        long newXBoardLow = ((xBoardLow >>> FIELD_WIDTH) | (xBoardMidLow << 5 * FIELD_WIDTH)) & VALID_BOARD_RANGE;
        long newXBoardMidLow = ((xBoardMidLow >>> FIELD_WIDTH) | (xBoardMidHigh << 5 * FIELD_WIDTH)) & VALID_BOARD_RANGE;
        long newXBoardMidHigh = ((xBoardMidHigh >>> FIELD_WIDTH) | (xBoardHigh << 5 * FIELD_WIDTH)) & VALID_BOARD_RANGE;
        long newXBoardHigh = xBoardHigh >>> FIELD_WIDTH;

        this.xBoardLow = newXBoardLow;
        this.xBoardMidLow = newXBoardMidLow;
        this.xBoardMidHigh = newXBoardMidHigh;
        this.xBoardHigh = newXBoardHigh;
    }

    @Override
    public void slideDown(int slide) {
        if (slide <= FIELD_ROW_MID_HIGH_BOARDER_Y) {
            if (slide <= FIELD_ROW_MID_LOW_BOARDER_Y) {
                // <= 6
                long deleteKey = KeyOperators.getMaskForKeyBelowY(slide);
                deleteLine(deleteKey, 0L, 0L, 0L);
            } else {
                // <= 12
                long deleteKey = KeyOperators.getMaskForKeyBelowY(slide - FIELD_ROW_MID_LOW_BOARDER_Y);
                deleteLine(0x4010040100401L, deleteKey, 0L, 0L);
            }
        } else {
            if (slide <= FIELD_ROW_HIGH_BOARDER_Y) {
                // <= 18
                long deleteKey = KeyOperators.getMaskForKeyBelowY(slide - FIELD_ROW_MID_HIGH_BOARDER_Y);
                deleteLine(0x4010040100401L, 0x4010040100401L, deleteKey, 0L);
            } else if (slide <= MAX_FIELD_HEIGHT) {
                // <= 24
                long deleteKey = KeyOperators.getMaskForKeyBelowY(slide - FIELD_ROW_HIGH_BOARDER_Y);
                deleteLine(0x4010040100401L, 0x4010040100401L, 0x4010040100401L, deleteKey);
            } else {
                clearAll();
            }
        }
    }

    private void clearAll() {
        this.xBoardLow = 0L;
        this.xBoardMidLow = 0L;
        this.xBoardMidHigh = 0L;
        this.xBoardHigh = 0L;
    }

    @Override
    public void slideUpWithWhiteLine(int slide) {
        assert 0 <= slide : slide;
        if (slide < MAX_FIELD_HEIGHT) {
            insertWhiteLineWithKey(KeyOperators.getMaskForKeyBelowY(slide));
        } else {
            clearAll();
        }
    }

    @Override
    public void slideUpWithBlackLine(int slide) {
        assert 0 <= slide : slide;
        if (slide < MAX_FIELD_HEIGHT) {
            insertBlackLineWithKey(KeyOperators.getMaskForKeyBelowY(slide));
        } else {
            fillAll();
        }
    }

    private void fillAll() {
        this.xBoardLow = VALID_BOARD_RANGE;
        this.xBoardMidLow = VALID_BOARD_RANGE;
        this.xBoardMidHigh = VALID_BOARD_RANGE;
        this.xBoardHigh = VALID_BOARD_RANGE;
    }

    @Override
    public int getMinX() {
        long board = xBoardLow | xBoardMidLow | xBoardMidHigh | xBoardHigh;
        if (board == 0)
            return -1;

        board = board | (board >> 20);
        board = board | (board >> 20);
        board = board | (board >> 10);
        long lowerBit = board & (-board);
        return BitOperators.bitToX(lowerBit);
    }

    @Override
    public boolean contains(Field child) {
        switch (child.getBoardCount()) {
            case 1: {
                long childBoardLow = child.getBoard(0);
                return (xBoardLow & childBoardLow) == childBoardLow;
            }
            case 2: {
                long childBoardLow = child.getBoard(0);
                long childBoardMidLow = child.getBoard(1);
                return (xBoardLow & childBoardLow) == childBoardLow
                        && (xBoardMidLow & childBoardMidLow) == childBoardMidLow;
            }
            case 4: {
                long childBoardLow = child.getBoard(0);
                long childBoardMidLow = child.getBoard(1);
                long childBoardMidHigh = child.getBoard(2);
                long childBoardHigh = child.getBoard(3);
                return (xBoardLow & childBoardLow) == childBoardLow
                        && (xBoardMidLow & childBoardMidLow) == childBoardMidLow
                        && (xBoardMidHigh & childBoardMidHigh) == childBoardMidHigh
                        && (xBoardHigh & childBoardHigh) == childBoardHigh;
            }
            default:
                throw new IllegalStateException("Illegal board count: " + child.getBoardCount());
        }
    }

    @Override
    public void inverse() {
        xBoardLow = (~xBoardLow) & VALID_BOARD_RANGE;
        xBoardMidLow = (~xBoardMidLow) & VALID_BOARD_RANGE;
        xBoardMidHigh = (~xBoardMidHigh) & VALID_BOARD_RANGE;
        xBoardHigh = (~xBoardHigh) & VALID_BOARD_RANGE;
    }

    @Override
    public void mirror() {
        xBoardLow = KeyOperators.mirror(xBoardLow);
        xBoardMidLow = KeyOperators.mirror(xBoardMidLow);
        xBoardMidHigh = KeyOperators.mirror(xBoardMidHigh);
        xBoardHigh = KeyOperators.mirror(xBoardHigh);
    }

    @Override
    public void mask(Field maskField) {
        xBoardLow &= maskField.getBoard(0);
        xBoardMidLow &= maskField.getBoard(1);
        xBoardMidHigh &= maskField.getBoard(2);
        xBoardHigh &= maskField.getBoard(3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof LargeField) {
            LargeField that = (LargeField) o;
            return xBoardLow == that.xBoardLow
                    && xBoardMidLow == that.xBoardMidLow
                    && xBoardMidHigh == that.xBoardMidHigh
                    && xBoardHigh == that.xBoardHigh;
        }

        if (o instanceof SmallField) {
            SmallField that = (SmallField) o;
            return xBoardLow == that.getXBoard()
                    && xBoardMidLow == 0L
                    && xBoardMidHigh == 0L
                    && xBoardHigh == 0L;
        } else if (o instanceof MiddleField) {
            MiddleField that = (MiddleField) o;
            return xBoardLow == that.getXBoardLow()
                    && xBoardMidLow == that.getXBoardHigh()
                    && xBoardMidHigh == 0L
                    && xBoardHigh == 0L;
        } else if (o instanceof Field) {
            Field that = (Field) o;
            return FieldComparator.compareField(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = (int) (xBoardLow ^ (xBoardLow >>> 32));
        result = 31 * result + (int) (xBoardMidLow ^ (xBoardMidLow >>> 32));
        result = 31 * result + (int) (xBoardMidHigh ^ (xBoardMidHigh >>> 32));
        result = 31 * result + (int) (xBoardHigh ^ (xBoardHigh >>> 32));
        return result;
    }

    @Override
    public int compareTo(Field o) {
        return FieldComparator.compareField(this, o);
    }

    @Override
    public String toString() {
        return String.format("LargeField{low=%d, midlow=%d, midhigh=%d, high=%d}", xBoardLow, xBoardMidLow, xBoardMidHigh, xBoardHigh);
    }
}

enum Position {
    Low,
    MidLow,
    MidHigh,
    High,
}
