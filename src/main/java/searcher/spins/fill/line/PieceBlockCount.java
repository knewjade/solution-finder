package searcher.spins.fill.line;


import core.mino.Piece;

import java.util.Objects;

class PieceBlockCount implements Comparable<PieceBlockCount> {
    private final Piece piece;
    private final int blockCount;

    PieceBlockCount(Piece piece, int blockCount) {
        this.piece = piece;
        this.blockCount = blockCount;
    }

    Piece getPiece() {
        return piece;
    }

    int getBlockCount() {
        return blockCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PieceBlockCount that = (PieceBlockCount) o;
        return blockCount == that.blockCount && piece == that.piece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, blockCount);
    }

    @Override
    public String toString() {
        return String.format("<%s,%d>", piece.getName(), blockCount);
    }

    @Override
    public int compareTo(PieceBlockCount o) {
        int pieceCompare = piece.compareTo(o.piece);
        if (pieceCompare != 0) {
            return pieceCompare;
        }

        return Integer.compare(blockCount, o.blockCount);
    }
}

