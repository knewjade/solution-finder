package core.field;

import common.comparator.FieldComparator;
import core.mino.Mino;
import core.neighbor.OriginalPiece;

/**
 * フィールドの高さ height <= 6 であること
 * マルチスレッド非対応
 */
public class SmallField implements Field {
    public static final long VALID_BOARD_RANGE = 0xfffffffffffffffL;

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

    public long getXBoard() {
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
    public void put(Mino mino, int x, int y) {
        xBoard |= mino.getMask(x, y);
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
        xBoard &= ~mino.getMask(x, y);
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
    public boolean canReachOnHarddrop(Mino mino, int x, int startY) {
        int max = MAX_FIELD_HEIGHT - mino.getMinY();
        for (int y = startY + 1; y < max; y++)
            if (!canPut(mino, x, y))
                return false;
        return true;
    }

    @Override
    public boolean canReachOnHarddrop(OriginalPiece piece) {
        Field collider = piece.getHarddropCollider();
        return canMerge(collider);
    }

    private long getXMask(int x, int y) {
        return 1L << x + y * FIELD_WIDTH;
    }

    private long getLineMask(int y) {
        return 0x3ffL << y * FIELD_WIDTH;
    }

    @Override
    public boolean isEmpty(int x, int y) {
        return (xBoard & getXMask(x, y)) == 0L;
    }

    @Override
    public boolean existsAbove(int y) {
        long mask = VALID_BOARD_RANGE << y * FIELD_WIDTH;
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
        return BitOperators.isWallBetweenLeft(x, maxY, xBoard);
    }

    @Override
    public boolean canPut(Mino mino, int x, int y) {
        return MAX_FIELD_HEIGHT + 2 <= y || (xBoard & mino.getMask(x, y)) == 0L;
    }

    @Override
    public boolean isOnGround(Mino mino, int x, int y) {
        return y <= -mino.getMinY() || !canPut(mino, x, y - 1);
    }

    @Override
    public int getBlockCountBelowOnX(int x, int maxY) {
        long mask = BitOperators.getColumnOneLineBelowY(maxY);
        long column = mask << x;
        return Long.bitCount(xBoard & column);
    }

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
    public void fillLine(int y) {
        xBoard |= getLineMask(y);
    }

    @Override
    public Field freeze(int maxHeight) {
        return new SmallField(this);
    }

    @Override
    public long getBoard(int index) {
        if (index == 0)
            return xBoard;
        return 0L;
    }

    @Override
    public int getBoardCount() {
        return 1;
    }

    @Override
    public void merge(Field other) {
        xBoard |= other.getBoard(0);
    }

    @Override
    public void reduce(Field other) {
        xBoard &= ~other.getBoard(0);
    }

    @Override
    public boolean canMerge(Field other) {
        return (xBoard & other.getBoard(0)) == 0L;
    }

    @Override
    public int getUpperYWith4Blocks() {
        assert Long.bitCount(xBoard) == 4;
        // 下から順に3bit分、オフする
        long board = xBoard & (xBoard - 1);
        board = board & (board - 1);
        board = board & (board - 1);
        return BitOperators.bitToY(board);
    }

    @Override
    public int getLowerY() {
        if (xBoard == 0)
            return -1;
        long lowerBit = xBoard & (-xBoard);
        return BitOperators.bitToY(lowerBit);
    }

    @Override
    public void slideLeft(int slide) {
        assert 0 <= slide;
        long mask = BitOperators.getColumnMaskRightX(slide);
        xBoard = (xBoard & mask) >> slide;
    }

    @Override
    public void slideRight(int slide) {
        assert 0 <= slide;
        long mask = BitOperators.getColumnMaskLeftX(FIELD_WIDTH - slide);
        xBoard = (xBoard & mask) << slide;
    }

    @Override
    public void slideDown() {
        this.xBoard = xBoard >>> FIELD_WIDTH;
    }

    @Override
    public int getMinX() {
        if (xBoard == 0)
            return -1;
        long board = xBoard;
        board = board | (board >> 20);
        board = board | (board >> 20);
        board = board | (board >> 10);
        long lowerBit = board & (-board);
        return BitOperators.bitToX(lowerBit);
    }

    @Override
    public boolean contains(Field child) {
        assert child.getBoardCount() <= 2;
        switch (child.getBoardCount()) {
            case 1: {
                long childBoardLow = child.getBoard(0);
                return (xBoard & childBoardLow) == childBoardLow;
            }
            case 2: {
                long childBoardLow = child.getBoard(0);
                return (xBoard & childBoardLow) == childBoardLow
                        && child.getBoard(1) == 0L;
            }
            case 4: {
                long childBoardLow = child.getBoard(0);
                return (xBoard & childBoardLow) == childBoardLow
                        && child.getBoard(1) == 0L
                        && child.getBoard(2) == 0L
                        && child.getBoard(3) == 0L;
            }
            default:
                throw new IllegalStateException("Illegal board count: " + child.getBoardCount());
        }
    }

    @Override
    public void inverse() {
        xBoard = (~xBoard) & VALID_BOARD_RANGE;
    }

    @Override
    public void mirror() {
        xBoard = KeyOperators.mirror(xBoard);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof SmallField) {
            SmallField that = (SmallField) o;
            return xBoard == that.xBoard;
        }

        if (o instanceof MiddleField) {
            MiddleField that = (MiddleField) o;
            return that.getXBoardLow() == xBoard
                    && that.getXBoardHigh() == 0L;
        } else if (o instanceof LargeField) {
            LargeField that = (LargeField) o;
            return that.getXBoardLow() == xBoard
                    && that.getXBoardMidLow() == 0L
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
        return (int) (xBoard ^ (xBoard >>> 32));
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