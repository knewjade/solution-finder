package common.datastore.action;

import core.srs.Rotate;

/*
 * y < 24であること
 */
public class MinimalAction implements Action {
    private static final int MAX_FIELD_WIDTH = 10;
    private static final int MAX_FIELD_HEIGHT = 24;
    private static final int MAX_FIELD_BLOCK = MAX_FIELD_WIDTH * MAX_FIELD_HEIGHT;

    public static MinimalAction create(int x, int y, Rotate rotate) {
        return new MinimalAction(x, y, rotate);
    }

    private final int x;
    private final int y;
    private final Rotate rotate;

    private MinimalAction(int x, int y, Rotate rotate) {
        assert y < 24 && rotate != null;
        this.x = x;
        this.y = y;
        this.rotate = rotate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinimalAction action = (MinimalAction) o;
        return x == action.x && y == action.y && rotate == action.rotate;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 24 * result + y;
        result = 4 * result + rotate.getNumber();
        return result;
    }

    @Override
    public int compareTo(Action o) {
        int rotateCompare = rotate.compareTo(o.getRotate());
        if (rotateCompare != 0)
            return rotateCompare;

        int xCompare = x - o.getX();
        if (xCompare != 0)
            return xCompare;

        return y - o.getY();
    }

    @Override
    public String toString() {
        return "Action{" +
                "x=" + x +
                ", y=" + y +
                ", rotate=" + rotate +
                '}';
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rotate getRotate() {
        return rotate;
    }
}
