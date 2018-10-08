package util.fig;

import common.tetfu.common.ColorType;

import java.awt.*;
import java.util.EnumMap;

public enum FigColor {
    Background(ColorType.Empty, Color.BLACK, Color.BLACK),
    Frame(null, Color.ORANGE, Color.ORANGE),
    Line(null, new Color(0x333333), new Color(0x333333), new Color(0x333333)),
    I(ColorType.I, new Color(0x009999), new Color(0x33cccc), new Color(0x00ffff)),
    T(ColorType.T, new Color(0x990099), new Color(0xcc33cc), new Color(0xff00ff)),
    S(ColorType.S, new Color(0x009900), new Color(0x33cc33), new Color(0x00ff00)),
    Z(ColorType.Z, new Color(0x990000), new Color(0xcc3333), new Color(0xff0000)),
    L(ColorType.L, new Color(0x996600), new Color(0xcc9933), new Color(0xff9900)),
    J(ColorType.J, new Color(0x0000BB), new Color(0x3333cc), new Color(0x0000ff)),
    O(ColorType.O, new Color(0x999900), new Color(0xcccc33), new Color(0xffff00)),
    Gray(ColorType.Gray, new Color(0x999999), new Color(0xcccccc), new Color(0xffffff)),;

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
    private final Color strong1;
    private final Color strong2;

    FigColor(ColorType type, Color normal, Color strong1) {
        this(type, normal, strong1, strong1);
    }

    FigColor(ColorType type, Color normal, Color strong1, Color strong2) {
        this.type = type;
        this.normal = normal;
        this.strong1 = strong1;
        this.strong2 = strong2;
    }

    public Color getNormalColor() {
        return normal;
    }

    public Color getStrongColor() {
        return strong1;
    }

    public Color getStrong2Color() {
        return strong2;
    }
}
