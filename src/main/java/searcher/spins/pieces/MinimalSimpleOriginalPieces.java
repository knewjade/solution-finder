package searcher.spins.pieces;

import core.neighbor.SimpleOriginalPiece;

import java.util.List;

public class MinimalSimpleOriginalPieces {
    private final List<SimpleOriginalPiece> originalPieces;
    private final int maxHeight;

    MinimalSimpleOriginalPieces(List<SimpleOriginalPiece> originalPieces, int maxHeight) {
        this.originalPieces = originalPieces;
        this.maxHeight = maxHeight;
    }

    public List<SimpleOriginalPiece> getOriginalPieces() {
        return originalPieces;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}
