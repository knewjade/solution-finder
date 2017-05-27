package common.datastore.pieces;

import core.mino.Block;

import java.util.List;

public interface Pieces {
    List<Block> getBlocks();

    Pieces addAndReturnNew(List<Block> blocks);
}
