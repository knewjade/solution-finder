package util.fig;

public class FigSetting {
    private final int fieldWidthBlock = 10;

    private final int nextBoxSize = 47;
    private final int nextBoxMargin = 5;

    private final int nextBlockSize = 10;
    private final int nextBlockMargin = 1;

    private final FrameType frameType;
    private final int fieldHeightBlock;
    private final int nextBoxCount;

    private final int fieldBlockSize;
    private final int fieldBlockMargin;

    public FigSetting(FrameType frameType, int fieldHeightBlock, int nextBoxCount) {
        this(frameType, fieldHeightBlock, nextBoxCount, 32, 2);
    }

    public FigSetting(FrameType frameType, int fieldHeightBlock, int nextBoxCount, int fieldBlockSize, int fieldBlockMargin) {
        this.frameType = frameType;
        this.fieldHeightBlock = fieldHeightBlock;
        this.nextBoxCount = nextBoxCount;
        this.fieldBlockSize = fieldBlockSize;
        this.fieldBlockMargin = fieldBlockMargin;
    }

    public int getScreenWidth() {
        switch (frameType) {
            case NoFrame:
                return getFieldWidthPx();
            case Right:
                return getFieldWidthPx() - fieldBlockMargin + nextBoxSize + nextBoxMargin * 2;
            case Basic:
                return getFieldWidthPx() - 2 * fieldBlockMargin + nextBoxSize * 2 + nextBoxMargin * 4;
        }
        throw new IllegalStateException("No reachable");
    }

    public int getFieldWidthPx() {
        return (fieldBlockSize + fieldBlockMargin) * fieldWidthBlock + fieldBlockMargin;
    }

    public int getScreenHeight() {
        return getFieldHeightPx();
    }

    private int getFieldHeightPx() {
        return (fieldBlockSize + fieldBlockMargin) * fieldHeightBlock + fieldBlockMargin;
    }

    public int getFieldHeightBlock() {
        return fieldHeightBlock;
    }

    public int getFieldWidthBlock() {
        return fieldWidthBlock;
    }

    public int getFieldBlockSize() {
        return fieldBlockSize;
    }

    public int getFieldBlockMargin() {
        return fieldBlockMargin;
    }

    public int getNextBoxSize() {
        return nextBoxSize;
    }

    public int getNextBoxMargin() {
        return nextBoxMargin;
    }

    public int geNextBoxCount() {
        return nextBoxCount;
    }

    public int getNextBlockSize() {
        return nextBlockSize;
    }

    public int getNextBlockMargin() {
        return nextBlockMargin;
    }
}
