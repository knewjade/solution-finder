package searcher.spins.pieces.bits;

import core.field.Field;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.pieces.MinimalSimpleOriginalPieces;

import java.util.EnumMap;
import java.util.List;

public interface BitBlocks {
    static BitBlocks create(MinimalSimpleOriginalPieces minimalSimpleOriginalPieces) {
        int maxHeight = minimalSimpleOriginalPieces.getMaxHeight();
        if (maxHeight <= 12) {
            return MiddleBitBlocks.create(minimalSimpleOriginalPieces);
        } else {
            return LargeBitBlocks.create(minimalSimpleOriginalPieces);
        }
    }

    // ミノ一覧 を返却する
    EnumMap<Piece, List<SimpleOriginalPiece>> getNextOriginPiecesMap(Field rest);

    // ミノ一覧 を返却する
    FilteredBitBlocks filter(Field rest);
}