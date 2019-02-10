package core.srs;

import core.mino.Mino;

public class SuccessSpinResult implements SpinResult {
    private final Mino after;
    private final int x;
    private final int y;
    private final int clearedLine;
    private final int testPatternIndex;
    private final RotateDirection direction;

    SuccessSpinResult(Mino after, int x, int y, int clearedLine, int testPatternIndex, RotateDirection direction) {
        this.after = after;
        this.x = x;
        this.y = y;
        this.clearedLine = clearedLine;
        this.testPatternIndex = testPatternIndex;
        this.direction = direction;
    }

    @Override
    public int getToX() {
        return x;
    }

    @Override
    public int getToY() {
        return y;
    }

    @Override
    public Rotate getToRotate() {
        return after.getRotate();
    }

    @Override
    public int getTestPatternIndex() {
        return testPatternIndex;
    }

    @Override
    public int getClearedLine() {
        return clearedLine;
    }

    @Override
    public RotateDirection getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "SuccessSpinResult{" +
                "after=" + after +
                ", x=" + x +
                ", y=" + y +
                ", clearedLine=" + clearedLine +
                ", testPatternIndex=" + testPatternIndex +
                ", direction=" + direction +
                '}';
    }
}
