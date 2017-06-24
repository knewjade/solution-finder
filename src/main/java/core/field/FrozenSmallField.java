package core.field;

import common.comparator.FieldComparator;
import core.mino.Mino;

/**
 * フィールドの高さ height <= 6 であること
 */
public class FrozenSmallField implements Field {
    private static final int FIELD_WIDTH = 10;
    private static final int MAX_FIELD_HEIGHT = 6;

    private final long xBoard;

    FrozenSmallField(SmallField src) {
        this.xBoard = src.getXBoard();
    }

    FrozenSmallField(long board) {
        this.xBoard = board;
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

    private long getXMask(int x, int y) {
        return 1L << x + y * FIELD_WIDTH;
    }

    @Override
    public boolean isEmpty(int x, int y) {
        return (xBoard & getXMask(x, y)) == 0L;
    }

    @Override
    public boolean existsAbove(int y) {
        long mask = 0xffffffffffL << y * FIELD_WIDTH;
        return y < MAX_FIELD_HEIGHT && (xBoard & mask) != 0L;
    }

    @Override
    public boolean isPerfect() {
        return xBoard == 0L;
    }

    @Override
    public boolean isFilledInColumn(int x, int maxY) {
        if (maxY == 0)
            return true;
        long mask = BitOperators.getColumnOneLineBelowY(maxY);
        long column = mask << x;
        return (~xBoard & column) == 0L;
    }

    @Override
    public boolean isWallBetweenLeft(int x, int maxY) {
        long mask = BitOperators.getColumnOneLineBelowY(maxY);
        long reverseXBoard = ~xBoard;
        long column = mask << x;
        long right = reverseXBoard & column;
        long left = reverseXBoard & (column >>> 1);
        return ((left << 1) & right) == 0L;
    }

    @Override
    public boolean canPutMino(Mino mino, int x, int y) {
        return (xBoard & mino.getMask(x, y)) == 0L;
    }

    @Override
    public boolean isOnGround(Mino mino, int x, int y) {
        return y <= -mino.getMinY() || !canPutMino(mino, x, y - 1);
    }

    @Override
    public int getBlockCountBelowOnX(int x, int maxY) {
        long mask = BitOperators.getColumnOneLineBelowY(maxY);
        long column = mask << x;
        return Long.bitCount(xBoard & column);
    }

    // TODO: unittest
    @Override
    public int getBlockCountOnY(int y) {
        long mask = 0x3ffL << y * FIELD_WIDTH;
        return Long.bitCount(xBoard & mask);
    }

    @Override
    public int getNumOfAllBlocks() {
        return Long.bitCount(xBoard);
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
    public Field freeze(int maxHeight) {
        return new SmallField(xBoard);
    }

    @Override
    public long getBoard(int index) {
        return xBoard;
    }

    @Override
    public int getBoardCount() {
        return 1;
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
        return (xBoard & other.getBoard(0)) == 0L;
    }

    // TODO: unittest
    @Override
    public int getUpperYWith4Blocks() {
        assert Long.bitCount(xBoard) == 4;
        // 下から順に3bit分、オフする
        long board = xBoard & (xBoard - 1);
        board = board & (board - 1);
        board = board & (board - 1);
        return BitOperators.bitToY(board);
    }

    // TODO: unittest
    @Override
    public int getLowerY() {
        long lowerBit = xBoard & (-xBoard);
        return BitOperators.bitToY(lowerBit);
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
            FrozenSmallField that = (FrozenSmallField) o;
            return xBoard == that.xBoard;
        } else if (o instanceof MiddleField) {
            MiddleField that = (MiddleField) o;
            return that.getBoard(0) == xBoard && that.getBoard(1) == 0L;
        } else if (o instanceof Field) {
            Field that = (Field) o;
            return FieldComparator.compareField(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (int) (xBoard ^ (xBoard >>> 32));
    }

    @Override
    public int compareTo(Field o) {
        return FieldComparator.compareField(this, o);
    }

    @Override
    public String toString() {
        return String.format("FrozenSmallField{board=%d}", xBoard);
    }
}
