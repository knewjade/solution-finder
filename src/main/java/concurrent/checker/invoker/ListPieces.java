package concurrent.checker.invoker;

import core.mino.Block;

import java.util.ArrayList;
import java.util.List;

public class ListPieces implements Pieces {
    private final List<Block> blocks;
    private int stockIndex;

    ListPieces() {
        this(new ArrayList<>(), 0);
    }

    private ListPieces(List<Block> blocks, int stockIndex) {
        this.blocks = blocks;
        this.stockIndex = stockIndex;
    }

    @Override
    public void addLast(Block block) {
        blocks.add(block);
    }

    @Override
    public void stock(Block block) {
        blocks.add(stockIndex, block);
        stockIndex = blocks.size();
    }

    @Override
    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public Pieces freeze() {
        return new ListPieces(new ArrayList<>(blocks), stockIndex);
    }

    @Override
    public String toString() {
        return "ListPieces{" +
                "blocks=" + this.blocks +
                '}';
    }
}
