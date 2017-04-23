package misc.pieces;

import core.mino.Block;

import java.util.ArrayList;
import java.util.List;

public class SafePieces {
    private final List<Block> blocks;

    public SafePieces(List<Block> blocks) {
        this.blocks = new ArrayList<>(blocks);
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public String toString() {
        return "SafePieces{" +
                "blocks=" + blocks +
                '}';
    }
}
