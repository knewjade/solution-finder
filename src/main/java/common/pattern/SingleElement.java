package common.pattern;

import common.datastore.BlockCounter;
import common.datastore.blocks.Blocks;
import common.datastore.blocks.LongBlocks;
import core.mino.Block;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class SingleElement implements Element {
    private final Block block;

    SingleElement(Block block) {
        this.block = block;
    }

    @Override
    public int getPopCount() {
        return 1;
    }

    @Override
    public List<Blocks> getPermutationBlocks() {
        LongBlocks blocks = new LongBlocks(Stream.of(block));
        return Collections.singletonList(blocks);
    }

    @Override
    public List<BlockCounter> getBlockCounters() {
        return Collections.singletonList(new BlockCounter(Stream.of(block)));
    }
}
