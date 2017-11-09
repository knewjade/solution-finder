package core.action.candidate;

import common.datastore.action.Action;
import core.action.cache.LockedCache;
import core.action.cache.LockedNeighborCache;
import core.field.Field;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.Neighbor;
import core.neighbor.Neighbors;
import core.neighbor.OriginalPiece;
import core.neighbor.OriginalPieceFactory;
import core.srs.MinoRotation;
import core.srs.Rotate;
import searcher.common.From;

import java.util.*;

/**
 * マルチスレッド非対応
 */
public class LockedNeighborCandidate implements Candidate<Neighbor> {
    private static final int FIELD_WIDTH = 10;

    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final Neighbors neighbors;
    private final LockedNeighborCache lockedCache;

    // temporary変数
    private int appearY = 0;

    public LockedNeighborCandidate(MinoFactory minoFactory, MinoShifter minoShifter, Neighbors neighbors) {
        this(minoFactory, minoShifter, new MinoRotation(), neighbors, 4);
    }

    LockedNeighborCandidate(MinoFactory minoFactory, MinoRotation minoRotation, OriginalPieceFactory pieceFactory) {
        this(minoFactory, new MinoShifter(), new MinoRotation(), new Neighbors(minoFactory, minoRotation, pieceFactory), 4);
    }

    private LockedNeighborCandidate(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, Neighbors neighbors, int maxY) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.neighbors = neighbors;
        this.lockedCache = new LockedNeighborCache();
    }

    @Override
    public Set<Neighbor> search(Field field, Piece piece, int appearY) {
        // temporaryの初期化
        this.appearY = appearY;
        lockedCache.clear();

        HashSet<Neighbor> actions = new HashSet<>();
        for (Rotate rotate : Rotate.values()) {
            Mino mino = minoFactory.create(piece, rotate);
            for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                for (int y = appearY - mino.getMaxY() - 1; -mino.getMinY() <= y; y--) {
                    if (field.canPut(mino, x, y) && field.isOnGround(mino, x, y)) {
                        Neighbor current = this.neighbors.get(piece, rotate, x, y);
                        if (check(field, current, From.None)) {
                            Action action = minoShifter.createTransformedAction(piece, rotate, x, y);
                            Neighbor neighbor = this.neighbors.get(piece, action.getRotate(), action.getX(), action.getY());
                            actions.add(neighbor);
                        } else {
                            lockedCache.visit(current);
                        }
                        lockedCache.resetTrail();
                    }
                }
            }
        }

        return actions;
    }

    private boolean check(Field field, Neighbor neighbor, From from) {
        // 一番上までたどり着いたとき
        if (appearY <= neighbor.getY())
            return true;

        // すでに訪問済みのとき
        if (lockedCache.isVisited(neighbor))
            return lockedCache.isFound(neighbor);  // その時の結果を返却。訪問済みだが結果が出てないときは他の探索でカバーできるためfalseを返却

        lockedCache.visit(neighbor);

        // harddropでたどりつけるとき
        OriginalPiece originalPiece = neighbor.getOriginalPiece();
        if (field.canReachOnHarddrop(originalPiece)) {
            lockedCache.found(neighbor);
            return true;
        }

        // 上に移動
        Neighbor up = neighbor.getUp();
        if (up != Neighbor.EMPTY_NEIGHBOR && field.canPut(up.getOriginalPiece())) {
            if (check(field, up, From.None)) {
                lockedCache.found(neighbor);
                return true;
            }
        }

        // 左に移動
        Neighbor left = neighbor.getLeft();
        if (from != From.Left && left != Neighbor.EMPTY_NEIGHBOR && field.canPut(left.getOriginalPiece())) {
            if (check(field, left, From.Right)) {
                lockedCache.found(neighbor);
                return true;
            }
        }

        // 右に移動
        Neighbor right = neighbor.getRight();
        if (from != From.Right && right != Neighbor.EMPTY_NEIGHBOR && field.canPut(right.getOriginalPiece())) {
            if (check(field, right, From.Left)) {
                lockedCache.found(neighbor);
                return true;
            }
        }

        // 左回転でくる可能性がある場所を移動
        if (checkLeftRotation(field, neighbor)) {
            lockedCache.found(neighbor);
            return true;
        }

        // 右回転でくる可能性がある場所を移動
        if (checkRightRotation(field, neighbor)) {
            lockedCache.found(neighbor);
            return true;
        }

        return false;
    }

    private boolean checkLeftRotation(Field field, Neighbor current) {
        List<Neighbor> nextLeftRotateSources = current.getNextLeftRotateSources();
        for (Neighbor source : nextLeftRotateSources) {
            if (!field.canPut(source.getOriginalPiece()))
                continue;

            // もう一度回して戻ってくるか
            List<Neighbor> destinations = source.getNextLeftRotateDestinations();
            Neighbor destination = getDestination(field, destinations);
            if (current.equals(destination) && check(field, source, From.None))
                return true;
        }
        return false;
    }

    private boolean checkRightRotation(Field field, Neighbor current) {
        List<Neighbor> nextRightRotateSources = current.getNextRightRotateSources();
        for (Neighbor source : nextRightRotateSources) {
            if (!field.canPut(source.getOriginalPiece()))
                continue;

            // もう一度回して戻ってくるか
            List<Neighbor> destinations = source.getNextRightRotateDestinations();
            Neighbor destination = getDestination(field, destinations);
            if (current.equals(destination) && check(field, source, From.None))
                return true;
        }
        return false;
    }

    private Neighbor getDestination(Field field, List<Neighbor> destinations) {
        for (Neighbor destination : destinations)
            if (field.canPut(destination.getOriginalPiece()))
                return destination;
        return Neighbor.EMPTY_NEIGHBOR;
    }
}
