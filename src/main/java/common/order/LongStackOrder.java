package common.order;

import common.comparator.StackOrderComparator;
import core.mino.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// max <= 21であること
public class LongStackOrder implements StackOrder<Block> {
    private static final long[] SCALE = new long[21];
    private static final int KIND_TYPE = 8;

    static {
        for (int index = 0; index < SCALE.length; index++)
            SCALE[index] = pow(index);
    }

    private static long pow(int number) {
        return (long) Math.pow(KIND_TYPE, number);
    }

    private long pieces;
    private int stockIndex;
    private int max;

    LongStackOrder() {
        this(0L, 0, 0);
    }

    private LongStackOrder(long pieces, int stockIndex, int max) {
        this.pieces = pieces;
        this.stockIndex = stockIndex;
        this.max = max;
    }

    @Override
    public void addLast(Block block) {
        pieces += toNumber(block, max);
        max += 1;
    }

    private long toNumber(Block block, int index) {
        int number = block != null ? block.getNumber() : KIND_TYPE - 1;
        return getScale(index) * number;
    }

    private long getScale(int index) {
        return SCALE[index];
    }

    @Override
    public void addLastTwo(Block block) {
        assert 1 <= max;
        insertBlock(block, max - 1);
        max += 1;
    }

    @Override
    public void addLastTwoAndRemoveLast(Block block) {
        assert 1 <= max;
        int index = max - 1;
        long head = pieces % getScale(index);
        pieces = toNumber(block, index) + head;
    }

    private void insertBlock(Block block, int index) {
        long head = pieces % getScale(index);
        long last = pieces - head;
        pieces = last * KIND_TYPE + toNumber(block, index) + head;
    }

    @Override
    public void stock(Block block) {
        insertBlock(block, stockIndex);
        max += 1;
        stockIndex = max;
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
        return new LongStackOrder(pieces, stockIndex, max);
    }

    @Override
    public StackOrder<Block> fix() {
        return new FrozenLongStackOrder(pieces, max);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (getClass() == o.getClass()) {
            LongStackOrder that = (LongStackOrder) o;
            return max == that.getMax() && pieces == that.getPieces();
        } else if (o instanceof FrozenLongStackOrder) {
            FrozenLongStackOrder that = (FrozenLongStackOrder) o;
            return max == that.getMax() && pieces == that.getPieces();
        } else if (o instanceof StackOrder) {
            StackOrder that = (StackOrder) o;
            return StackOrderComparator.compareStackOrder(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("No mutable");
    }

    public long getPieces() {
        return pieces;
    }

    public int getMax() {
        return max;
    }
}
