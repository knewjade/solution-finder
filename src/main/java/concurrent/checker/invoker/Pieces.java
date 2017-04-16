package concurrent.checker.invoker;

import core.mino.Block;

import java.util.List;

public interface Pieces {
    void addLast(Block block);

    void addLastTwo(Block block);

    Pieces freeze();

    void stock(Block block);

    List<Block> getBlocks();
}
