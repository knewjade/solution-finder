package common.datastore.pieces;

import common.comparator.PiecesNumberComparator;
import core.mino.Block;

import java.util.List;
import java.util.stream.Stream;

// TODO: unittest
public class NoSafeListPieces implements Pieces, Comparable<Pieces> {
    private final List<Block> blocks;

    public NoSafeListPieces(List<Block> blocks) {
        assert blocks != null;
        this.blocks = blocks;
    }

    @Override
    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public Stream<Block> getBlockStream() {
        return blocks.stream();
    }

    @Override
    public Pieces addAndReturnNew(List<Block> blocks) {
        throw new UnsupportedOperationException("Unsafe operation");
    }

    @Override
    public Pieces addAndReturnNew(Block block) {
        throw new UnsupportedOperationException("Unsafe operation");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (getClass() != o.getClass()) {
            NoSafeListPieces that = (NoSafeListPieces) o;
            return blocks.equals(that.blocks);
        } else if (o instanceof Pieces) {
            Pieces that = (Pieces) o;
            return PiecesNumberComparator.comparePieces(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return blocks.hashCode();
    }

    @Override
    public int compareTo(Pieces o) {
        return PiecesNumberComparator.comparePieces(this, o);
    }
}
