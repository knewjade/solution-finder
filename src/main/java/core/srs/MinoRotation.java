package core.srs;

import core.field.Field;
import core.mino.Mino;
import entry.common.kicks.factory.DefaultMinoRotationFactory;

public interface MinoRotation {
    DefaultMinoRotationFactory DEFAULT_MINO_ROTATION_FACTORY = new DefaultMinoRotationFactory();

    static MinoRotation create() {
        return DEFAULT_MINO_ROTATION_FACTORY.create();
    }

    int[] getKicks(Field field, Mino before, Mino after, int x, int y, RotateDirection direction);

    int[] getKicksWithRightRotation(Field field, Mino before, Mino after, int x, int y);

    int[] getKicksWithLeftRotation(Field field, Mino before, Mino after, int x, int y);

    int[] getKicksWith180Rotation(Field field, Mino before, Mino after, int x, int y);

    int[][] getPatternsFrom(Mino current, RotateDirection direction);

    int[][] getRightPatternsFrom(Mino current);

    int[][] getLeftPatternsFrom(Mino current);

    int[][] getRotate180PatternsFrom(Mino current);

    boolean isPrivilegeSpins(Mino before, RotateDirection direction, int testPatternIndex);

    default boolean noSupports180() {
        return !supports180();
    }

    boolean supports180();
}
