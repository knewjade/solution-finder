package core.action.reachable;

import common.datastore.action.Action;
import core.action.cache.MinimalLockedCache;
import core.field.Field;
import core.mino.Mino;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.Neighbor;
import core.neighbor.Neighbors;
import core.neighbor.OriginalPiece;
import core.srs.Rotate;

import java.util.List;

/**
 * マルチスレッド非対応
 */
public class LockedNeighborReachable implements Reachable {
    private final MinoShifter minoShifter;
    private final Neighbors neighbors;

    // temporary変数
    private Field field = null;
    private final MinimalLockedCache lockedCache;

    public LockedNeighborReachable(MinoShifter minoShifter, Neighbors neighbors, int maxY) {
        this.minoShifter = minoShifter;
        this.neighbors = neighbors;
        this.lockedCache = new MinimalLockedCache(maxY);
    }

    @Override
    public boolean checks(Field field, Mino mino, int x, int y, int appearY) {
        assert field.canPut(mino, x, y);

        this.field = field;
        lockedCache.clear();

        Piece piece = mino.getPiece();
        Rotate rotate = mino.getRotate();
        Neighbor neighbor = neighbors.get(piece, rotate, x, y);

        if (checkFirst(neighbor))
            return true;

        List<Action> actions = minoShifter.enumerateSameOtherActions(piece, rotate, x, y);
        for (Action action : actions)
            if (checkFirst(piece, action.getRotate(), action.getX(), action.getY()))
                return true;

        return false;
    }

    private boolean checkFirst(Neighbor neighbor) {
        return check(neighbor);
    }

    private boolean checkFirst(Piece piece, Rotate rotate, int x, int y) {
        Neighbor neighbor = neighbors.get(piece, rotate, x, y);
        return check(neighbor);
    }

    private boolean check(Neighbor neighbor) {
        // harddropでたどりつけるとき
        OriginalPiece originalPiece = neighbor.getOriginalPiece();
        if (field.canReachOnHarddrop(originalPiece))
            return true;

        // すでに訪問済みのとき
        int x = originalPiece.getX();
        int y = originalPiece.getY();
        Rotate rotate = originalPiece.getRotate();
        if (lockedCache.isVisit(x, y, rotate))
            return false;  // 訪問済みだがまだ探索中の場合は、他の探索でカバーできるためfalseを返却

        lockedCache.visit(x, y, rotate);

        // 上左右に移動
        List<Neighbor> moves = neighbor.getNextMovesSources();
        for (Neighbor move : moves)
            if (field.canPut(move.getOriginalPiece()) && check(move))
                return true;

        // 左回転でくる可能性がある場所を移動
        if (checkLeftRotation(neighbor))
            return true;

        // 右回転でくる可能性がある場所を移動
        if (checkRightRotation(neighbor))
            return true;

        return false;
    }

    private boolean checkLeftRotation(Neighbor current) {
        List<Neighbor> sources = current.getNextLeftRotateSources();
        for (Neighbor source : sources) {
            if (!field.canPut(source.getOriginalPiece()))
                continue;

            // もう一度回して戻ってくるか
            List<Neighbor> destinations = source.getNextLeftRotateDestinations();
            Neighbor destination = getDestination(destinations);
            if (current.equals(destination) && check(source)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkRightRotation(Neighbor current) {
        List<Neighbor> sources = current.getNextRightRotateSources();
        for (Neighbor source : sources) {
            if (!field.canPut(source.getOriginalPiece()))
                continue;

            // もう一度回して戻ってくるか
            List<Neighbor> destinations = source.getNextRightRotateDestinations();
            Neighbor destination = getDestination(destinations);
            if (current.equals(destination) && check(source)) {
                return true;
            }
        }
        return false;
    }

    private Neighbor getDestination(List<Neighbor> destinations) {
        for (Neighbor destination : destinations)
            if (field.canPut(destination.getOriginalPiece()))
                return destination;
        return Neighbor.EMPTY_NEIGHBOR;
    }
}
