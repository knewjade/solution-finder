package common.order;

import core.mino.Block;

import java.util.ArrayList;
import java.util.List;

public class ListOrder {
    private final List<Block> blocks;
    private int stockIndex;

    ListOrder() {
        this(new ArrayList<>(), 0);
    }

    private ListOrder(List<Block> blocks, int stockIndex) {
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

    ListOrder freeze() {
        return new ListOrder(new ArrayList<>(blocks), stockIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListOrder that = (ListOrder) o;

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
        return "ListOrder{" +
                "blocks=" + this.blocks +
                '}';
    }
}
