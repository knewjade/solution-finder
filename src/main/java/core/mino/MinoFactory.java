package core.mino;

import core.srs.Rotate;

import java.util.EnumMap;

public class MinoFactory {
    private final EnumMap<Block, EnumMap<Rotate, Mino>> maps = new EnumMap<>(Block.class);

    public MinoFactory() {
        for (Block block : Block.values()) {
            EnumMap<Rotate, Mino> minos = new EnumMap<>(Rotate.class);
            for (Rotate rotate : Rotate.values())
                minos.put(rotate, new Mino(block, rotate));
            maps.put(block, minos);
        }
    }

    public Mino create(Block block, Rotate rotate) {
        return maps.get(block).get(rotate);
    }
}
