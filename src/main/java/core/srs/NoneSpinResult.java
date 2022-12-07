package core.srs;

public class NoneSpinResult implements SpinResult {
    @Override
    public RotateDirection getDirection() {
        throw new IllegalStateException();
    }

    @Override
    public int getToX() {
        throw new IllegalStateException();
    }

    @Override
    public int getToY() {
        throw new IllegalStateException();
    }

    @Override
    public Rotate getToRotate() {
        throw new IllegalStateException();
    }

    @Override
    public int getTestPatternIndex() {
        throw new IllegalStateException();
    }

    @Override
    public boolean isPrivilegeSpins() { throw new IllegalStateException();}
}
