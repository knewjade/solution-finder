package entry.path;

import common.datastore.BlockCounter;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.iterable.CombinationIterable;
import common.pattern.IBlocksGenerator;
import core.mino.Block;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ReduceBlocksGenerator implements IBlocksGenerator {
    private final IBlocksGenerator blocksGenerator;
    private final int maxDepth;

    public ReduceBlocksGenerator(IBlocksGenerator blocksGenerator, int maxDepth) {
        this.blocksGenerator = blocksGenerator;
        this.maxDepth = maxDepth;
    }

    @Override
    public int getDepth() {
        int depth = blocksGenerator.getDepth();
        if (maxDepth < depth)
            return maxDepth;
        return depth;
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
                    .flatMap(blocks -> {
                        CombinationIterable<Block> lists = new CombinationIterable<>(blocks, maxDepth);
                        return StreamSupport.stream(lists.spliterator(), false);
                    })
                    .map(BlockCounter::new);
    }

    public Stream<BlockCounter> blockCountersParallelStream() {
        return blockCountersStream().parallel();
    }
}
