package common.datastore;

import core.mino.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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

    public BlockCounter(Stream<Block> blocks) {
        this(0L, blocks);
    }

    private BlockCounter(long counter, Stream<Block> blocks) {
        long sum = blocks.mapToLong(block -> SLIDE_MASK[block.getNumber()]).sum();
        this.counter = counter + sum;
    }

    public BlockCounter add(List<Block> blocks) {
        return new BlockCounter(counter, blocks);
    }

    public long getCounter() {
        return counter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockCounter that = (BlockCounter) o;

        return counter == that.counter;
    }

    @Override
    public int hashCode() {
        return (int) (counter ^ (counter >>> 32));
    }

    public List<Block> getBlocks() {
        ArrayList<Block> blocks = new ArrayList<>();
        for (int index = 0, max = Block.getSize(); index < max; index++) {
            Block block = Block.getBlock(index);
            long size = (counter >>> 8 * index) & 0xff;
            for (int counter = 0; counter < size; counter++)
                blocks.add(block);
        }
        return blocks;
    }
}
