package core.srs;

import core.mino.Block;

import java.util.EnumMap;

public enum Rotate {
    Spawn(0),
    Right(1),
    Reverse(2),
    Left(3),;

    private static final EnumMap<Rotate, Rotate> toLeft = new EnumMap<>(Rotate.class);
    private static final EnumMap<Rotate, Rotate> toRight = new EnumMap<>(Rotate.class);
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

        for (Rotate rotate : Rotate.values())
            ROTATE_MAP[rotate.getNumber()] = rotate;
    }

    public static Rotate getRotate(int number) {
        assert number < ROTATE_MAP.length;
        return ROTATE_MAP[number];
    }

    public static int getSize() {
        return ROTATE_MAP.length;
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
