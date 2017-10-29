package common.datastore;

import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;

public class FullOperationWithKey implements MinoOperationWithKey {
    private final Mino mino;
    private final int x;
    private final int y;
    private final long needDeletedKey;
    private final long usingKey;

    // lowerY: 最も下にあるブロックのy座標
    public FullOperationWithKey(Mino mino, int x, long needDeletedKey, long usingKey, int lowerY) {
        this.mino = mino;
        this.x = x;
        this.y = lowerY - mino.getMinY();  // 回転軸のy座標 (ライン消去後のフィールドに対して置かれるべき位置)に直す
        this.needDeletedKey = needDeletedKey;
        this.usingKey = usingKey;
    }

    public FullOperationWithKey(Mino mino, int x, int y, long needDeletedKey, long usingKey) {
        this.mino = mino;
        this.x = x;
        this.y = y;
        this.needDeletedKey = needDeletedKey;
        this.usingKey = usingKey;
    }

    public FullOperationWithKey(Mino mino, int x, int y, long needDeletedKey) {
        this.mino = mino;
        this.x = x;
        this.y = y;
        this.needDeletedKey = needDeletedKey;
        this.usingKey = -1L;
    }

    @Override
    public Mino getMino() {
        return mino;
    }

    @Override
    public Block getBlock() {
        return mino.getBlock();
    }

    @Override
    public Rotate getRotate() {
        return mino.getRotate();
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
        FullOperationWithKey that = (FullOperationWithKey) o;
        return x == that.x && y == that.y && needDeletedKey == that.needDeletedKey && mino.equals(that.mino);
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
