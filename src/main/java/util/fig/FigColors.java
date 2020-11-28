package util.fig;

import common.tetfu.common.ColorType;

import java.awt.*;
import java.util.EnumMap;
import java.util.Properties;

public class FigColors {
    private static Color toColor(String code) {
        return Color.decode(code);
    }

    private static FigColor toBasicColor(String color, Properties properties) {
        Color normal = toColor(properties.getProperty(color + ".normal"));
        Color clear = toColor(properties.getProperty(color + ".clear"));
        Color piece = toColor(properties.getProperty(color + ".piece"));
        return new FigColor(normal, clear, piece);
    }

    private final EnumMap<ColorType, FigColor> map;
    private final Color border;
    private final Color sideFrame;
    private final Color holdBoxFrame;
    private final Color nextBoxFrame;

    public FigColors(Properties properties) {
        EnumMap<ColorType, FigColor> map = new EnumMap<>(ColorType.class);
        {
            map.put(ColorType.T, toBasicColor("T", properties));
            map.put(ColorType.I, toBasicColor("I", properties));
            map.put(ColorType.O, toBasicColor("O", properties));
            map.put(ColorType.S, toBasicColor("S", properties));
            map.put(ColorType.Z, toBasicColor("Z", properties));
            map.put(ColorType.L, toBasicColor("L", properties));
            map.put(ColorType.J, toBasicColor("J", properties));
        }
        {
            Color normal = toColor(properties.getProperty("Gray.normal"));
            Color clear = toColor(properties.getProperty("Gray.clear"));
            Color piece = Color.BLACK;
            map.put(ColorType.Gray, new FigColor(normal, clear, piece));
        }
        {
            Color normal = toColor(properties.getProperty("Empty"));
            Color clear = Color.BLACK;
            Color piece = Color.BLACK;
            map.put(ColorType.Empty, new FigColor(normal, clear, piece));
        }

        this.map = map;

        {
            this.border = toColor(properties.getProperty("Border"));
        }
        {
            String sideFrame = properties.getProperty("SideFrame");
            this.sideFrame = sideFrame != null ? toColor(sideFrame) : new Color(0x333333);
        }
        {
            String boxFrame = properties.getProperty("BoxFrame");
            this.holdBoxFrame = boxFrame != null ? toColor(boxFrame) : new Color(0xdddddd);
            this.nextBoxFrame = boxFrame != null ? toColor(boxFrame) : new Color(0x999999);
        }
    }

    public Color nextBoxBorder() {
        return nextBoxFrame;
    }

    public Color line() {
        return border;
    }

    public FigColor background() {
        return map.get(ColorType.Empty);
    }

    public FigColor parse(ColorType type) {
        return map.get(type);
    }

    public Color holdFrame() {
        return holdBoxFrame;
    }

    public Color sideFrame() {
        return sideFrame;
    }
}
