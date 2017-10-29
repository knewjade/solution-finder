package common.datastore.blocks;

import core.mino.Block;

import java.util.List;
import java.util.stream.Stream;

public interface Blocks {
    List<Block> getBlocks();

    Stream<Block> blockStream();

    Blocks addAndReturnNew(List<Block> blocks);

    Blocks addAndReturnNew(Block block);

    Blocks addAndReturnNew(Stream<Block> blocks);

}
