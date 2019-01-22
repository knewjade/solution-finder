package core.srs;

import core.field.Field;
import core.mino.Mino;

public class MinoRotationDetail {
    private static final int FIELD_WIDTH = 10;

    private final MinoRotation minoRotation;

    public MinoRotationDetail(MinoRotation minoRotation) {
        this.minoRotation = minoRotation;
    }

    public int[][] getPatternsFrom(Mino current, RotateDirection direction) {
        return minoRotation.getPatternsFrom(current, direction);
    }

    public SpinResult getKicks(Field field, RotateDirection direction, Mino before, Mino after, int x, int y) {
        int[][] offsets = minoRotation.getPatternsFrom(before, direction);
        return getKicks(field, direction, after, x, y, offsets);
    }

    private SpinResult getKicks(Field field, RotateDirection direction, Mino after, int x, int y, int[][] offsets) {
        int minX = -after.getMinX();
        int maxX = FIELD_WIDTH - after.getMaxX();
        int minY = -after.getMinY();
        for (int index = 0, offsetsLength = offsets.length; index < offsetsLength; index++) {
            int[] offset = offsets[index];
            int toX = x + offset[0];
            int toY = y + offset[1];
            if (minX <= toX && toX < maxX && minY <= toY && field.canPut(after, toX, toY)) {
                Field freeze = field.freeze();
                freeze.put(after, toX, toY);
                int clearLine = freeze.clearLine();
                return new SuccessSpinResult(after, toX, toY, clearLine, index, direction);
            }
        }
        return SpinResult.NONE;
    }
}
