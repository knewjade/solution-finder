package action.cache;

import action.memory.Memory;
import action.memory.ArrayMemory;
import action.memory.LargeMemory;
import action.memory.MiddleMemory;
import action.memory.SmallMemory;
import core.srs.Rotate;

import java.util.EnumMap;

public class MinimalLockedCache {
    private final EnumMap<Rotate, Memory> visitedMap = new EnumMap<>(Rotate.class);

    public MinimalLockedCache(int height) {
        for (Rotate rotate : Rotate.values())
            visitedMap.put(rotate, createMemory(height));
    }

    private Memory createMemory(int height) {
        if (height < 6)
            return new SmallMemory();
        else if (height < 12)
            return new MiddleMemory();
        else if (height < 24)
            return new LargeMemory();
        return new ArrayMemory(height);
    }

    public void clear() {
        for (Memory memory : visitedMap.values())
            memory.clear();
    }

    public void visit(int x, int y, Rotate rotate) {
        visitedMap.get(rotate).setTrue(x, y);
    }

    public boolean isVisit(int x, int y, Rotate rotate) {
        return visitedMap.get(rotate).get(x, y);
    }
}
