package misc.pieces;

import core.mino.Block;

import java.util.List;

public class ComparablePieces implements Comparable<ComparablePieces> {
    private final List<Block> blocks;

    public ComparablePieces(List<Block> blocks) {
        assert blocks != null;
        this.blocks = blocks;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComparablePieces that = (ComparablePieces) o;

        return blocks.equals(that.blocks);
    }

    @Override
    public int hashCode() {
        return blocks.hashCode();
    }

    @Override
    public int compareTo(ComparablePieces o) {
        int size = this.blocks.size();
        int oSize = o.blocks.size();
        if (size == oSize) {
            for (int index = 0; index < size; index++) {
                int compare = blocks.get(index).compareTo(o.blocks.get(index));
                if (compare != 0)
                    return compare;
            }
            return 0;
        } else {
            return Integer.compare(size, oSize);
        }
    }
}
