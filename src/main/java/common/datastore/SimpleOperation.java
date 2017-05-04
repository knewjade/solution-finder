package common.datastore;

import core.mino.Block;
import core.srs.Rotate;

public class SimpleOperation implements Operation {
    private final Block block;
    private final Rotate rotate;
    private final int x;
    private final int y;

    public SimpleOperation(Block block, Rotate rotate, int x, int y) {
        assert block != null && rotate != null;
        this.block = block;
        this.rotate = rotate;
        this.x = x;
        this.y = y;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public Rotate getRotate() {
        return rotate;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleOperation operation = (SimpleOperation) o;
        return x == operation.x && y == operation.y && block == operation.block && rotate == operation.rotate;
    }

    @Override
    public int hashCode() {
        int result = block.hashCode();
        result = 31 * result + rotate.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%s-%s %d,%d]", block, rotate, x, y);
    }
}
