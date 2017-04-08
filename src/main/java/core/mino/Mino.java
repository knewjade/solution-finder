package core.mino;

import core.srs.Rotate;

public class Mino {
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

    private final Block block;
    private final Rotate rotate;

    private final MinMax xMinMax;
    private final MinMax yMinMax;

    private final int[][] positions;

    private final long mask;
    public Mino(Block block, Rotate rotate) {
        this.block = block;
        this.rotate = rotate;
        this.xMinMax = calcXMinMax(block, rotate);
        this.yMinMax = calcYMinMax(block, rotate);
        this.positions = calcPositions(block, rotate);
        this.mask = calcMask();
    }

    private MinMax calcXMinMax(Block block, Rotate rotate) {
        switch (rotate) {
            case Spawn:
                return new MinMax(block.minX(), block.maxX());
            case Right:
                return new MinMax(block.minY(), block.maxY());
            case Reverse:
                return new MinMax(-block.maxX(), -block.minX());
            case Left:
                return new MinMax(-block.maxY(), -block.minY());
        }
        throw new IllegalStateException("No unreachable");
    }

    private MinMax calcYMinMax(Block block, Rotate rotate) {
        switch (rotate) {
            case Spawn:
                return new MinMax(block.minY(), block.maxY());
            case Right:
                return new MinMax(-block.maxX(), -block.minX());
            case Reverse:
                return new MinMax(-block.maxY(), -block.minY());
            case Left:
                return new MinMax(block.minX(), block.maxX());
        }
        throw new IllegalStateException("No unreachable");
    }

    private int[][] calcPositions(Block block, Rotate rotate) {
        switch (rotate) {
            case Spawn:
                return block.getPositions();
            case Right:
                return rotateRight(block.getPositions());
            case Reverse:
                return rotateReverse(block.getPositions());
            case Left:
                return rotateLeft(block.getPositions());
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

    public Block getBlock() {
        return block;
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
        int slide = x - MASK_CENTER_X + (y - MASK_CENTER_Y) * 10;
        long l = 0 < slide ? mask << slide : mask >>> -slide;
        return l;
    }
}
