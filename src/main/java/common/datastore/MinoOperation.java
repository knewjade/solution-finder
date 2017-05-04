package common.datastore;

import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;

public class MinoOperation implements Operation {
    private final Mino mino;
    private final int x;
    private final int y;

    public MinoOperation(Mino mino, int x, int y) {
        this.mino = mino;
        this.x = x;
        this.y = y;
    }

    public Mino getMino() {
        return mino;
    }

    @Override
    public Block getBlock() {
        return mino.getBlock();
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
}
