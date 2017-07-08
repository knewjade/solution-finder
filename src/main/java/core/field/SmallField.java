package core.field;

import common.comparator.FieldComparator;
import core.mino.Mino;

/**
 * フィールドの高さ height <= 6 であること
 * マルチスレッド非対応
 */
public class SmallField implements Field {
    private static final int FIELD_WIDTH = 10;
    private static final int MAX_FIELD_HEIGHT = 6;

    private long xBoard = 0; // x,y: 最下位 (0,0), (1,0),  ... , (9,0), (0,1), ... 最上位 // フィールド範囲外は必ず0であること

    public SmallField() {
    }

    private SmallField(SmallField src) {
        this.xBoard = src.xBoard;
    }

    public SmallField(long xBoard) {
        this.xBoard = xBoard;
    }

    long getXBoard() {
        return xBoard;
    }

    @Override
    public int getMaxFieldHeight() {
        return MAX_FIELD_HEIGHT;
    }

    @Override
    public void setBlock(int x, int y) {
        xBoard |= getXMask(x, y);
    }

    @Override
    public void removeBlock(int x, int y) {
        xBoard &= ~getXMask(x, y);
    }

    @Override
    public void putMino(Mino mino, int x, int y) {
        xBoard |= mino.getMask(x, y);
    }

    @Override
    public void removeMino(Mino mino, int x, int y) {
        xBoard &= ~mino.getMask(x, y);
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
        long deleteKey = KeyOperators.getDeleteKey(xBoard);
        this.xBoard = LongBoardMap.deleteLine(xBoard, deleteKey);
        return deleteKey;
    }

    @Override
    public void insertBlackLineWithKey(long deleteKey) {
        this.xBoard = LongBoardMap.insertBlackLine(xBoard, deleteKey);
    }

    @Override
    public void insertWhiteLineWithKey(long deleteKey) {
        this.xBoard = LongBoardMap.insertWhiteLine(xBoard, deleteKey);
    }

    @Override
    public Field freeze(int maxHeight) {
        return new SmallField(this);
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
        xBoard |= other.getBoard(0);
    }

    // TODO: unittest
    @Override
    public void reduce(Field other) {
        xBoard &= ~other.getBoard(0);
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
        xBoard = ~xBoard & BitOperators.getRowMaskBelowY(maxHeight);
    }

    // TODO: unittest
    @Override
    public void slideLeft(int slide) {
        long mask = BitOperators.getColumnMaskRightX(slide);
        xBoard = (xBoard & mask) >> slide;
    }

    @Override
    public Field fix() {
        return new FrozenSmallField(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof SmallField) {
            SmallField that = (SmallField) o;
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
        throw new UnsupportedOperationException("this is mutable object");
    }

    @Override
    public int compareTo(Field o) {
        return FieldComparator.compareField(this, o);
    }


    @Override
    public String toString() {
        return String.format("SmallField{board=%d}", xBoard);
    }
}