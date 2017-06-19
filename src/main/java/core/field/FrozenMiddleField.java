package core.field;

import common.comparator.FieldComparator;
import core.mino.Mino;

/**
 * フィールドの高さ height <= 12 であること
 */
public class FrozenMiddleField implements Field {
    private static final int FIELD_WIDTH = 10;
    private static final int MAX_FIELD_HEIGHT = 12;
    private static final int FIELD_ROW_BOARDER_Y = 6;

    private final long xBoardLow;
    private final long xBoardHigh;

    FrozenMiddleField(MiddleField src) {
        this.xBoardLow = src.getXBoardLow();
        this.xBoardHigh = src.getXBoardHigh();
    }

    @Override
    public int getMaxFieldHeight() {
        return MAX_FIELD_HEIGHT;
    }

    @Override
    public void setBlock(int x, int y) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public void removeBlock(int x, int y) {
        throw new UnsupportedOperationException("this is frozen");
    }

    private long getXMask(int x, int y) {
        return 1L << x + y * FIELD_WIDTH;
    }

    @Override
    public void putMino(Mino mino, int x, int y) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public void removeMino(Mino mino, int x, int y) {
        throw new UnsupportedOperationException("this is frozen");
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
        int max = MAX_FIELD_HEIGHT - mino.getMinY();
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
        long maskHigh = BitOperators.getColumnOneLineBelowY(maxYHigh);
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
            long mask = BitOperators.getColumnOneLineBelowY(maxY) << x;
            return Long.bitCount(xBoardLow & mask);
        } else {
            // すべて必要
            long maskLow = BitOperators.getColumnOneLineBelowY(FIELD_ROW_BOARDER_Y) << x;
            long maskHigh = BitOperators.getColumnOneLineBelowY(maxY - FIELD_ROW_BOARDER_Y) << x;
            return Long.bitCount(xBoardLow & maskLow) + Long.bitCount(xBoardHigh & maskHigh);
        }
    }

    // TODO: unittest
    @Override
    public int getBlockCountOnY(int y) {
        if (y < 6) {
            long mask = 0x3ff << y * FIELD_WIDTH;
            return Long.bitCount(xBoardLow & mask);
        } else {
            long mask = 0x3ff << (y - 6) * FIELD_WIDTH;
            return Long.bitCount(xBoardHigh & mask);
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
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public void insertBlackLineWithKey(long deleteKey) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public void insertWhiteLineWithKey(long deleteKey) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public int getBoardCount() {
        return 2;
    }

    @Override
    public long getBoard(int index) {
        assert 0 <= index && index < 2 : index;
        if (index == 0)
            return xBoardLow;
        return xBoardHigh;
    }

    @Override
    public Field freeze(int maxHeight) {
        return new MiddleField(xBoardLow, xBoardHigh);
    }

    // TODO: unittest
    @Override
    public void merge(Field other) {
        throw new UnsupportedOperationException("this is frozen");
    }

    // TODO: unittest
    @Override
    public void reduce(Field other) {
        throw new UnsupportedOperationException("this is frozen");
    }

    // TODO: unittest
    @Override
    public boolean canMerge(Field other) {
        int otherBoardCount = other.getBoardCount();
        assert 0 < otherBoardCount && otherBoardCount <= 2;

        if (otherBoardCount == 1) {
            return (xBoardLow & other.getBoard(0)) == 0L && xBoardHigh == 0L;
        } else {
            return (xBoardLow & other.getBoard(0)) == 0L && (xBoardHigh & other.getBoard(1)) == 0L;
        }
    }

    // TODO: unittest
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

    // TODO: unittest
    @Override
    public int getLowerY() {
        if (xBoardLow != 0L) {
            long lowerBit = xBoardLow & (-xBoardLow);
            return BitOperators.bitToY(lowerBit);
        } else {
            long lowerBit = xBoardHigh & (-xBoardHigh);
            return BitOperators.bitToY(lowerBit) + 6;
        }
    }

    // TODO: unittest
    @Override
    public void invert(int maxHeight) {
        throw new UnsupportedOperationException("this is frozen");
    }

    // TODO: unittest
    @Override
    public void slideLeft(int slide) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public Field fix() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (getClass() == o.getClass()) {
            FrozenMiddleField that = (FrozenMiddleField) o;
            return xBoardLow == that.xBoardLow && xBoardHigh == that.xBoardHigh;
        } else if (o instanceof MiddleField) {
            MiddleField that = (MiddleField) o;
            return xBoardLow == that.getXBoardLow() && xBoardHigh == that.getXBoardHigh();
        } else if (o instanceof SmallField) {
            SmallField that = (SmallField) o;
            return xBoardHigh == 0L && xBoardLow == that.getBoard(0);
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

    long getXBoardLow() {
        return xBoardLow;
    }

    long getXBoardHigh() {
        return xBoardHigh;
    }
}
