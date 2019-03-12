package core.field;

import common.comparator.FieldComparator;
import core.mino.Mino;
import core.neighbor.OriginalPiece;

/**
 * フィールドの高さ height <= 12 であること
 * マルチスレッド非対応
 */
public class MiddleField implements Field {
    private static final int FIELD_WIDTH = 10;
    private static final int MAX_FIELD_HEIGHT = 12;
    private static final int FIELD_ROW_BOARDER_Y = 6;
    private static final long VALID_BOARD_RANGE = 0xfffffffffffffffL;

    // x,y: 最下位 (0,0), (1,0),  ... , (9,0), (0,1), ... 最上位 // フィールド範囲外は必ず0であること
    private long xBoardLow = 0;
    private long xBoardHigh = 0;

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

    long getXBoardLow() {
        return xBoardLow;
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

    private long getLineMask(int y) {
        return 0x3ffL << y * FIELD_WIDTH;
    }

    @Override
    public void put(Mino mino, int x, int y) {
        // Lowの更新が必要
        if (y + mino.getMinY() < FIELD_ROW_BOARDER_Y)
            xBoardLow |= mino.getMask(x, y);

        // Highの更新が必要
        if (FIELD_ROW_BOARDER_Y <= y + mino.getMaxY())
            xBoardHigh |= mino.getMask(x, y - FIELD_ROW_BOARDER_Y);
    }

    @Override
    public void put(OriginalPiece piece) {
        merge(piece.getMinoField());
    }

    @Override
    public boolean canPut(OriginalPiece piece) {
        return canMerge(piece.getMinoField());
    }

    @Override
    public void remove(Mino mino, int x, int y) {
        // Lowの更新が必要
        if (y + mino.getMinY() < FIELD_ROW_BOARDER_Y)
            xBoardLow &= ~mino.getMask(x, y);

        // Highの更新が必要
        if (FIELD_ROW_BOARDER_Y <= y + mino.getMaxY())
            xBoardHigh &= ~mino.getMask(x, y - FIELD_ROW_BOARDER_Y);
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
        if (y < FIELD_ROW_BOARDER_Y)
            return (xBoardLow & getXMask(x, y)) == 0L;
        else
            return (xBoardHigh & getXMask(x, y - FIELD_ROW_BOARDER_Y)) == 0L;
    }

    @Override
    public boolean exists(int x, int y) {
        return !isEmpty(x, y);
    }

    @Override
    public boolean existsAbove(int y) {
        if (MAX_FIELD_HEIGHT <= y) {
            return false;
        } else if (FIELD_ROW_BOARDER_Y <= y) {
            // Highで完結
            long mask = VALID_BOARD_RANGE << (y - FIELD_ROW_BOARDER_Y) * FIELD_WIDTH;
            return (xBoardHigh & mask) != 0L;
        } else {
            // すべて必要
            // Highのチェック
            if (xBoardHigh != 0L)
                return true;

            // Lowのチェック
            long mask = VALID_BOARD_RANGE << y * FIELD_WIDTH;
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
            long mask = BitOperators.getColumnOneLineBelowY(maxY) << x;
            return (~xBoardLow & mask) == 0L;
        } else {
            // すべて必要
            // Lowのチェック
            long maskLow = BitOperators.getColumnOneLineBelowY(FIELD_ROW_BOARDER_Y) << x;
            if ((~xBoardLow & maskLow) != 0L)
                return false;

            // Highのチェック
            long maskHigh = BitOperators.getColumnOneLineBelowY(maxY - FIELD_ROW_BOARDER_Y) << x;
            return (~xBoardHigh & maskHigh) == 0L;
        }
    }

    @Override
    public boolean isWallBetweenLeft(int x, int maxY) {
        if (maxY == 0) {
            return true;
        } else if (maxY <= FIELD_ROW_BOARDER_Y) {
            // Lowで完結
            return BitOperators.isWallBetweenLeft(x, maxY, xBoardLow);
        } else {
            // すべて必要
            // Lowのチェック
            if (!BitOperators.isWallBetweenLeft(x, FIELD_ROW_BOARDER_Y, xBoardLow))
                return false;

            // Highのチェック
            return BitOperators.isWallBetweenLeft(x, maxY - FIELD_ROW_BOARDER_Y, xBoardHigh);
        }
    }

    @Override
    public boolean canPut(Mino mino, int x, int y) {
        if (MAX_FIELD_HEIGHT + 2 <= y) {
            return true;
        } else if (y + mino.getMaxY() < FIELD_ROW_BOARDER_Y) {
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
        return y <= -mino.getMinY() || !canPut(mino, x, y - 1);
    }

    @Override
    public int getBlockCountBelowOnX(int x, int maxY) {
        if (maxY <= FIELD_ROW_BOARDER_Y) {
            // Lowで完結
            long mask = BitOperators.getColumnOneLineBelowY(maxY) << x;
            return Long.bitCount(xBoardLow & mask);
        } else {
            // すべて必要
            long maskLow = BitOperators.getColumnOneLineBelowY(FIELD_ROW_BOARDER_Y) << x;
            long maskHigh = BitOperators.getColumnOneLineBelowY(maxY - FIELD_ROW_BOARDER_Y) << x;
            return Long.bitCount(xBoardLow & maskLow) + Long.bitCount(xBoardHigh & maskHigh);
        }
    }

    @Override
    public int getBlockCountOnY(int y) {
        if (y < 6) {
            long mask = 0x3ffL << y * FIELD_WIDTH;
            long i = xBoardLow & mask;
            return Long.bitCount(i);
        } else {
            long mask = 0x3ffL << (y - 6) * FIELD_WIDTH;
            return Long.bitCount(xBoardHigh & mask);
        }
    }

    @Override
    public boolean existsBlockCountOnY(int y) {
        if (y < 6) {
            long mask = 0x3ffL << y * FIELD_WIDTH;
            return (xBoardLow & mask) != 0;
        } else {
            long mask = 0x3ffL << (y - 6) * FIELD_WIDTH;
            return (xBoardHigh & mask) != 0;
        }
    }

    @Override
    public int getNumOfAllBlocks() {
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
        long deleteKeyHigh = KeyOperators.getDeleteKey(xBoardHigh);

        deleteLine(deleteKeyLow, deleteKeyHigh);

        return deleteKeyLow | (deleteKeyHigh << 1);
    }

    @Override
    public long getFilledLine() {
        long deleteKeyLow = KeyOperators.getDeleteKey(xBoardLow);
        long deleteKeyHigh = KeyOperators.getDeleteKey(xBoardHigh);

        return deleteKeyLow | (deleteKeyHigh << 1);
    }

    @Override
    public long getUsingKey() {
        long usingKeyLow = KeyOperators.getUsingKey(xBoardLow);
        long usingKeyHigh = KeyOperators.getUsingKey(xBoardHigh);

        return usingKeyLow | (usingKeyHigh << 1);
    }

    @Override
    public void insertBlackLineWithKey(long deleteKey) {
        long deleteKeyLow = extractDeleteKeyLow(deleteKey);
        int deleteLineLow = Long.bitCount(deleteKeyLow);
        int leftLineLowY = 6 - deleteLineLow;
        long newXBoardLow = LongBoardMap.insertBlackLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLineLowY), deleteKeyLow);

        long deleteKeyHigh = extractDeleteKeyHigh(deleteKey);
        long newXBoardHigh = LongBoardMap.insertBlackLine((xBoardHigh << 10 * deleteLineLow) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLineLowY)) >> 10 * leftLineLowY), deleteKeyHigh);

        this.xBoardLow = newXBoardLow;
        this.xBoardHigh = newXBoardHigh & VALID_BOARD_RANGE;
    }

    private long extractDeleteKeyLow(long deleteKey) {
        return deleteKey & 0x4010040100401L;
    }

    private long extractDeleteKeyHigh(long deleteKey) {
        return (deleteKey & 0x8020080200802L) >> 1;
    }

    @Override
    public void insertWhiteLineWithKey(long deleteKey) {
        long deleteKeyLow = extractDeleteKeyLow(deleteKey);
        int deleteLineLow = Long.bitCount(deleteKeyLow);
        int leftLineLowY = 6 - deleteLineLow;
        long newXBoardLow = LongBoardMap.insertWhiteLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLineLowY), deleteKeyLow);

        long deleteKeyHigh = extractDeleteKeyHigh(deleteKey);
        long newXBoardHigh = LongBoardMap.insertWhiteLine((xBoardHigh << 10 * deleteLineLow) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLineLowY)) >> 10 * leftLineLowY), deleteKeyHigh);

        this.xBoardLow = newXBoardLow;
        this.xBoardHigh = newXBoardHigh & VALID_BOARD_RANGE;
    }

    @Override
    public void deleteLineWithKey(long deleteKey) {
        long deleteKeyLow = extractDeleteKeyLow(deleteKey);
        long deleteKeyHigh = extractDeleteKeyHigh(deleteKey);
        deleteLine(deleteKeyLow, deleteKeyHigh);
    }

    private void deleteLine(long deleteKeyLow, long deleteKeyHigh) {
        long newXBoardLow = LongBoardMap.deleteLine(xBoardLow, deleteKeyLow);
        long newXBoardHigh = LongBoardMap.deleteLine(xBoardHigh, deleteKeyHigh);

        int deleteLineLow = Long.bitCount(deleteKeyLow);

        this.xBoardLow = (newXBoardLow | (newXBoardHigh << (6 - deleteLineLow) * 10)) & VALID_BOARD_RANGE;
        this.xBoardHigh = newXBoardHigh >>> deleteLineLow * 10;
    }

    @Override
    public void fillLine(int y) {
        if (y < FIELD_ROW_BOARDER_Y)
            xBoardLow |= getLineMask(y);
        else
            xBoardHigh |= getLineMask(y - FIELD_ROW_BOARDER_Y);
    }

    @Override
    public int getBoardCount() {
        return 2;
    }

    @Override
    public long getBoard(int index) {
        switch (index) {
            case 0:
                return xBoardLow;
            case 1:
                return xBoardHigh;
            default:
                return 0L;
        }
    }

    @Override
    public Field freeze(int maxHeight) {
        assert 0 < maxHeight && maxHeight <= 12;
        if (maxHeight <= 6)
            return new SmallField(xBoardLow);
        return new MiddleField(this);
    }

    @Override
    public Field freeze() {
        return new MiddleField(this);
    }

    @Override
    public void merge(Field other) {
        int otherBoardCount = other.getBoardCount();
        assert 0 < otherBoardCount && otherBoardCount <= 4 : otherBoardCount;

        xBoardLow |= other.getBoard(0);
        if (1 < otherBoardCount)
            xBoardHigh |= other.getBoard(1);
    }

    @Override
    public void reduce(Field other) {
        int otherBoardCount = other.getBoardCount();
        assert 0 < otherBoardCount && otherBoardCount <= 4;

        xBoardLow &= ~other.getBoard(0);
        if (2 <= otherBoardCount)
            xBoardHigh &= ~other.getBoard(1);
    }

    @Override
    public boolean canMerge(Field other) {
        int otherBoardCount = other.getBoardCount();
        assert 0 < otherBoardCount && otherBoardCount <= 2;

        if (otherBoardCount == 1) {
            return (xBoardLow & other.getBoard(0)) == 0L;
        } else {
            return (xBoardLow & other.getBoard(0)) == 0L && (xBoardHigh & other.getBoard(1)) == 0L;
        }
    }

    @Override
    public int getUpperYWith4Blocks() {
        assert Long.bitCount(xBoardLow) + Long.bitCount(xBoardHigh) == 4;
        if (xBoardLow != 0L) {
            if (xBoardHigh != 0L) {
                // 何ビットかxBoardHighにある
                // xBoardHighを下から順にオフする
                long prevBoard = xBoardHigh;
                long board = xBoardHigh & (xBoardHigh - 1);
                while (board != 0L) {
                    prevBoard = board;
                    board = board & (board - 1);
                }
                return BitOperators.bitToY(prevBoard) + 6;
            } else {
                // すべてxBoardLowにある
                // xBoardLowを下から順に3bit分、オフする
                long board = xBoardLow & (xBoardLow - 1);
                board = board & (board - 1);
                board = board & (board - 1);
                return BitOperators.bitToY(board);
            }
        } else {
            // すべてxBoardHighにある
            // xBoardHighを下から順に3bit分、オフする
            long board = xBoardHigh & (xBoardHigh - 1);
            board = board & (board - 1);
            board = board & (board - 1);
            return BitOperators.bitToY(board) + 6;
        }
    }

    @Override
    public int getLowerY() {
        if (xBoardLow != 0L) {
            long lowerBit = xBoardLow & (-xBoardLow);
            return BitOperators.bitToY(lowerBit);
        } else if (xBoardHigh == 0L) {
            return -1;
        } else {
            long lowerBit = xBoardHigh & (-xBoardHigh);
            return BitOperators.bitToY(lowerBit) + 6;
        }
    }

    @Override
    public void slideLeft(int slide) {
        assert 0 <= slide;
        long mask = BitOperators.getColumnMaskRightX(slide);
        xBoardLow = (xBoardLow & mask) >> slide;
        xBoardHigh = (xBoardHigh & mask) >> slide;
    }

    @Override
    public void slideRight(int slide) {
        assert 0 <= slide;
        long mask = BitOperators.getColumnMaskLeftX(FIELD_WIDTH - slide);
        xBoardLow = (xBoardLow & mask) << slide;
        xBoardHigh = (xBoardHigh & mask) << slide;
    }

    @Override
    public void slideDown() {
        long newXBoardLow = ((xBoardLow >>> FIELD_WIDTH) | (xBoardHigh << 5 * FIELD_WIDTH)) & VALID_BOARD_RANGE;
        long newXBoardHigh = xBoardHigh >>> FIELD_WIDTH;

        this.xBoardLow = newXBoardLow;
        this.xBoardHigh = newXBoardHigh;
    }

    @Override
    public void slideDown(int slide) {
        assert 0 <= slide : slide;
        if (slide <= FIELD_ROW_BOARDER_Y) {
            long deleteKey = KeyOperators.getMaskForKeyBelowY(slide);
            deleteLine(deleteKey, 0L);
        } else if (slide <= MAX_FIELD_HEIGHT) {
            long deleteKey = KeyOperators.getMaskForKeyBelowY(slide - FIELD_ROW_BOARDER_Y);
            deleteLine(0x4010040100401L, deleteKey);
        } else {
            clearAll();
        }
    }

    private void clearAll() {
        this.xBoardLow = 0L;
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
        this.xBoardHigh = VALID_BOARD_RANGE;
    }

    @Override
    public int getMinX() {
        long board = xBoardLow | xBoardHigh;
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
                long childBoardHigh = child.getBoard(1);
                return (xBoardLow & childBoardLow) == childBoardLow
                        && (xBoardHigh & childBoardHigh) == childBoardHigh;
            }
            case 4: {
                long childBoardLow = child.getBoard(0);
                long childBoardHigh = child.getBoard(1);
                return (xBoardLow & childBoardLow) == childBoardLow
                        && (xBoardHigh & childBoardHigh) == childBoardHigh
                        && child.getBoard(2) == 0L
                        && child.getBoard(3) == 0L;
            }
            default:
                throw new IllegalStateException("Illegal board count: " + child.getBoardCount());
        }
    }

    @Override
    public void inverse() {
        xBoardLow = (~xBoardLow) & VALID_BOARD_RANGE;
        xBoardHigh = (~xBoardHigh) & VALID_BOARD_RANGE;
    }

    @Override
    public void mirror() {
        xBoardLow = KeyOperators.mirror(xBoardLow);
        xBoardHigh = KeyOperators.mirror(xBoardHigh);
    }

    @Override
    public void mask(Field maskField) {
        xBoardLow &= maskField.getBoard(0);
        xBoardHigh &= maskField.getBoard(1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof MiddleField) {
            MiddleField that = (MiddleField) o;
            return xBoardLow == that.xBoardLow
                    && xBoardHigh == that.xBoardHigh;
        }

        if (o instanceof SmallField) {
            SmallField that = (SmallField) o;
            return xBoardLow == that.getXBoard()
                    && xBoardHigh == 0L;
        } else if (o instanceof LargeField) {
            LargeField that = (LargeField) o;
            return that.getXBoardLow() == xBoardLow
                    && that.getXBoardMidLow() == xBoardHigh
                    && that.getXBoardMidHigh() == 0L
                    && that.getXBoardHigh() == 0L;
        } else if (o instanceof Field) {
            Field that = (Field) o;
            return FieldComparator.compareField(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = (int) (xBoardLow ^ (xBoardLow >>> 32));
        result = 31 * result + (int) (xBoardHigh ^ (xBoardHigh >>> 32));
        return result;
    }

    @Override
    public int compareTo(Field o) {
        return FieldComparator.compareField(this, o);
    }

    @Override
    public String toString() {
        return String.format("MiddleField{low=%d, high=%d}", xBoardLow, xBoardHigh);
    }
}
