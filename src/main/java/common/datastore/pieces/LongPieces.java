package common.datastore.pieces;

import common.comparator.PiecesComparator;
import core.mino.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// max <= 22であること
public class LongPieces implements Pieces {
    private final long pieces;
    private final int max;

    public LongPieces() {
        this.pieces = 0L;
        this.max = 0;
    }

    public LongPieces(List<Block> blocks) {
        long pieces = 0L;
        for (Block block : blocks)
            pieces = pieces * 7 + block.getNumber();
        this.pieces = pieces;
        this.max = blocks.size();
    }

    private LongPieces(LongPieces parent, List<Block> blocks) {
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
    public Pieces addAndReturnNew(List<Block> blocks) {
        return new LongPieces(this, blocks);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (getClass() != o.getClass()) {
            LongPieces that = (LongPieces) o;
            return pieces == that.pieces;
        } else if (o instanceof Pieces) {
            Pieces that = (Pieces) o;
            return PiecesComparator.comparePieces(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (int) (pieces ^ (pieces >>> 32));
    }
}
