package common.pattern;

import common.datastore.BlockCounter;
import common.datastore.blocks.Blocks;

import java.util.stream.Stream;

public interface IBlocksGenerator {
    int getDepth();

    Stream<Blocks> blocksStream();

    Stream<BlockCounter> blockCountersStream();
}
