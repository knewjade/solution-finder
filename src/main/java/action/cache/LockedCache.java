package action.cache;

import action.cache.memory.*;
import core.srs.Rotate;

import java.util.EnumMap;

public class LockedCache {
    private final EnumMap<Rotate, Memory> visitedMap = new EnumMap<>(Rotate.class);
    private final EnumMap<Rotate, Memory> foundMap = new EnumMap<>(Rotate.class);

    public LockedCache(int height) {
        for (Rotate rotate : Rotate.values()) {
            visitedMap.put(rotate, createMemory(height));
            foundMap.put(rotate, createMemory(height));
        }
    }

    private Memory createMemory(int height) {
        if (height <= 6)
            return new SmallMemory();
        else if (height <= 12)
            return new MiddleMemory();
        else if (height <= 24)
            return new LargeMemory();
        return new ArrayMemory(height);
    }

    public void clear() {
        for (Memory memory : visitedMap.values())
            memory.clear();
        for (Memory memory : foundMap.values())
            memory.clear();
    }

    public void visit(int x, int y, Rotate rotate) {
        visitedMap.get(rotate).setTrue(x, y);
    }

    public boolean isVisit(int x, int y, Rotate rotate) {
        return visitedMap.get(rotate).get(x, y);
    }

    public void found(int x, int y, Rotate rotate) {
        foundMap.get(rotate).setTrue(x, y);
    }

    public boolean isFound(int x, int y, Rotate rotate) {
        return foundMap.get(rotate).get(x, y);
    }

    public void resetTrail() {
        for (Memory memory : visitedMap.values())
            memory.clear();
    }
}
