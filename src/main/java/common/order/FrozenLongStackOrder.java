package common.order;

import common.comparator.StackOrderComparator;
import core.mino.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// max <= 21であること
public class FrozenLongStackOrder implements StackOrder<Block> {
    private static final long[] SCALE = new long[21];
    private static final int KIND_TYPE = 8;

    static {
        for (int index = 0; index < SCALE.length; index++)
            SCALE[index] = pow(index);
    }

    private static long pow(int number) {
        return (long) Math.pow(KIND_TYPE, number);
    }

    private final long pieces;
    private final int max;

    FrozenLongStackOrder(long pieces, int max) {
        this.pieces = pieces;
        this.max = max;
    }

    @Override
    public void addLast(Block block) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public void addLastTwo(Block block) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public void stock(Block block) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public List<Block> toList() {
        ArrayList<Block> blocks = new ArrayList<>();
        long value = pieces;
        for (int count = 0; count < max; count++) {
            Block block = getBlock((int) (value % KIND_TYPE));
            blocks.add(block);
            value = value / KIND_TYPE;
        }
        assert value == 0 : max;
        return blocks;
    }

    private Block getBlock(int number) {
        return number != KIND_TYPE - 1 ? Block.getBlock(number) : null;
    }

    @Override
    public Stream<Block> toStream() {
        Stream.Builder<Block> builder = Stream.builder();
        long value = pieces;
        for (int count = 0; count < max; count++) {
            Block block = getBlock((int) (value % KIND_TYPE));
            builder.accept(block);
            value = value / KIND_TYPE;
        }
        assert value == 0;
        return builder.build();
    }

    @Override
    public StackOrder<Block> freeze() {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public StackOrder<Block> fix() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (getClass() == o.getClass()) {
            FrozenLongStackOrder that = (FrozenLongStackOrder) o;
            return max == that.max && pieces == that.pieces;
        } else if (o instanceof LongStackOrder) {
            LongStackOrder that = (LongStackOrder) o;
            return max == that.getMax() && pieces == that.getPieces();
        } else if (o instanceof StackOrder) {
            StackOrder that = (StackOrder) o;
            return StackOrderComparator.compareStackOrder(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = (int) (pieces ^ (pieces >>> 32));
        result = 31 * result + max;
        return result;
    }

    public int getMax() {
        return max;
    }

    public long getPieces() {
        return pieces;
    }
}
