package common.datastore.blocks;

import common.comparator.PiecesNumberComparator;
import core.mino.Piece;

import java.util.List;
import java.util.stream.Stream;

public class ReadOnlyListPieces implements Pieces, Comparable<Pieces> {
    private final List<Piece> pieces;

    public ReadOnlyListPieces(List<Piece> pieces) {
        assert pieces != null;
        this.pieces = pieces;
    }

    @Override
    public Piece[] getPieceArray() {
        Piece[] array = new Piece[pieces.size()];
        return pieces.toArray(array);
    }

    @Override
    public List<Piece> getPieces() {
        return pieces;
    }

    @Override
    public Stream<Piece> blockStream() {
        return pieces.stream();
    }

    @Override
    public Pieces addAndReturnNew(List<Piece> pieces) {
        throw new UnsupportedOperationException("Unsafe operation");
    }

    @Override
    public Pieces addAndReturnNew(Piece piece) {
        throw new UnsupportedOperationException("Unsafe operation");
    }

    @Override
    public Pieces addAndReturnNew(Stream<Piece> blocks) {
        throw new UnsupportedOperationException("Unsafe operation");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof ReadOnlyListPieces) {
            ReadOnlyListPieces that = (ReadOnlyListPieces) o;
            return pieces.equals(that.pieces);
        } else if (o instanceof Pieces) {
            Pieces that = (Pieces) o;
            return PiecesNumberComparator.comparePieces(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        long pieces = LongPieces.parse(this.pieces);
        return LongPieces.toHash(pieces);
    }

    @Override
    public int compareTo(Pieces o) {
        return PiecesNumberComparator.comparePieces(this, o);
    }
}
