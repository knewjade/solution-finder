package core.srs;

import core.field.Field;
import core.mino.Mino;

public interface MinoRotation {
    static MinoRotation create() {
        return new MinoRotationImpl();
    }

    int[] getKicks(Field field, Mino before, Mino after, int x, int y, RotateDirection direction);

    int[] getKicksWithRightRotation(Field field, Mino before, Mino after, int x, int y);

    int[] getKicksWithLeftRotation(Field field, Mino before, Mino after, int x, int y);

    int[][] getPatternsFrom(Mino current, RotateDirection direction);

    int[][] getRightPatternsFrom(Mino current);

    int[][] getLeftPatternsFrom(Mino current);
}
