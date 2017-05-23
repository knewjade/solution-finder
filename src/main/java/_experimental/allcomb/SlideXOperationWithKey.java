package _experimental.allcomb;

import common.datastore.OperationWithKey;
import core.mino.Mino;

public class SlideXOperationWithKey implements OperationWithKey {
    private final OperationWithKey operationWithKey;
    private final int slideX;

    public SlideXOperationWithKey(OperationWithKey operationWithKey, int slideX) {
        this.operationWithKey = operationWithKey;
        this.slideX = slideX;
    }

    @Override
    public Mino getMino() {
        return operationWithKey.getMino();
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
