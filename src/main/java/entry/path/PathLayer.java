package entry.path;

import java.util.HashMap;
import java.util.Map;

public enum PathLayer {
    Unique(1),
    Minimal(2),;

    private static final Map<Integer, PathLayer> map = new HashMap<>();

    static {
        for (PathLayer pathLayer : PathLayer.values())
            map.put(pathLayer.getNumber(), pathLayer);
    }

    public static PathLayer parse(int number) {
        return map.get(number);
    }

    private final int number;

    PathLayer(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public boolean contains(PathLayer layer) {
        return layer.number <= this.number;
    }

    public String getName() {
        return this.name().toLowerCase();
    }
}
