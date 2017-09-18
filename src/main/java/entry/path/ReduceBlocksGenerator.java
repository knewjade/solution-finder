package entry.path;

import common.datastore.BlockCounter;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.pattern.BlocksGenerator;

import java.util.stream.Stream;

public class ReduceBlocksGenerator {
    private final BlocksGenerator blocksGenerator;
    private final int maxDepth;

    public ReduceBlocksGenerator(BlocksGenerator blocksGenerator, int maxDepth) {
        this.blocksGenerator = blocksGenerator;
        this.maxDepth = maxDepth;
    }

    public Stream<Blocks> blocksStream() {
        if (blocksGenerator.getDepth() <= maxDepth)
            return blocksGenerator.blocksStream();
        else
            return blocksGenerator.blocksStream()
                    .map(Blocks::getBlocks)
                    .map(blocks -> blocks.subList(0, maxDepth))
                    .map(LongBlocks::new);
    }

    public Stream<Blocks> blocksParallelStream() {
        return blocksStream().parallel();
    }

    public Stream<BlockCounter> blockCountersStream() {
        if (blocksGenerator.getDepth() <= maxDepth)
            return blocksGenerator.blockCountersStream();
        else
            return blocksGenerator.blockCountersStream()
                    .map(BlockCounter::getBlocks)
                    .map(blocks -> blocks.subList(0, maxDepth))
                    .map(BlockCounter::new);
    }

    public Stream<BlockCounter> blockCountersParallelStream() {
        return blockCountersStream().parallel();
    }
}
