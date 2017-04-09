package main;

import core.mino.Block;

import java.util.List;

public interface Pieces {
    void addLast(Block block);

    Pieces freeze();

    void stock(Block block);

    List<Block> getBlocks();
}
