package tetfu;

import core.mino.Block;

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

    private final int number;

    ColorType(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public static boolean isBlock(ColorType colorType) {
        return colorType != Empty && colorType != Gray;
    }
}
