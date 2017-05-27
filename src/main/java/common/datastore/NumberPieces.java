package common.datastore;

import core.mino.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// max <= 22であること
public class NumberPieces implements Pieces {
    private final long pieces;
    private final int max;

    public NumberPieces() {
        this.pieces = 0L;
        this.max = 0;
    }

    public NumberPieces(List<Block> blocks) {
        long pieces = 0L;
        for (Block block : blocks)
            pieces = pieces * 7 + block.getNumber();
        this.pieces = pieces;
        this.max = blocks.size();
    }

    private NumberPieces(NumberPieces parent, List<Block> blocks) {
        long pieces = parent.pieces;
        for (Block block : blocks)
            pieces = pieces * 7 + block.getNumber();
        this.pieces = pieces;
        this.max = parent.max + blocks.size();
    }

    @Override
    public List<Block> getBlocks() {
        ArrayList<Block> blocks = new ArrayList<>();
        long value = pieces;
        for (int count = 0; count < max; count++) {
            Block block = Block.getBlock((int) (value % 7));
            blocks.add(block);
            value = value / 7;
        }
        assert value == 0;
        Collections.reverse(blocks);
        return blocks;
    }

    @Override
    public Pieces add(List<Block> blocks) {
        return new NumberPieces(this, blocks);
    }
}
