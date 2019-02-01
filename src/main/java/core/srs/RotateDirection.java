package core.srs;

public enum RotateDirection {
    Right,
    Left,
    ;

    public static RotateDirection reverse(RotateDirection rotate) {
        switch (rotate) {
            case Right:
                return Left;
            case Left:
                return Right;
        }
        throw new IllegalStateException();
    }
}
