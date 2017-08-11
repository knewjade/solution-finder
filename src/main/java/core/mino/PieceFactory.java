package core.mino;

import common.ActionParser;
import core.srs.Rotate;

import java.util.Collection;
import java.util.HashMap;

public class PieceFactory {
    private static final int FIELD_WIDTH = 10;

    private final HashMap<Integer, Piece> pieces;

    public PieceFactory(int fieldHeight) {
        this.pieces = createPieces(fieldHeight);
    }

    private HashMap<Integer, Piece> createPieces(int fieldHeight) {
        HashMap<Integer, Piece> pieces = new HashMap<>();
        for (Block block : Block.values()) {
            for (Rotate rotate : Rotate.values()) {
                Mino mino = new Mino(block, rotate);
                for (int y = -mino.getMinY(); y < fieldHeight - mino.getMaxY(); y++) {
                    for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                        int indexKey = ActionParser.parseToInt(block, rotate, x, y);
                        Piece piece = new Piece(mino, x, y, fieldHeight);
                        pieces.put(indexKey, piece);
                    }
                }
            }
        }
        return pieces;
    }

    public Collection<Piece> getAllPieces() {
        return pieces.values();
    }
}
