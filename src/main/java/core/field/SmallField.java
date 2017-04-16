package core.field;

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
        int max = MAX_FIELD_HEIGHT - mino.getMaxY();
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
        long mask = getColumnBelowY(maxY);
        long column = mask << x;
        return (~xBoard & column) == 0L;
    }

    @Override
    public boolean isWallBetweenLeft(int x, int maxY) {
        long mask = getColumnBelowY(maxY);
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
        long mask = getColumnBelowY(maxY);
        long column = mask << x;
        return Long.bitCount(xBoard & column);
    }

    @Override
    public int getAllBlockCount() {
        return Long.bitCount(xBoard);
    }

    // y行より1列ブロックをマスクを取得する（y行を含まない）
    private long getColumnBelowY(int maxY) {
        switch (maxY) {
            case 0:
                return 0;
            case 1:
                return 0x1L;
            case 2:
                return 0x401L;
            case 3:
                return 0x100401L;
            case 4:
                return 0x40100401L;
            case 5:
                return 0x10040100401L;
            case 6:
                return 0x4010040100401L;
        }
        throw new IllegalStateException("No reachable");
    }

    @Override
    public int clearLine() {
        long deleteKey = clearLineReturnKey();
        return Long.bitCount(deleteKey);
    }

    @Override
    public long clearLineReturnKey() {
        long deleteKey = getDeleteKey(xBoard);
        this.xBoard = LongBoardMap.deleteLine(xBoard, deleteKey);
        return deleteKey;
    }

    @Override
    public void insertBlackLineWithKey(long deleteKey) {
        this.xBoard = LongBoardMap.insertBlackLine(xBoard, deleteKey);
    }

    private long getDeleteKey(long board) {
        long a1010101010 = 768614336404564650L;
        long b1 = (board & a1010101010) >>> 1 & board;
        long a0101010000 = 378672165735973200L;
        long b2 = (b1 & a0101010000) >>> 4 & b1;
        long a0000010100 = 22540009865236500L;
        long b3 = (b2 & a0000010100) >>> 2 & b2;
        long a0000000100 = 4508001973047300L;
        return (b3 & a0000000100) >>> 2 & b3;
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

    @Override
    public void merge(Field other) {
        xBoard |= other.getBoard(0);
    }
}
