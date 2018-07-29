package core.field;

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
            case MidHigh:{
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
            case High:{
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canPut(OriginalPiece piece) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Mino mino, int x, int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(OriginalPiece piece) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getYOnHarddrop(Mino mino, int x, int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canReachOnHarddrop(Mino mino, int x, int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canReachOnHarddrop(OriginalPiece piece) {
        throw new UnsupportedOperationException();
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
    public boolean existsAbove(int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPerfect() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFilledInColumn(int x, int maxY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWallBetweenLeft(int x, int maxY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canPut(Mino mino, int x, int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOnGround(Mino mino, int x, int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBlockCountBelowOnX(int x, int maxY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBlockCountOnY(int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getNumOfAllBlocks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int clearLine() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long clearLineReturnKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertBlackLineWithKey(long deleteKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertWhiteLineWithKey(long deleteKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fillLine(int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBoardCount() {
        return 4;
    }

    @Override
    public long getBoard(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Field freeze(int maxHeight) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void merge(Field field) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reduce(Field field) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canMerge(Field field) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getUpperYWith4Blocks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLowerY() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void slideLeft(int slide) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void slideRight(int slide) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void slideDown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Field child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void inverse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(Field o) {
        throw new UnsupportedOperationException();
    }
}

enum Position {
    Low,
    MidLow,
    MidHigh,
    High,
}
