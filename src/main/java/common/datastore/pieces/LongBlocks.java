package common.datastore.pieces;

import common.comparator.PiecesNumberComparator;
import core.mino.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// max <= 22であること
public class LongBlocks implements Blocks, Comparable<LongBlocks> {
    private static final long[] SCALE = new long[22];

    static {
        for (int index = 0; index < SCALE.length; index++)
            SCALE[index] = pow(index);
    }

    private static long pow(int number) {
        long value = 1L;
        for (int count = 0; count < number; count++)
            value *= 7L;
        return value;
    }

    private static long getScale(int index) {
        return SCALE[index];
    }

    static long parse(List<Block> blocks) {
        return parse(0L, blocks, 0);
    }

    private static long parse(long pieces, List<Block> blocks, int startIndex) {
        for (int index = 0; index < blocks.size(); index++) {
            Block block = blocks.get(index);
            int scaleIndex = startIndex + index;
            pieces += getScale(scaleIndex) * block.getNumber();
        }
        return pieces;
    }

    static int toHash(long pieces) {
        return (int) (pieces ^ (pieces >>> 32));
    }

    private final long pieces;
    private final int max;

    public LongBlocks() {
        this.pieces = 0L;
        this.max = 0;
    }

    public LongBlocks(List<Block> blocks) {
        assert blocks.size() <= 22;
        this.pieces = parse(0L, blocks, 0);
        this.max = blocks.size();
    }

    public LongBlocks(Stream<Block> blocks) {
        TemporaryCount temporary = new TemporaryCount(0L, 0);
        blocks.sequential().forEach(temporary::add);
        this.pieces = temporary.value;
        this.max = temporary.index;
        assert this.max <= 22;
    }

    private LongBlocks(LongBlocks parent, List<Block> blocks) {
        this.pieces = parse(parent.pieces, blocks, parent.max);
        this.max = parent.max + blocks.size();
        assert this.max <= 22;
    }

    private LongBlocks(LongBlocks parent, Block block) {
        this.pieces = parent.pieces + SCALE[parent.max] * block.getNumber();
        this.max = parent.max + 1;
        assert this.max <= 22;
    }

    public long getPieces() {
        return pieces;
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
        return blocks;
    }

    @Override
    public Stream<Block> blockStream() {
        Stream.Builder<Block> builder = Stream.builder();
        long value = pieces;
        for (int count = 0; count < max; count++) {
            Block block = Block.getBlock((int) (value % 7));
            builder.accept(block);
            value = value / 7;
        }
        assert value == 0;
        return builder.build();
    }

    @Override
    public Blocks addAndReturnNew(List<Block> blocks) {
        return new LongBlocks(this, blocks);
    }

    @Override
    public Blocks addAndReturnNew(Block block) {
        return new LongBlocks(this, block);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof LongBlocks) {
            LongBlocks that = (LongBlocks) o;
            return pieces == that.pieces && max == that.max;
        } else if (o instanceof Blocks) {
            Blocks that = (Blocks) o;
            return PiecesNumberComparator.comparePieces(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return toHash(pieces);
    }

    public int compareTo(LongBlocks o) {
        return Long.compare(this.pieces, o.pieces);
    }

    private static class TemporaryCount {
        private long value = 0L;
        private int index = 0;

        private TemporaryCount(long value, int index) {
            this.value = value;
            this.index = index;
        }

        private void add(Block block) {
            value += getScale(index) * block.getNumber();
            index += 1;
        }
    }
}
