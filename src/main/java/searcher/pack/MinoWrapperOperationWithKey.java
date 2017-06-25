package searcher.pack;

import common.datastore.OperationWithKey;
import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;
import searcher.pack.separable_mino.SeparableMino;

public class MinoWrapperOperationWithKey implements OperationWithKey {
    private final SeparableMino mino;
    private final int y;

    public MinoWrapperOperationWithKey(SeparableMino separableMino) {
        this.mino = separableMino;
        this.y = mino.getLowerY() - mino.getMino().getMinY();
    }

    @Override
    public Mino getMino() {
        return mino.getMino();
    }

    @Override
    public int getX() {
        return mino.getX();
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public long getNeedDeletedKey() {
        return mino.getDeleteKey();
    }

    @Override
    public long getUsingKey() {
        return mino.getUsingKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinoWrapperOperationWithKey that = (MinoWrapperOperationWithKey) o;

        if (getX() != that.getX()) return false;
        if (getY() != that.getY()) return false;
        if (getNeedDeletedKey() != that.getNeedDeletedKey()) return false;
        return getMino().equals(that.getMino());
    }

    @Override
    public int hashCode() {
        int result = getY();
        result = 10 * result + getX();
        result = 7 * result + getMino().getBlock().getNumber();
        result = 4 * result + getMino().getRotate().getNumber();
        long needDeletedKey = getNeedDeletedKey();
        result = 31 * result + (int) (needDeletedKey ^ (needDeletedKey >>> 32));
        long usingKey = getUsingKey();
        result = 31 * result + (int) (usingKey ^ (usingKey >>> 32));
        return result;
    }

    @Override
    public String toString() {
        Mino mino = getMino();
        return "OperationWithKey{" +
                "mino=" + mino.getBlock() + "-" + mino.getRotate() +
                ", x=" + getX() +
                ", y=" + getY() +
                ", needDeletedKey=" + getNeedDeletedKey() +
                '}';
    }
}
