package common.datastore;

import core.mino.Mino;
import core.mino.Piece;
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

    @Override
    public Mino getMino() {
        return mino;
    }

    @Override
    public Piece getPiece() {
        return mino.getPiece();
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
        if (this == o)
            return true;

        if (!(o instanceof MinoOperationWithKey))
            return false;

        return MinoOperationWithKey.defaultEquals(this, (MinoOperationWithKey) o);
    }

    @Override
    public int hashCode() {
        return MinoOperationWithKey.defaultHash(mino, x, y, needDeletedKey);
    }

    @Override
    public String toString() {
        return "OperationWithKey{" +
                "mino=" + mino.getPiece() + "-" + mino.getRotate() +
                ", x=" + x +
                ", y=" + y +
                ", needDeletedKey=" + needDeletedKey +
                '}';
    }
}
