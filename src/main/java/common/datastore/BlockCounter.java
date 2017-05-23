package common.datastore;

import core.mino.Block;

import java.util.List;

public class BlockCounter {
    private static final long[] SLIDE_MASK = new long[]{1L, 1L << 8, 1L << 16, 1L << 24, 1L << 32, 1L << 40, 1L << 48};

    private final long counter;

    public BlockCounter(List<Block> blocks) {
        this(0L, blocks);
    }

    private BlockCounter(long counter, List<Block> blocks) {
        for (Block block : blocks) {
            long mask = SLIDE_MASK[block.getNumber()];
            counter += mask;
        }
        this.counter = counter;
    }

    public BlockCounter add(List<Block> blocks) {
        return new BlockCounter(counter, blocks);
    }

    public long getCounter() {
        return counter;
    }
}
