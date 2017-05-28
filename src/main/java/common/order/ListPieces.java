package common.order;

import core.mino.Block;

import java.util.ArrayList;
import java.util.List;

public class ListPieces {
    private final List<Block> blocks;
    private int stockIndex;

    ListPieces() {
        this(new ArrayList<>(), 0);
    }

    private ListPieces(List<Block> blocks, int stockIndex) {
        this.blocks = blocks;
        this.stockIndex = stockIndex;
    }

    void addLast(Block block) {
        blocks.add(block);
    }

    void addLastTwo(Block block) {
        blocks.add(blocks.size() - 1, block);
    }

    void stock(Block block) {
        blocks.add(stockIndex, block);
        stockIndex = blocks.size();
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    ListPieces freeze() {
        return new ListPieces(new ArrayList<>(blocks), stockIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListPieces that = (ListPieces) o;

        if (stockIndex != that.stockIndex) return false;
        return blocks.equals(that.blocks);
    }

    @Override
    public int hashCode() {
        int result = blocks.hashCode();
        result = 31 * result + stockIndex;
        return result;
    }

    @Override
    public String toString() {
        return "ListPieces{" +
                "blocks=" + this.blocks +
                '}';
    }
}
