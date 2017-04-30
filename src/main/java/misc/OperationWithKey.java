package misc;

import core.mino.Mino;

public class OperationWithKey {
    private final Mino mino;
    private final int x;
    private final int y;
    private final long needDeletedKey;

    // lowerY: 最も下にあるブロックのy座標
    OperationWithKey(Mino mino, int x, long needDeletedKey, int lowerY) {
        this.mino = mino;
        this.x = x;
        this.y = lowerY - mino.getMinY();  // 回転軸のy座標 (ライン消去後のフィールドに対して置かれるべき位置)に直す
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
