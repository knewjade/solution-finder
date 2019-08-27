package common.datastore.blocks;

import common.comparator.PiecesNumberComparator;
import core.mino.Piece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// max <= 44であること
public class LongLongPieces implements Pieces, Comparable<LongLongPieces> {
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

    private static long parse(long pieces, List<Piece> blocks, int startIndex) {
        for (int index = 0; index < blocks.size(); index++) {
            Piece piece = blocks.get(index);
            int scaleIndex = startIndex + index;
            pieces += getScale(scaleIndex) * piece.getNumber();
        }
        return pieces;
    }

    private static int toHash(long pieces) {
        return (int) (pieces ^ (pieces >>> 32));
    }

    private final long lowPieces;
    private final long highPieces;
    private final int max;

    public LongLongPieces() {
        this.lowPieces = 0L;
        this.highPieces = 0L;
        this.max = 0;
    }

    public LongLongPieces(Piece... pieces) {
        this(Arrays.asList(pieces));
    }

    public LongLongPieces(Pieces pieces) {
        this(pieces.blockStream());
    }

    public LongLongPieces(List<Piece> pieces) {
        int size = pieces.size();
        assert size <= 44;

        if (size <= 22) {
            this.lowPieces = parse(0L, pieces, 0);
            this.highPieces = 0L;
            this.max = size;
        } else {
            this.lowPieces = parse(0L, pieces.subList(0, 22), 0);
            this.highPieces = parse(0L, pieces.subList(22, size), 0);
            this.max = size;
        }
    }

    private LongLongPieces(Stream<Piece> blocks) {
        this(blocks.collect(Collectors.toList()));
    }

    private LongLongPieces(LongLongPieces parent, List<Piece> pieces) {
        this(parent, pieces.stream());
    }

    private LongLongPieces(LongLongPieces parent, Piece piece) {
        this(parent, Stream.of(piece));
    }

    private LongLongPieces(LongLongPieces parent, Stream<Piece> blocks) {
        this(Stream.concat(parent.blockStream(), blocks));
    }

    @Override
    public List<Piece> getPieces() {
        ArrayList<Piece> pieces = new ArrayList<>();
        {
            long value = this.lowPieces;
            for (int count = 0; count < Math.min(max, 22); count++) {
                Piece piece = Piece.getBlock((int) (value % 7));
                pieces.add(piece);
                value = value / 7;
            }
            assert value == 0;
        }
        {
            long value = this.highPieces;
            for (int count = 22; count < max; count++) {
                Piece piece = Piece.getBlock((int) (value % 7));
                pieces.add(piece);
                value = value / 7;
            }
            assert value == 0;
        }
        return pieces;
    }

    @Override
    public Stream<Piece> blockStream() {
        Stream.Builder<Piece> builder = Stream.builder();
        {
            long value = lowPieces;
            for (int count = 0; count < Math.min(max, 22); count++) {
                Piece piece = Piece.getBlock((int) (value % 7));
                builder.accept(piece);
                value = value / 7;
            }
            assert value == 0;
        }
        {
            long value = highPieces;
            for (int count = 22; count < max; count++) {
                Piece piece = Piece.getBlock((int) (value % 7));
                builder.accept(piece);
                value = value / 7;
            }
            assert value == 0;
        }
        return builder.build();
    }

    @Override
    public Piece[] getPieceArray() {
        Piece[] pieces = new Piece[max];
        {
            long value = this.lowPieces;
            for (int index = 0; index < Math.min(max, 22); index++) {
                Piece piece = Piece.getBlock((int) (value % 7));
                pieces[index] = piece;
                value = value / 7;
            }
            assert value == 0;
        }
        {
            long value = this.highPieces;
            for (int index = 22; index < max; index++) {
                Piece piece = Piece.getBlock((int) (value % 7));
                pieces[index] = piece;
                value = value / 7;
            }
            assert value == 0;
        }
        return pieces;
    }

    @Override
    public Pieces addAndReturnNew(List<Piece> pieces) {
        return new LongLongPieces(this, pieces);
    }

    @Override
    public Pieces addAndReturnNew(Piece piece) {
        return new LongLongPieces(this, piece);
    }

    @Override
    public Pieces addAndReturnNew(Stream<Piece> blocks) {
        return new LongLongPieces(this, blocks);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof LongLongPieces) {
            LongLongPieces that = (LongLongPieces) o;
            return lowPieces == that.lowPieces && highPieces == that.highPieces && max == that.max;
        } else if (o instanceof Pieces) {
            Pieces that = (Pieces) o;
            return PiecesNumberComparator.comparePieces(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return toHash(highPieces) ^ toHash(lowPieces) >>> 23;
    }

    @Override
    public int compareTo(LongLongPieces o) {
        {
            int compare = Long.compare(this.lowPieces, o.lowPieces);
            if (compare != 0L) {
                return compare;
            }
        }
        return Long.compare(this.highPieces, o.highPieces);
    }

    @Override
    public String toString() {
        return String.format("LongLongPieces{%s,%s}", lowPieces, highPieces);
    }
}
