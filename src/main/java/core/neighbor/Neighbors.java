package core.neighbor;

import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;

import java.util.ArrayList;
import java.util.Collection;

public class Neighbors {
    private static final int FIELD_WIDTH = 10;

    private final Neighbor[][][][] neighbors;
    private final int maxHeight;

    public Neighbors(MinoFactory minoFactory, MinoRotation minoRotation, OriginalPieceFactory pieceFactory) {
        int maxHeight = pieceFactory.getMaxHeight();
        this.neighbors = createNeighbors(pieceFactory, maxHeight);
        this.maxHeight = maxHeight;
        updateNeighbors(minoFactory, minoRotation);
    }

    private Neighbor[][][][] createNeighbors(OriginalPieceFactory pieceFactory, int maxHeight) {
        int maxBlock = Piece.values().length;
        int maxRotate = Rotate.values().length;
        Neighbor[][][][] neighbors = new Neighbor[maxBlock][maxRotate][maxHeight][FIELD_WIDTH];
        Collection<OriginalPiece> pieces = pieceFactory.create();
        for (OriginalPiece piece : pieces) {
            Mino mino = piece.getMino();
            Piece block = mino.getPiece();
            Rotate rotate = mino.getRotate();
            int x = piece.getX();
            int y = piece.getY();
            Neighbor neighbor = new Neighbor(piece);
            neighbors[block.getNumber()][rotate.getNumber()][y][x] = neighbor;
        }
        return neighbors;
    }

    private void updateNeighbors(MinoFactory minoFactory, MinoRotation minoRotation) {
        for (Neighbor[][][] eachBlocks : neighbors)
            for (Neighbor[][] eachRotate : eachBlocks)
                for (Neighbor[] eachY : eachRotate)
                    for (Neighbor current : eachY)
                        if (current != null)
                            updateNeighbor(minoFactory, minoRotation, current);
    }

    private void updateNeighbor(MinoFactory minoFactory, MinoRotation minoRotation, Neighbor current) {
        OriginalPiece piece = current.getPiece();
        Mino mino = piece.getMino();
        int x = piece.getX();
        int y = piece.getY();

        // 上と左右を更新
        current.updateUp(getNeighbor(mino, x, y + 1));
        current.updateLeft(getNeighbor(mino, x - 1, y));
        current.updateRight(getNeighbor(mino, x + 1, y));

        // 左回転でジャンプする先を更新
        int[][] patternsLeftDest = minoRotation.getLeftPatternsFrom(mino);
        Mino afterLeft = minoFactory.create(mino.getPiece(), mino.getRotate().getLeftRotate());
        ArrayList<Neighbor> neighborsLeftDest = createDestinations(afterLeft, x, y, patternsLeftDest);
        current.updateLeftRotateDestination(neighborsLeftDest);

        // 右回転でジャンプする先を更新
        int[][] patternsRightDest = minoRotation.getRightPatternsFrom(mino);
        Mino afterRight = minoFactory.create(mino.getPiece(), mino.getRotate().getRightRotate());
        ArrayList<Neighbor> neighborsRightDest = createDestinations(afterRight, x, y, patternsRightDest);
        current.updateRightRotateDestination(neighborsRightDest);

        // 左回転でここにジャンプしてくる元を更新
        Mino beforeLeft = minoFactory.create(mino.getPiece(), mino.getRotate().getRightRotate());
        int[][] patternsLeftSrc = minoRotation.getLeftPatternsFrom(beforeLeft);
        ArrayList<Neighbor> neighborsLeftSrc = createSources(beforeLeft, x, y, patternsLeftSrc);
        current.updateLeftRotateSource(neighborsLeftSrc);

        // 右回転でここにジャンプしてくる元を更新
        Mino beforeRight = minoFactory.create(mino.getPiece(), mino.getRotate().getLeftRotate());
        int[][] patternsRightSrc = minoRotation.getRightPatternsFrom(beforeRight);
        ArrayList<Neighbor> neighborsRightSrc = createSources(beforeRight, x, y, patternsRightSrc);
        current.updateRightRotateSource(neighborsRightSrc);
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
        return getNeighbor(mino.getPiece(), mino.getRotate(), x, y);
    }

    private Neighbor getNeighbor(Piece piece, Rotate rotate, int x, int y) {
        if (x < 0 || FIELD_WIDTH <= x || y < 0 || maxHeight <= y)
            return Neighbor.EMPTY_NEIGHBOR;
        Neighbor neighbor = neighbors[piece.getNumber()][rotate.getNumber()][y][x];
        return neighbor != null ? neighbor : Neighbor.EMPTY_NEIGHBOR;
    }

    public Neighbor get(Piece piece, Rotate rotate, int x, int y) {
        Neighbor neighbor = neighbors[piece.getNumber()][rotate.getNumber()][y][x];
        assert neighbor != null;
        return neighbor;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}
