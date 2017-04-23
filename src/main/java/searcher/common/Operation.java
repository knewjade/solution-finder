package searcher.common;

import core.mino.Block;
import core.srs.Rotate;

public class Operation implements Comparable<Operation> {
    private final Block block;
    private final Rotate rotate;
    private final int x;
    private final int y;

    public Operation(Block block, Rotate rotate, int x, int y) {
        assert block != null && rotate != null;
        this.block = block;
        this.rotate = rotate;
        this.x = x;
        this.y = y;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
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

    @Override
    public int compareTo(Operation o) {
        int blockCompare = block.compareTo(o.block);
        if (blockCompare != 0)
            return blockCompare;

        int rotateCompare = rotate.compareTo(o.rotate);
        if (rotateCompare != 0)
            return rotateCompare;

        int xCompare = x - o.x;
        if (xCompare != 0)
            return xCompare;

        return y - o.y;
    }
}
