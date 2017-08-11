package core.mino;

import common.ActionParser;
import common.datastore.action.Action;
import core.srs.MinoRotation;
import core.srs.Rotate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Neighbors {
    private static final int FIELD_WIDTH = 10;

    private final HashMap<Integer, Neighbor> neighbors;
    private final Neighbor[][][][] array = new Neighbor[7][4][20][10];

    public Neighbors(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, PieceFactory pieceFactory) {
        this.neighbors = createNeighbors(pieceFactory);
        updateNeighbors(minoFactory, minoShifter, minoRotation);
    }

    private HashMap<Integer, Neighbor> createNeighbors(PieceFactory pieceFactory) {
        HashMap<Integer, Neighbor> neighbors = new HashMap<>();
        Collection<Piece> pieces = pieceFactory.getAllPieces();
        for (Piece piece : pieces) {
            Mino mino = piece.getMino();
            Block block = mino.getBlock();
            Rotate rotate = mino.getRotate();
            int x = piece.getX();
            int y = piece.getY();
            int indexKey = ActionParser.parseToInt(block, rotate, x, y);
            Neighbor neighbor = new Neighbor(piece);
            neighbors.put(indexKey, neighbor);
            array[block.getNumber()][rotate.getNumber()][x][y] = neighbor;
        }
        return neighbors;
    }

    private void updateNeighbors(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation) {
        for (Neighbor current : neighbors.values()) {
            Piece piece = current.getPiece();
            Mino mino = piece.getMino();
            int x = piece.getX();
            int y = piece.getY();

            // 上下左右を更新
            current.updateUp(getNeighbor(mino, x, y + 1));
            current.updateDown(getNeighbor(mino, x, y - 1));
            current.updateLeft(getNeighbor(mino, x - 1, y));
            current.updateRight(getNeighbor(mino, x + 1, y));

            // 左回転でジャンプする先を更新
            int[][] patternsLeftDest = minoRotation.getLeftPatternsFrom(mino);
            Mino afterLeft = minoFactory.create(mino.getBlock(), mino.getRotate().getLeftRotate());
            ArrayList<Neighbor> neighborsLeftDest = createDestinations(afterLeft, x, y, patternsLeftDest);
            current.updateLeftRotateDestination(neighborsLeftDest);

            // 右回転でジャンプする先を更新
            int[][] patternsRightDest = minoRotation.getRightPatternsFrom(mino);
            Mino afterRight = minoFactory.create(mino.getBlock(), mino.getRotate().getRightRotate());
            ArrayList<Neighbor> neighborsRightDest = createDestinations(afterRight, x, y, patternsRightDest);
            current.updateRightRotateDestination(neighborsRightDest);

            // 左回転でここにジャンプしてくる元を更新
            Mino beforeLeft = minoFactory.create(mino.getBlock(), mino.getRotate().getRightRotate());
            int[][] patternsLeftSrc = minoRotation.getLeftPatternsFrom(beforeLeft);
            ArrayList<Neighbor> neighborsLeftSrc = createSources(beforeLeft, x, y, patternsLeftSrc);
            current.updateLeftRotateSource(neighborsLeftSrc);

            // 右回転でここにジャンプしてくる元を更新
            Mino beforeRight = minoFactory.create(mino.getBlock(), mino.getRotate().getLeftRotate());
            int[][] patternsRightSrc = minoRotation.getRightPatternsFrom(beforeRight);
            ArrayList<Neighbor> neighborsRightSrc = createSources(beforeRight, x, y, patternsRightSrc);
            current.updateRightRotateSource(neighborsRightSrc);
        }
    }

    private ArrayList<Neighbor> createDestinations(Mino mino, int x, int y, int[][] patterns) {
        ArrayList<Neighbor> destinations = new ArrayList<>();
        for (int[] pattern : patterns) {
            Neighbor neighbor = getNeighbor(mino, x + pattern[0], y + pattern[1]);
            destinations.add(neighbor);
        }
        return destinations;
    }

    private ArrayList<Neighbor> createSources(Mino mino, int x, int y, int[][] patterns) {
        ArrayList<Neighbor> sources = new ArrayList<>();
        for (int[] pattern : patterns) {
            Neighbor neighbor = getNeighbor(mino, x - pattern[0], y - pattern[1]);
            sources.add(neighbor);
        }
        return sources;
    }

    private Neighbor getNeighbor(Mino mino, int x, int y) {
        return getNeighbor(mino.getBlock(), mino.getRotate(), x, y);
    }

    private Neighbor getNeighbor(Block block, Rotate rotate, int x, int y) {
        if (x < 0 || FIELD_WIDTH <= x || y < 0)
            return Neighbor.EMPTY_NEIGHBOR;
        int indexKey = ActionParser.parseToInt(block, rotate, x, y);
        return neighbors.getOrDefault(indexKey, Neighbor.EMPTY_NEIGHBOR);
    }

    public Neighbor get(Block block, Rotate rotate, int x, int y) {
//        int indexKey = ActionParser.parseToInt(block, rotate, x, y);
//        assert neighbors.containsKey(indexKey);
//        return neighbors.get(indexKey);
        Neighbor neighbor = array[block.getNumber()][rotate.getNumber()][x][y];
        assert neighbor != null;
        return neighbor;
    }
}
