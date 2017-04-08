package searcher.common.action;

import core.srs.Rotate;

/*
 * y < 24であること
 */
public class MinimalAction implements Comparable<Action>, Action {
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
        return x + MAX_FIELD_WIDTH * y + rotate.getNumber() * MAX_FIELD_BLOCK;
    }

    @Override
    public int compareTo(Action o) {
        if (this.y == o.getY()) {
            if (this.x == o.getX()) {
                return sign(this.rotate.getNumber(), o.getRotate().getNumber());
            } else {
                return sign(this.x, o.getX());
            }
        } else {
            return sign(this.y, o.getY());
        }
    }

    private int sign(int left, int right) {
        return left > right ? 1 : -1;
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
