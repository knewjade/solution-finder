package util.fig;

import common.tetfu.common.ColorType;

import java.awt.*;
import java.util.EnumMap;

public enum FigColor {
    Background(ColorType.Empty, Color.BLACK, Color.BLACK),
    Frame(null, Color.ORANGE, Color.ORANGE),
    Line(null, new Color(0x333333), new Color(0x333333)),
    I(ColorType.I, new Color(0x00999A), new Color(0x24CCCD)),
    T(ColorType.T, new Color(0x9B009B), new Color(0xCE27CE)),
    S(ColorType.S, new Color(0x009B00), new Color(0x26CE22)),
    Z(ColorType.Z, new Color(0x9B0000), new Color(0xCE312D)),
    L(ColorType.L, new Color(0x9A6700), new Color(0xCD9A24)),
    J(ColorType.J, new Color(0x0000BE), new Color(0x3229CF)),
    O(ColorType.O, new Color(0x999A00), new Color(0xCCCE19)),
    Gray(ColorType.Gray, new Color(0x999999), new Color(0xCCCCCC)),;

    private static final EnumMap<ColorType, FigColor> map = new EnumMap<>(ColorType.class);

    static {
        for (FigColor color : FigColor.values())
            if (color.type != null)
                map.put(color.type, color);
    }

    public static FigColor parse(ColorType type) {
        assert map.containsKey(type);
        return map.get(type);
    }

    private final ColorType type;
    private final Color normal;
    private final Color strong;

    FigColor(ColorType type, Color normal, Color strong) {
        this.type = type;
        this.normal = normal;
        this.strong = strong;
    }

    public Color getNormalColor() {
        return normal;
    }

    public Color getStrongColor() {
        return strong;
    }
}
