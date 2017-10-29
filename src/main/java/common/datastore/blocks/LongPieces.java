package common.datastore.blocks;

import common.comparator.PiecesNumberComparator;
import core.mino.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// max <= 22であること
public class LongPieces implements Pieces, Comparable<LongPieces> {
    private static final long[] SCALE = new long[22];

    static {
        for (int index = 0; index < SCALE.length; index++)
            SCALE[index] = pow(index);
    }

    public LongPieces(Pieces pieces) {
        this(pieces.blockStream());
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

    static long parse(List<Piece> pieces) {
        return parse(0L, pieces, 0);
    }

    private static long parse(long pieces, List<Piece> blocks, int startIndex) {
        for (int index = 0; index < blocks.size(); index++) {
            Piece piece = blocks.get(index);
            int scaleIndex = startIndex + index;
            pieces += getScale(scaleIndex) * piece.getNumber();
        }
        return pieces;
    }

    static int toHash(long pieces) {
        return (int) (pieces ^ (pieces >>> 32));
    }

    private final long pieces;
    private final int max;

    public LongPieces() {
        this.pieces = 0L;
        this.max = 0;
    }

    public LongPieces(List<Piece> pieces) {
        assert pieces.size() <= 22;
        this.pieces = parse(0L, pieces, 0);
        this.max = pieces.size();
    }

    public LongPieces(Stream<Piece> blocks) {
        this(0L, 0, blocks);
    }

    private LongPieces(LongPieces parent, List<Piece> pieces) {
        this.pieces = parse(parent.pieces, pieces, parent.max);
        this.max = parent.max + pieces.size();
        assert this.max <= 22;
    }

    private LongPieces(LongPieces parent, Piece piece) {
        this.pieces = parent.pieces + SCALE[parent.max] * piece.getNumber();
        this.max = parent.max + 1;
        assert this.max <= 22;
    }

    private LongPieces(LongPieces parent, Stream<Piece> blocks) {
        this(parent.pieces, parent.max, blocks);
    }

    private LongPieces(long pieces, int max, Stream<Piece> blocks) {
        TemporaryCount temporary = new TemporaryCount(pieces, max);
        blocks.sequential().forEach(temporary::add);
        this.pieces = temporary.value;
        this.max = temporary.index;
        assert this.max <= 22;
    }

    public long getPiecesValue() {
        return pieces;
    }

    @Override
    public List<Piece> getPieces() {
        ArrayList<Piece> pieces = new ArrayList<>();
        long value = this.pieces;
        for (int count = 0; count < max; count++) {
            Piece piece = Piece.getBlock((int) (value % 7));
            pieces.add(piece);
            value = value / 7;
        }
        assert value == 0;
        return pieces;
    }

    @Override
    public Stream<Piece> blockStream() {
        Stream.Builder<Piece> builder = Stream.builder();
        long value = pieces;
        for (int count = 0; count < max; count++) {
            Piece piece = Piece.getBlock((int) (value % 7));
            builder.accept(piece);
            value = value / 7;
        }
        assert value == 0;
        return builder.build();
    }

    @Override
    public Pieces addAndReturnNew(List<Piece> pieces) {
        return new LongPieces(this, pieces);
    }

    @Override
    public Pieces addAndReturnNew(Piece piece) {
        return new LongPieces(this, piece);
    }

    @Override
    public Pieces addAndReturnNew(Stream<Piece> blocks) {
        return new LongPieces(this, blocks);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof LongPieces) {
            LongPieces that = (LongPieces) o;
            return pieces == that.pieces && max == that.max;
        } else if (o instanceof Pieces) {
            Pieces that = (Pieces) o;
            return PiecesNumberComparator.comparePieces(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return toHash(pieces);
    }

    public int compareTo(LongPieces o) {
        return Long.compare(this.pieces, o.pieces);
    }

    // TODO: write unittest
    public Piece getLastBlock() {
        assert 1 <= max : max;
        long value = pieces / SCALE[max - 1];
        return Piece.getBlock((int) (value % 7));
    }

    private static class TemporaryCount {
        private long value = 0L;
        private int index = 0;

        private TemporaryCount(long value, int index) {
            this.value = value;
            this.index = index;
        }

        private void add(Piece piece) {
            value += getScale(index) * piece.getNumber();
            index += 1;
        }
    }
}
