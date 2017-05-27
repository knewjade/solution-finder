package common.datastore;

import core.mino.Block;

import java.util.ArrayList;
import java.util.List;

public class SafePieces implements Pieces {
    private final List<Block> blocks;

    public SafePieces(List<Block> blocks) {
        this.blocks = new ArrayList<>(blocks);
    }

    private SafePieces(List<Block> parent, List<Block> blocks) {
        this.blocks = new ArrayList<>(parent);
        this.blocks.addAll(blocks);
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public Pieces add(List<Block> blocks) {
        return new SafePieces(this.blocks, blocks);
    }

    @Override
    public String toString() {
        return "SafePieces{" +
                "blocks=" + blocks +
                '}';
    }
}
