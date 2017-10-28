package core.mino.piece;

import common.ActionParser;
import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;

import java.util.Collection;
import java.util.HashMap;

// TODO: write unittest
public class OriginalPieceFactory {
    private static final int FIELD_WIDTH = 10;

    private final HashMap<Integer, OriginalPiece> pieces;
    private final int maxHeight;

    public OriginalPieceFactory(int fieldHeight) {
        this.pieces = createPieces(fieldHeight);
        this.maxHeight = fieldHeight;
    }

    private HashMap<Integer, OriginalPiece> createPieces(int fieldHeight) {
        HashMap<Integer, OriginalPiece> pieces = new HashMap<>();
        for (Block block : Block.values()) {
            for (Rotate rotate : Rotate.values()) {
                Mino mino = new Mino(block, rotate);
                for (int y = -mino.getMinY(); y < fieldHeight - mino.getMaxY(); y++) {
                    for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                        int indexKey = ActionParser.parseToInt(block, rotate, x, y);
                        OriginalPiece piece = new OriginalPiece(mino, x, y, fieldHeight);
                        pieces.put(indexKey, piece);
                    }
                }
            }
        }
        return pieces;
    }

    Collection<OriginalPiece> getAllPieces() {
        return pieces.values();
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}
