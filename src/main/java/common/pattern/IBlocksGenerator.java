package common.pattern;

import common.datastore.BlockCounter;
import common.datastore.pieces.Blocks;

import java.util.stream.Stream;

public interface IBlocksGenerator {
    int getDepth();

    Stream<Blocks> blocksStream();

    Stream<BlockCounter> blockCountersStream();
}
