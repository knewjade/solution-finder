package util.fig;

import java.awt.*;

public class FigColor {
    private final Color normal;
    private final Color strong1;
    private final Color strong2;

    public FigColor(Color normal, Color strong1, Color strong2) {
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
