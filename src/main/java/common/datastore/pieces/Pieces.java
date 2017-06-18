package common.datastore.pieces;

import core.mino.Block;

import java.util.List;
import java.util.stream.Stream;

public interface Pieces {
    List<Block> getBlocks();

    Stream<Block> getBlockStream();

    Pieces addAndReturnNew(List<Block> blocks);

    Pieces addAndReturnNew(Block block);
}
