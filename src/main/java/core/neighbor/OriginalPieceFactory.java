package core.neighbor;

import core.mino.Mino;
import core.mino.Piece;
import core.srs.Rotate;

import java.util.HashSet;
import java.util.Set;

public class OriginalPieceFactory {
    private static final int FIELD_WIDTH = 10;

    private final Set<OriginalPiece> pieces;
    private final int maxHeight;

    public OriginalPieceFactory(int fieldHeight) {
        this.pieces = createPieces(fieldHeight);
        this.maxHeight = fieldHeight;
    }

    private Set<OriginalPiece> createPieces(int fieldHeight) {
        Set<OriginalPiece> pieces = new HashSet<>();
        for (Piece block : Piece.values()) {
            for (Rotate rotate : Rotate.values()) {
                Mino mino = new Mino(block, rotate);
                for (int y = -mino.getMinY(); y < fieldHeight - mino.getMaxY(); y++) {
                    for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                        OriginalPiece piece = new OriginalPiece(mino, x, y, fieldHeight);
                        pieces.add(piece);
                    }
                }
            }
        }
        return pieces;
    }

    public Set<OriginalPiece> create() {
        return pieces;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}
