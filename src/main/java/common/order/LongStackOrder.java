package common.order;

import common.comparator.StackOrderComparator;
import core.mino.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// max <= 21であること
public class LongStackOrder implements StackOrder<Piece> {
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
    public void addLast(Piece piece) {
        pieces += toNumber(piece, max);
        max += 1;
    }

    private long toNumber(Piece piece, int index) {
        int number = piece != null ? piece.getNumber() : KIND_TYPE - 1;
        return getScale(index) * number;
    }

    private long getScale(int index) {
        return SCALE[index];
    }

    @Override
    public void addLastTwo(Piece piece) {
        assert 1 <= max;
        insertBlock(piece, max - 1);
        max += 1;
    }

    @Override
    public void addLastTwoAndRemoveLast(Piece piece) {
        assert 1 <= max;
        int index = max - 1;
        long head = pieces % getScale(index);
        pieces = toNumber(piece, index) + head;
    }

    private void insertBlock(Piece piece, int index) {
        long head = pieces % getScale(index);
        long last = pieces - head;
        pieces = last * KIND_TYPE + toNumber(piece, index) + head;
    }

    @Override
    public void stock(Piece piece) {
        insertBlock(piece, stockIndex);
        max += 1;
        stockIndex = max;
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
        return new LongStackOrder(pieces, stockIndex, max);
    }

    @Override
    public StackOrder<Piece> fix() {
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
