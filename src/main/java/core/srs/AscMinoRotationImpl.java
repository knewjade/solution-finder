package core.srs;

import core.field.Field;
import core.mino.Mino;

public class AscMinoRotationImpl implements MinoRotation {
    private static final int FIELD_WIDTH = 10;

    private final Pattern cw = new Pattern(new int[][]{
            {0, 0}, {-1, 0}, {0, -1}, {-1, -1}, {0, -2}, {-1, -2}, {-2, 0}, {-2, -1}, {-2, -2}, {1, 0}, {1, -1}, {0, 1}, {-1, 1}, {-2, 1}, {1, -3}, {2, 0}, {0, 2}, {-1, 2}, {-2, 2}
    });

    private final Pattern ccw = new Pattern(new int[][]{
            {0, 0}, {1, 0}, {0, -1}, {1, -1}, {0, -2}, {1, -2}, {2, 0}, {2, -1}, {2, -2}, {-1, 0}, {-1, -1}, {0, 1}, {1, 1}, {2, 1}, {-1, -3}, {-2, 0}, {0, 2}, {1, 2}, {2, 2}
    });

    AscMinoRotationImpl() {
    }

    @Override
    public int[] getKicks(Field field, Mino before, Mino after, int x, int y, RotateDirection direction) {
        switch (direction) {
            case Right:
                return getKicksWithRightRotation(field, before, after, x, y);
            case Left:
                return getKicksWithLeftRotation(field, before, after, x, y);
        }
        throw new IllegalStateException();
    }

    @Override
    public int[] getKicksWithRightRotation(Field field, Mino before, Mino after, int x, int y) {
        return getKicks(field, x, y, after, cw);
    }

    private int[] getKicks(Field field, int x, int y, Mino after, Pattern pattern) {
        int[][] offsets = pattern.getOffsets();
        int minX = -after.getMinX();
        int maxX = FIELD_WIDTH - after.getMaxX();
        int minY = -after.getMinY();
        for (int[] offset : offsets) {
            int toX = x + offset[0];
            int toY = y + offset[1];
            if (minX <= toX && toX < maxX && minY <= toY && field.canPut(after, toX, toY))
                return offset;
        }
        return null;
    }

    @Override
    public int[] getKicksWithLeftRotation(Field field, Mino before, Mino after, int x, int y) {
        return getKicks(field, x, y, after, ccw);
    }

    @Override
    public int[][] getPatternsFrom(Mino current, RotateDirection direction) {
        switch (direction) {
            case Right:
                return getRightPatternsFrom(current);
            case Left:
                return getLeftPatternsFrom(current);
        }
        throw new IllegalStateException();
    }

    @Override
    public int[][] getRightPatternsFrom(Mino current) {
        return cw.getOffsets();
    }

    @Override
    public int[][] getLeftPatternsFrom(Mino current) {
        return ccw.getOffsets();
    }
}
