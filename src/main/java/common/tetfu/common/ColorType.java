package common.tetfu.common;

import java.util.HashMap;

public enum ColorType {
    Empty(0),
    I(1),
    L(2),
    O(3),
    Z(4),
    T(5),
    J(6),
    S(7),
    Gray(8);

    private static final HashMap<Integer, ColorType> map = new HashMap<>();

    static {
        for (ColorType type : ColorType.values())
            map.put(type.number, type);
    }

    public static ColorType parse(int number) {
        assert map.containsKey(number);
        return map.get(number);
    }

    private final int number;

    ColorType(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public static boolean isMinoBlock(ColorType colorType) {
        return colorType != Empty && colorType != Gray;
    }
}
