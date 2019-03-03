package searcher.spins.pieces;

import core.neighbor.SimpleOriginalPiece;

import java.util.List;

class AllSimpleOriginalPieces {
    private final List<SimpleOriginalPiece> originalPieces;
    private final int maxHeight;

    AllSimpleOriginalPieces(List<SimpleOriginalPiece> originalPieces, int maxHeight) {
        this.originalPieces = originalPieces;
        this.maxHeight = maxHeight;
    }

    List<SimpleOriginalPiece> getOriginalPieces() {
        return originalPieces;
    }

    int getMaxHeight() {
        return maxHeight;
    }
}
