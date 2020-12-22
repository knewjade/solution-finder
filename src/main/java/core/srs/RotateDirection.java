package core.srs;

import java.util.Arrays;
import java.util.List;

public enum RotateDirection {
    Right,
    Left,
    Rotate180,
    ;

    public static RotateDirection reverse(RotateDirection direction) {
        switch (direction) {
            case Right:
                return Left;
            case Left:
                return Right;
            case Rotate180:
                return Rotate180;
        }
        throw new IllegalStateException();
    }

    public static List<RotateDirection> valuesNo180() {
        return Arrays.asList(Right, Left);
    }

    public static List<RotateDirection> valuesWith180() {
        return Arrays.asList(values());
    }
}
