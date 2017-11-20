package common.order;

import common.comparator.StackOrderComparator;
import core.mino.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// max <= 21であること
public class FrozenLongStackOrder implements StackOrder<Piece> {
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
    public void addLast(Piece piece) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public void addLastTwo(Piece piece) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public void addLastTwoAndRemoveLast(Piece piece) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public void stock(Piece piece) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public List<Piece> toList() {
        ArrayList<Piece> pieces = new ArrayList<>();
        long value = this.pieces;
        for (int count = 0; count < max; count++) {
            Piece piece = getBlock((int) (value % KIND_TYPE));
            pieces.add(piece);
            value = value / KIND_TYPE;
        }
        assert value == 0 : max;
        return pieces;
    }

    private Piece getBlock(int number) {
        return number != KIND_TYPE - 1 ? Piece.getBlock(number) : null;
    }

    @Override
    public Stream<Piece> toStream() {
        Stream.Builder<Piece> builder = Stream.builder();
        long value = pieces;
        for (int count = 0; count < max; count++) {
            Piece piece = getBlock((int) (value % KIND_TYPE));
            builder.accept(piece);
            value = value / KIND_TYPE;
        }
        assert value == 0;
        return builder.build();
    }

    @Override
    public StackOrder<Piece> freeze() {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public StackOrder<Piece> fix() {
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
