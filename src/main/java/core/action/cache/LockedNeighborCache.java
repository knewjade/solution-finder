package core.action.cache;

import core.neighbor.Neighbor;

import java.util.HashSet;

public class LockedNeighborCache {
    private final HashSet<Neighbor> isVisited = new HashSet<>();
    private final HashSet<Neighbor> isPassed = new HashSet<>();

    public void clear() {
        isPassed.clear();
    }

    public void visit(Neighbor neighbor) {
        isVisited.add(neighbor);
    }

    public boolean isVisited(Neighbor neighbor) {
        return isVisited.contains(neighbor);
    }

    public void found(Neighbor neighbor) {
        isPassed.add(neighbor);
    }

    public boolean isFound(Neighbor neighbor) {
        return isPassed.contains(neighbor);
    }

    public void resetTrail() {
        isVisited.clear();
    }
}
