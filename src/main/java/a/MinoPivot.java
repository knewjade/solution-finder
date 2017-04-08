package a;

import core.mino.Mino;

public class MinoPivot {
    private final Mino mino;
    private final int x;
    private final int y;

    MinoPivot(Mino mino, Coordinate coordinate) {
        this(mino, coordinate.x, coordinate.y);
    }

    MinoPivot(Mino mino, int x, int y) {
        this.mino = mino;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "MinoPivot{" +
                "core.mino=" + mino +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public Mino getMino() {
        return mino;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
