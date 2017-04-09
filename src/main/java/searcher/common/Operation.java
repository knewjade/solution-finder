package searcher.common;

import core.mino.Block;
import core.srs.Rotate;

public class Operation {
    private final Block block;
    private final Rotate rotate;
    private final int x;
    private final int y;

    Operation(Block block, Rotate rotate, int x, int y) {
        this.block = block;
        this.rotate = rotate;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("[%s-%s %d,%d]", block, rotate, x, y);
    }

    public Block getBlock() {
        return block;
    }

    public Rotate getRotate() {
        return rotate;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
