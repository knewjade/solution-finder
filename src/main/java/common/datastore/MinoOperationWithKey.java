package common.datastore;

import core.mino.Mino;

public interface MinoOperationWithKey extends OperationWithKey {
    static int defaultHash(Mino mino, int x, int y, long needDeletedKey) {
        int result = y;
        result = 10 * result + x;
        result = 31 * result + mino.hashCode();
        result = 31 * result + (int) (needDeletedKey ^ (needDeletedKey >>> 32));
        return result;
    }

    static boolean defaultEquals(MinoOperationWithKey self, MinoOperationWithKey that) {
        return self.getX() == that.getX() &&
                self.getY() == that.getY() &&
                self.getNeedDeletedKey() == that.getNeedDeletedKey() &&
                self.getMino().equals(that.getMino());
    }

    Mino getMino();
}
