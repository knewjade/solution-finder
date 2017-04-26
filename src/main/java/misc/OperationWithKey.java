package misc;

import core.mino.Mino;

public class OperationWithKey {
    private final Mino mino;
    private final int x;
    private final int y;
    private final long needDeletedKey;

    OperationWithKey(Mino mino, int x, long needDeletedKey, int lowerY) {
        this.mino = mino;
        this.x = x;
        this.y = lowerY - mino.getMinY();
        this.needDeletedKey = needDeletedKey;
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

    public long getNeedDeletedKey() {
        return needDeletedKey;
    }

    @Override
    public String toString() {
        return "OperationWithKey{" +
                "mino=" + mino +
                ", x=" + x +
                ", y=" + y +
                ", needDeletedKey=" + needDeletedKey +
                '}';
    }
}
