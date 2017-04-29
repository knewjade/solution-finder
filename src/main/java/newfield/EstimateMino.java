package newfield;

import core.mino.Block;

class EstimateMino {
    private final Block block;
    private final RotateLimit rotateLimit;
    private final Delta delta;

    EstimateMino(Block block, RotateLimit rotateLimit, Delta delta) {
        this.block = block;
        this.rotateLimit = rotateLimit;
        this.delta = delta;
    }

    public Block getBlock() {
        return block;
    }

    public RotateLimit getRotateLimit() {
        return rotateLimit;
    }

    public Delta getDelta() {
        return delta;
    }

    @Override
    public String toString() {
        return "EstimateMino{" +
                "block=" + block +
                ", rotateLimit=" + rotateLimit +
                ", delta=" + delta +
                '}';
    }
}
