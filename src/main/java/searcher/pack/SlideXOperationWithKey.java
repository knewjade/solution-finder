package searcher.pack;

import common.datastore.OperationWithKey;
import core.mino.Block;
import core.srs.Rotate;

public class SlideXOperationWithKey implements OperationWithKey {
    private final OperationWithKey operationWithKey;
    private final int slideX;

    public SlideXOperationWithKey(OperationWithKey operationWithKey, int slideX) {
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
}
