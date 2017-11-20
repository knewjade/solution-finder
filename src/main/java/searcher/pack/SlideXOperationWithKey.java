package searcher.pack;

import common.datastore.MinoOperationWithKey;
import core.mino.Mino;
import core.mino.Piece;
import core.srs.Rotate;

public class SlideXOperationWithKey implements MinoOperationWithKey {
    private final MinoOperationWithKey operationWithKey;
    private final int slideX;

    public SlideXOperationWithKey(MinoOperationWithKey operationWithKey, int slideX) {
        this.operationWithKey = operationWithKey;
        this.slideX = slideX;
    }

    @Override
    public Piece getPiece() {
        return operationWithKey.getPiece();
    }

    @Override
    public Rotate getRotate() {
        return operationWithKey.getRotate();
    }

    @Override
    public int getX() {
        return operationWithKey.getX() + slideX;
    }

    @Override
    public int getY() {
        return operationWithKey.getY();
    }

    @Override
    public long getNeedDeletedKey() {
        return operationWithKey.getNeedDeletedKey();
    }

    @Override
    public long getUsingKey() {
        return operationWithKey.getUsingKey();
    }

    @Override
    public Mino getMino() {
        return operationWithKey.getMino();
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
        return MinoOperationWithKey.defaultHash(getMino(), getX(), getY(), getNeedDeletedKey());
    }
}
