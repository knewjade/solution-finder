package misc;

import core.mino.Block;

import java.util.List;

public class ComparablePieces implements Comparable<ComparablePieces> {
    private final List<Block> blocks;

    public ComparablePieces(List<Block> blocks) {
        this.blocks = blocks;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public int compareTo(ComparablePieces o) {
        int size = this.blocks.size();
        int size1 = o.blocks.size();
        if (size == size1) {
            for (int index = 0; index < size; index++) {
                int compare = Integer.compare(this.blocks.get(index).getNumber(), o.blocks.get(index).getNumber());
                if (compare != 0)
                    return compare;
            }
            return 0;
        } else {
            return Integer.compare(size, size1);
        }
    }
}
