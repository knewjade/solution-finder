package concurrent;

import core.action.reachable.LockedNeighborReachable;
import core.action.reachable.Reachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.neighbor.Neighbors;
import core.neighbor.OriginalPieceFactory;
import core.srs.MinoRotation;

public class LockedNeighborReachableThreadLocal extends ThreadLocal<Reachable> {
    private final MinoShifter minoShifter;
    private final int maxY;
    private Neighbors neighbors;

    public LockedNeighborReachableThreadLocal(int maxY) {
        this(new MinoFactory(), new MinoShifter(), new MinoRotation(), maxY);
    }

    private LockedNeighborReachableThreadLocal(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY) {
        OriginalPieceFactory pieceFactory = new OriginalPieceFactory(maxY + 3);
        this.minoShifter = minoShifter;
        this.neighbors = new Neighbors(minoFactory, minoRotation, pieceFactory);
        this.maxY = maxY;
    }

    @Override
    protected LockedNeighborReachable initialValue() {
        return new LockedNeighborReachable(minoShifter, neighbors, maxY);
    }
}
