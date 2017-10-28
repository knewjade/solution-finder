package core.mino.piece;

import core.mino.Mino;

/**
 * フィールド幅は通常の10を想定
 */
public class Piece implements Comparable<Piece> {
    private final Mino mino;
    private final int x;
    private final int y;
    private final long deleteKey;

    // x, yは回転軸の座標
    public Piece(Mino mino, int x, int y, long deleteKey) {
        this.mino = mino;
        this.x = x;
        this.y = y;
        this.deleteKey = deleteKey;
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

    public long getDeleteKey() {
        return deleteKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Piece piece = (Piece) o;
        return x == piece.x && y == piece.y && deleteKey == piece.deleteKey && mino.equals(piece.mino);
    }

    @Override
    public int hashCode() {
        int result = y;
        result = 10 * result + x;
        result = 7 * result + mino.getBlock().getNumber();
        result = 4 * result + mino.getRotate().getNumber();
        result = 31 * result + (int) (deleteKey ^ (deleteKey >>> 32));
        return result;
    }

    @Override
    public int compareTo(Piece o) {
        int compareX = Integer.compare(x, o.x);
        if (compareX != 0)
            return compareX;

        int compareY = Integer.compare(y, o.y);
        if (compareY != 0)
            return compareY;

        int compareDeleteKey = Long.compare(deleteKey, o.deleteKey);
        if (compareDeleteKey != 0)
            return compareDeleteKey;

        return mino.compareTo(o.mino);
    }
}
