package core.srs;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public enum Rotate {
    Spawn(0),
    Right(1),
    Reverse(2),
    Left(3),
    ;

    private static final EnumMap<Rotate, Rotate> toLeft = new EnumMap<>(Rotate.class);
    private static final EnumMap<Rotate, Rotate> toRight = new EnumMap<>(Rotate.class);
    private static final EnumMap<Rotate, Rotate> to180 = new EnumMap<>(Rotate.class);
    private static final Rotate[] ROTATE_MAP = new Rotate[Rotate.values().length];

    static {
        toLeft.put(Spawn, Left);
        toLeft.put(Left, Reverse);
        toLeft.put(Reverse, Right);
        toLeft.put(Right, Spawn);

        toRight.put(Spawn, Right);
        toRight.put(Right, Reverse);
        toRight.put(Reverse, Left);
        toRight.put(Left, Spawn);

        to180.put(Spawn, Reverse);
        to180.put(Right, Left);
        to180.put(Reverse, Spawn);
        to180.put(Left, Right);

        for (Rotate rotate : Rotate.values())
            ROTATE_MAP[rotate.getNumber()] = rotate;
    }

    public static Rotate getRotate(int number) {
        assert number < ROTATE_MAP.length;
        return ROTATE_MAP[number];
    }

    public static List<Rotate> valueList() {
        return Arrays.asList(Rotate.values());
    }

    public static int getSize() {
        return ROTATE_MAP.length;
    }

    private final int number;

    Rotate(int number) {
        this.number = number;
    }

    public Rotate get(RotateDirection direction) {
        switch (direction) {
            case Left:
                return getLeftRotate();
            case Right:
                return getRightRotate();
        }
        throw new IllegalStateException();
    }

    public Rotate getReverse(RotateDirection direction) {
        switch (direction) {
            case Left:
                return getRightRotate();
            case Right:
                return getLeftRotate();
        }
        throw new IllegalStateException();
    }

    public Rotate getLeftRotate() {
        return toLeft.get(this);
    }

    public Rotate getRightRotate() {
        return toRight.get(this);
    }

    public Rotate get180Rotate() {
        return to180.get(this);
    }

    public int getNumber() {
        return this.number;
    }
}
