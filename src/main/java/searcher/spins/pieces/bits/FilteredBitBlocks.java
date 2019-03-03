package searcher.spins.pieces.bits;

import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Stream;

public class FilteredBitBlocks {
    private final EnumMap<Piece, List<SimpleOriginalPiece>> pieceMap;

    FilteredBitBlocks(EnumMap<Piece, List<SimpleOriginalPiece>> pieceMap) {
        this.pieceMap = pieceMap;
    }

    public Stream<SimpleOriginalPiece> get(Piece piece, long needDeletedKey) {
        assert this.pieceMap.containsKey(piece) : piece + " " + Long.toBinaryString(needDeletedKey);
        List<SimpleOriginalPiece> pieces = this.pieceMap.get(piece);
        long notNeedDeletedKey = ~needDeletedKey;
        return pieces.stream()
                .filter(originalPiece -> (originalPiece.getNeedDeletedKey() & notNeedDeletedKey) == 0L);
    }
}
