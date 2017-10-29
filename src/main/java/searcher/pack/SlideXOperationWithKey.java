package searcher.pack;

import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;

public class SlideXOperationWithKey implements MinoOperationWithKey {
    private final MinoOperationWithKey operationWithKey;
    private final int slideX;

    public SlideXOperationWithKey(MinoOperationWithKey operationWithKey, int slideX) {
        this.operationWithKey = operationWithKey;
        this.slideX = slideX;
    }

    @Override
    public Block getBlock() {
        return operationWithKey.getBlock();
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
}
