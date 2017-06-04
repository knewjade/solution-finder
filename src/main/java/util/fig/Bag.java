package util.fig;

import core.mino.Block;

import java.util.LinkedList;
import java.util.List;

public class Bag {
    private final LinkedList<Block> blocks;
    private Block hold;

    public Bag(List<Block> blocks, Block hold) {
        this.blocks = new LinkedList<>(blocks);
        this.hold = hold;
    }

    public void use(Block block) {
        if (block == null)
            return;

        Block peek = blocks.peek();
        if (block == peek) {
            blocks.pollFirst();
        } else if (block == hold) {
            hold = blocks.pollFirst();
        } else if (hold == null) {
            hold = blocks.pollFirst();
            use(block);
        } else {
            throw new IllegalStateException("No reachable");
        }
    }

    @Override
    public String toString() {
        return "Bag{" +
                "blocks=" + blocks +
                ", hold=" + hold +
                '}';
    }

    public List<Block> getNext(int nextBoxCount) {
        if (nextBoxCount <= blocks.size())
            return blocks.subList(0, nextBoxCount);
        return blocks;
    }

    public Block getHold() {
        return hold;
    }
}
