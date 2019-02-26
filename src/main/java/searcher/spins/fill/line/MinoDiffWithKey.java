package searcher.spins.fill.line;

import core.mino.Mino;

import java.util.Objects;

class MinoDiffWithKey {
    private final Mino mino;
    private final int minX;
    private final int cy;
    private final long needDeletedKey;
    private final int blockCount;
    private final int dy;

    MinoDiffWithKey(Mino mino, int minX, int cy, long needDeletedKey, int blockCount, int dy) {
        this.mino = mino;
        this.minX = minX;
        this.cy = cy;
        this.needDeletedKey = needDeletedKey;
        this.blockCount = blockCount;
        this.dy = dy;
    }

    Mino getMino() {
        return mino;
    }

    int calcCx(int minX) {
        return minX - this.minX;
    }

    int calcCy() {
        return this.cy;
    }

    long getNeedDeletedKey() {
        return this.needDeletedKey;
    }

    int getBlockCount() {
        return blockCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinoDiffWithKey obj = (MinoDiffWithKey) o;
        return minX == obj.minX && cy == obj.cy && needDeletedKey == obj.needDeletedKey &&
                blockCount == obj.blockCount && mino.equals(obj.mino);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mino, cy);
    }

    @Override
    public String toString() {
        return "MinoDiffWithKey{" +
                "mino=" + mino +
                ", minX=" + minX +
                ", cy=" + cy +
                ", deletedKey=" + needDeletedKey +
                ", blockCount=" + blockCount +
                ", dy=" + dy +
                '}';
    }
}
