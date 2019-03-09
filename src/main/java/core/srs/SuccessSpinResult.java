package core.srs;

import core.mino.Mino;

public class SuccessSpinResult implements SpinResult {
    private final Mino after;
    private final int x;
    private final int y;
    private final int testPatternIndex;
    private final RotateDirection direction;

    SuccessSpinResult(Mino after, int x, int y, int testPatternIndex, RotateDirection direction) {
        this.after = after;
        this.x = x;
        this.y = y;
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
    public RotateDirection getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "SuccessSpinResult{" +
                "after=" + after +
                ", x=" + x +
                ", y=" + y +
                ", testPatternIndex=" + testPatternIndex +
                ", direction=" + direction +
                '}';
    }
}
