package core.mino;

import core.srs.Rotate;

public class Mino implements Comparable<Mino> {
    private static final int MASK_CENTER_X = 4;
    private static final int MASK_CENTER_Y = 2;

    private static class MinMax {
        private final int min;
        private final int max;

        private MinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }

    private static int[][] rotateRight(int[][] positions) {
        int[][] rotated = new int[positions.length][2];
        for (int index = 0; index < positions.length; index++) {
            int[] current = positions[index];
            rotated[index][0] = current[1];
            rotated[index][1] = -current[0];
        }
        return rotated;
    }

    private static int[][] rotateLeft(int[][] positions) {
        int[][] rotated = new int[positions.length][2];
        for (int index = 0; index < positions.length; index++) {
            int[] current = positions[index];
            rotated[index][0] = -current[1];
            rotated[index][1] = current[0];
        }
        return rotated;
    }

    private static int[][] rotateReverse(int[][] positions) {
        int[][] rotated = new int[positions.length][2];
        for (int index = 0; index < positions.length; index++) {
            int[] current = positions[index];
            rotated[index][0] = -current[0];
            rotated[index][1] = -current[1];
        }
        return rotated;
    }

    private final Piece piece;
    private final Rotate rotate;

    private final MinMax xMinMax;
    private final MinMax yMinMax;

    private final int[][] positions;

    private final long mask;

    public Mino(Piece piece, Rotate rotate) {
        this.piece = piece;
        this.rotate = rotate;
        this.xMinMax = calcXMinMax(piece, rotate);
        this.yMinMax = calcYMinMax(piece, rotate);
        this.positions = calcPositions(piece, rotate);
        this.mask = calcMask();
    }

    private MinMax calcXMinMax(Piece piece, Rotate rotate) {
        switch (rotate) {
            case Spawn:
                return new MinMax(piece.minX(), piece.maxX());
            case Right:
                return new MinMax(piece.minY(), piece.maxY());
            case Reverse:
                return new MinMax(-piece.maxX(), -piece.minX());
            case Left:
                return new MinMax(-piece.maxY(), -piece.minY());
        }
        throw new IllegalStateException("No unreachable");
    }

    private MinMax calcYMinMax(Piece piece, Rotate rotate) {
        switch (rotate) {
            case Spawn:
                return new MinMax(piece.minY(), piece.maxY());
            case Right:
                return new MinMax(-piece.maxX(), -piece.minX());
            case Reverse:
                return new MinMax(-piece.maxY(), -piece.minY());
            case Left:
                return new MinMax(piece.minX(), piece.maxX());
        }
        throw new IllegalStateException("No unreachable");
    }

    private int[][] calcPositions(Piece piece, Rotate rotate) {
        switch (rotate) {
            case Spawn:
                return piece.getPositions();
            case Right:
                return rotateRight(piece.getPositions());
            case Reverse:
                return rotateReverse(piece.getPositions());
            case Left:
                return rotateLeft(piece.getPositions());
        }
        throw new IllegalStateException("No unreachable");
    }

    // x,y: 最下位 (0,0), (1,0),  ... , (9,0), (0,1), ... 最上位
    private long calcMask() {
        long mask = 0L;
        for (int[] position : positions) {
            int x = MASK_CENTER_X + position[0];
            int y = MASK_CENTER_Y + position[1];
            mask |= 1L << (y * 10 + x);
        }
        return mask;
    }

    public Piece getPiece() {
        return piece;
    }

    public Rotate getRotate() {
        return rotate;
    }

    public int getMinX() {
        return xMinMax.min;
    }

    public int getMaxX() {
        return xMinMax.max;
    }

    public int getMinY() {
        return yMinMax.min;
    }

    public int getMaxY() {
        return yMinMax.max;
    }

    public int[][] getPositions() {
        return positions;
    }

    public long getMask(int x, int y) {
        assert 0 <= x && x < 10 : x;
        assert -4 < y && y < 8 : y;
        int slide = x - MASK_CENTER_X + (y - MASK_CENTER_Y) * 10;
        return 0 < slide ? mask << slide : mask >>> -slide;
    }

    @Override
    public String toString() {
        return "Mino{" +
                "piece=" + piece +
                ", rotate=" + rotate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mino mino = (Mino) o;
        return piece == mino.piece && rotate == mino.rotate;
    }

    @Override
    public int hashCode() {
        return piece.hashCode() ^ rotate.hashCode();
    }

    @Override
    public int compareTo(Mino o) {
        int compareBlock = piece.compareTo(o.piece);
        if (compareBlock != 0)
            return compareBlock;
        return rotate.compareTo(o.rotate);
    }
}
