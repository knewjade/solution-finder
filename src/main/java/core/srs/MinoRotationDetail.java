package core.srs;

import core.field.Field;
import core.mino.Mino;
import core.mino.MinoFactory;

public class MinoRotationDetail {
    private static final int FIELD_WIDTH = 10;

    private final MinoFactory minoFactory;
    private final MinoRotation minoRotation;

    public MinoRotationDetail(MinoFactory minoFactory, MinoRotation minoRotation) {
        this.minoFactory = minoFactory;
        this.minoRotation = minoRotation;
    }

    public int[][] getPatternsFrom(Mino current, RotateDirection direction) {
        return minoRotation.getPatternsFrom(current, direction);
    }

    public SpinResult getKicks(Field field, RotateDirection direction, Mino before, int beforeX, int beforeY) {
        int[][] offsets = minoRotation.getPatternsFrom(before, direction);
        Rotate afterRotate = before.getRotate().get(direction);
        Mino after = minoFactory.create(before.getPiece(), afterRotate);
        return getKicks(field, direction, before, after, beforeX, beforeY, offsets);
    }

    private SpinResult getKicks(Field field, RotateDirection direction, Mino before, Mino after, int beforeX, int beforeY, int[][] offsets) {
        int minX = -after.getMinX();
        int maxX = FIELD_WIDTH - after.getMaxX();
        int minY = -after.getMinY();
        for (int index = 0, offsetsLength = offsets.length; index < offsetsLength; index++) {
            int[] offset = offsets[index];
            int toX = beforeX + offset[0];
            int toY = beforeY + offset[1];
            if (minX <= toX && toX < maxX && minY <= toY && field.canPut(after, toX, toY)) {
                Field freeze = field.freeze();
                freeze.put(after, toX, toY);
                return createSuccessSpinResult(direction, before, after, index, toX, toY);
            }
        }
        return SpinResult.NONE;
    }

    private SuccessSpinResult createSuccessSpinResult(
            RotateDirection direction, Mino before, Mino after, int testPatternIndex, int toX, int toY
    ) {
        boolean privilegeSpins = minoRotation.isPrivilegeSpins(before, direction, testPatternIndex);
        return new SuccessSpinResult(after, toX, toY, testPatternIndex, direction, privilegeSpins);
    }

    public MinoRotation getMinoRotation() {
        return minoRotation;
    }
}
