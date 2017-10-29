package core.mino;

import core.srs.Rotate;

import java.util.EnumMap;

public class MinoFactory {
    private final EnumMap<Piece, EnumMap<Rotate, Mino>> maps = new EnumMap<>(Piece.class);

    public MinoFactory() {
        for (Piece piece : Piece.values()) {
            EnumMap<Rotate, Mino> minos = new EnumMap<>(Rotate.class);
            for (Rotate rotate : Rotate.values())
                minos.put(rotate, new Mino(piece, rotate));
            maps.put(piece, minos);
        }
    }

    public Mino create(Piece piece, Rotate rotate) {
        return maps.get(piece).get(rotate);
    }
}
