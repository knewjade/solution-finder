package common.order;

import core.mino.Block;

import java.util.List;

// TODO: 削除
public interface Pieces {
    void addLast(Block block);

    void addLastTwo(Block block);

    Pieces freeze();

    void stock(Block block);

    List<Block> getBlocks();
}
