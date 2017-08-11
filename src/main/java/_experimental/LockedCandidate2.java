package _experimental;

import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.*;
import core.srs.MinoRotation;
import core.srs.Rotate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * マルチスレッド非対応
 */
public class LockedCandidate2 implements Candidate<Neighbor> {
    private static final int FIELD_WIDTH = 10;

    private final MinoFactory minoFactory;

    private final HashSet<Neighbor> isVisited = new HashSet<>();
    private final HashSet<Neighbor> isPassed = new HashSet<>();
    private final Neighbors neighbors;
    private final MinoShifter minoShifter;

    // temporary変数
    private Field field = null;

    public LockedCandidate2(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, PieceFactory pieceFactory) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.neighbors = new Neighbors(minoFactory, minoShifter, minoRotation, pieceFactory);
    }

    @Override
    public Set<Neighbor> search(Field field, Block block, int appearY) {
        this.field = field;

        HashSet<Neighbor> results = new HashSet<>();

        isPassed.clear();

        Set<Rotate> uniqueRotates = minoShifter.getUniqueRotates(block);

        for (Rotate rotate : uniqueRotates) {
            Mino mino = minoFactory.create(block, rotate);
            for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                for (int y = -mino.getMinY(); y < appearY - mino.getMaxY(); y++) {
                    Neighbor neighbor = neighbors.get(block, rotate, x, y);
                    if (field.canPut(neighbor.getPiece()) && field.isOnGround(mino, x, y)) {
                        loop(results, neighbor);
                    }
                }
            }
        }
        return results;
    }

    private void loop(HashSet<Neighbor> results, Neighbor neighbor) {
        isVisited.clear();
        if (check(neighbor)) {
            results.add(neighbor);
        } else {
            Piece piece = neighbor.getPiece();
            Mino mino = piece.getMino();
            Block block = mino.getBlock();
            List<Action> actions = minoShifter.enumerateSameOtherActions(block, mino.getRotate(), piece.getX(), piece.getY());
            for (Action action : actions) {
                Neighbor similar = neighbors.get(block, action.getRotate(), action.getX(), action.getY());
                if (check(similar))
                    results.add(similar);
            }
        }
    }

    private boolean check(Neighbor current) {
        Piece piece = current.getPiece();

        // ハードドロップで到達できるとき
        if (field.canReachOnHarddrop(piece))
            return true;

        if (isPassed.contains(current))
            return true;

        // すでに訪問済みのとき
        if (isVisited.contains(current))
            return false;  // 訪問済みだが結果が出てないときは他の探索でカバーできるためfalseを返却

        // 訪問済みにする
        isVisited.add(current);

        // 上と左右に移動
        List<Neighbor> nextMovesSources = current.getNextMovesSources();
        for (Neighbor next : nextMovesSources) {
            Piece nextPiece = next.getPiece();
            if (field.canPut(nextPiece) && check(next)) {
                isPassed.add(current);
                return true;
            }
        }

        // 左回転でくる可能性がある場所を移動
        List<Neighbor> nextLeftRotateSources = current.getNextLeftRotateSources();
        for (Neighbor source : nextLeftRotateSources) {
            if (!field.canPut(source.getPiece()))
                continue;

            // もう一度回して戻ってくるか
            List<Neighbor> destinations = source.getNextLeftRotateDestinations();
            Neighbor destination = getDestination(destinations);
            if (current.equals(destination) && check(source)) {
                isPassed.add(current);
                return true;
            }
        }

        // 右回転でくる可能性がある場所を移動
        List<Neighbor> nextRightRotateSources = current.getNextRightRotateSources();
        for (Neighbor source : nextRightRotateSources) {
            if (!field.canPut(source.getPiece()))
                continue;

            // もう一度回して戻ってくるか
            List<Neighbor> destinations = source.getNextRightRotateDestinations();
            Neighbor destination = getDestination(destinations);
            if (current.equals(destination) && check(source)) {
                isPassed.add(current);
                return true;
            }
        }

        return false;
    }

    private Neighbor getDestination(List<Neighbor> destinations) {
        for (Neighbor destination : destinations)
            if (field.canPut(destination.getPiece()))
                return destination;
        return Neighbor.EMPTY_NEIGHBOR;
    }
}
