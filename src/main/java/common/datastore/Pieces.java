package common.datastore;

import core.mino.Block;

import java.util.List;

public interface Pieces {
    List<Block> getBlocks();
    Pieces add(List<Block> blocks);
}
