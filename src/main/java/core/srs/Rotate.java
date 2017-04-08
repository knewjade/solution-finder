package core.srs;

import java.util.EnumMap;

public enum Rotate {
    Spawn(0),
    Right(1),
    Reverse(2),
    Left(3),;

    private static final EnumMap<Rotate, Rotate> toLeft = new EnumMap<>(Rotate.class);
    private static final EnumMap<Rotate, Rotate> toRight = new EnumMap<>(Rotate.class);

    static {
        toLeft.put(Spawn, Left);
        toLeft.put(Left, Reverse);
        toLeft.put(Reverse, Right);
        toLeft.put(Right, Spawn);

        toRight.put(Spawn, Right);
        toRight.put(Right, Reverse);
        toRight.put(Reverse, Left);
        toRight.put(Left, Spawn);
    }

    private final int number;

    Rotate(int number) {
        this.number = number;
    }

    public Rotate getLeftRotate() {
        return toLeft.get(this);
    }

    public Rotate getRightRotate() {
        return toRight.get(this);
    }

    public int getNumber() {
        return this.number;
    }
}
