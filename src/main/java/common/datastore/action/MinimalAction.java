package common.datastore.action;

import core.srs.Rotate;

/*
 * y < 24であること
 */
public class MinimalAction implements Action, Comparable<Action> {
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rotate getRotate() {
        return rotate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Action)) return false;
        return Action.defaultEquals(this, (Action) o);
    }

    @Override
    public int hashCode() {
        return Action.defaultHashCode(x, y, rotate);
    }

    @Override
    public String toString() {
        return "Action{" +
                "x=" + x +
                ", y=" + y +
                ", rotate=" + rotate +
                '}';
    }

    @Override
    public int compareTo(Action o) {
        return Action.defaultCompareTo(this, o);
    }
}
