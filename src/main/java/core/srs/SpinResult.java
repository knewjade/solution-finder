package core.srs;

public interface SpinResult {
    SpinResult NONE = new NoneSpinResult();

    RotateDirection getDirection();

    int getToX();

    int getToY();

    Rotate getToRotate();

    int getTestPatternIndex();

    boolean isPrivilegeSpins();
}
