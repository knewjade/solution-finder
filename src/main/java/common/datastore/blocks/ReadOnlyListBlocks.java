package common.datastore.blocks;

import common.comparator.PiecesNumberComparator;
import core.mino.Block;

import java.util.List;
import java.util.stream.Stream;

public class ReadOnlyListBlocks implements Blocks, Comparable<Blocks> {
    private final List<Block> blocks;

    public ReadOnlyListBlocks(List<Block> blocks) {
        assert blocks != null;
        this.blocks = blocks;
    }

    @Override
    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public Stream<Block> blockStream() {
        return blocks.stream();
    }

    @Override
    public Blocks addAndReturnNew(List<Block> blocks) {
        throw new UnsupportedOperationException("Unsafe operation");
    }

    @Override
    public Blocks addAndReturnNew(Block block) {
        throw new UnsupportedOperationException("Unsafe operation");
    }

    @Override
    public Blocks addAndReturnNew(Stream<Block> blocks) {
        throw new UnsupportedOperationException("Unsafe operation");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof ReadOnlyListBlocks) {
            ReadOnlyListBlocks that = (ReadOnlyListBlocks) o;
            return blocks.equals(that.blocks);
        } else if (o instanceof Blocks) {
            Blocks that = (Blocks) o;
            return PiecesNumberComparator.comparePieces(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        long pieces = LongBlocks.parse(blocks);
        return LongBlocks.toHash(pieces);
    }

    @Override
    public int compareTo(Blocks o) {
        return PiecesNumberComparator.comparePieces(this, o);
    }
}
