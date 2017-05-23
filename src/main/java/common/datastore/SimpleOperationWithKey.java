package common.datastore;

import core.mino.Mino;

public class SimpleOperationWithKey implements OperationWithKey {
    private final Mino mino;
    private final int x;
    private final int y;
    private final long needDeletedKey;
    private final long usingKey;

    // lowerY: 最も下にあるブロックのy座標
    public SimpleOperationWithKey(Mino mino, int x, long needDeletedKey, long usingKey, int lowerY) {
        this.mino = mino;
        this.x = x;
        this.y = lowerY - mino.getMinY();  // 回転軸のy座標 (ライン消去後のフィールドに対して置かれるべき位置)に直す
        this.needDeletedKey = needDeletedKey;
        this.usingKey = usingKey;
    }

    public SimpleOperationWithKey(Mino mino, int x, int y, long needDeletedKey, long usingKey) {
        this.mino = mino;
        this.x = x;
        this.y = y;
        this.needDeletedKey = needDeletedKey;
        this.usingKey = usingKey;
    }

    @Override
    public Mino getMino() {
        return mino;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public long getNeedDeletedKey() {
        return needDeletedKey;
    }

    @Override
    public long getUsingKey() {
        return usingKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleOperationWithKey that = (SimpleOperationWithKey) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        if (needDeletedKey != that.needDeletedKey) return false;
        return mino.equals(that.mino);
    }

    @Override
    public int hashCode() {
        int result = y;
        result = 10 * result + x;
        result = 7 * result + getMino().getBlock().getNumber();
        result = 4 * result + getMino().getRotate().getNumber();
        result = 31 * result + (int) (needDeletedKey ^ (needDeletedKey >>> 32));
        result = 31 * result + (int) (usingKey ^ (usingKey >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "OperationWithKey{" +
                "mino=" + mino.getBlock() + "-" + mino.getRotate() +
                ", x=" + x +
                ", y=" + y +
                ", needDeletedKey=" + needDeletedKey +
                '}';
    }
}
