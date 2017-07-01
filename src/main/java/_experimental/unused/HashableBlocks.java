package _experimental.unused;

import core.mino.Block;

import java.util.List;

// num of blocks <= 10 であること
// nullを含まないこと
class HashableBlocks {
    private final List<Block> blocks;

    private final int hash;

    public HashableBlocks(List<Block> blocks) {
        this.blocks = blocks;
        this.hash = calculateHash(blocks);
    }

    private int calculateHash(List<Block> blocks) {
        int size = blocks.size();
        int number = blocks.get(size - 1).getNumber();
        for (int index = size - 2; 0 <= index; index--) {
            number *= 8;
            number += blocks.get(index).getNumber();
        }
        return number;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashableBlocks pieces = (HashableBlocks) o;
        return hash == pieces.hash;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }
}
