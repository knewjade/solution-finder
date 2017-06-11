package _implements.parity_based_pack.step1;

import core.mino.Block;

public class DeltaLimitedMino {
    private final Block block;
    private final DeltaLimit deltaLimit;

    public static DeltaLimitedMino create(Block block, DeltaLimit deltaLimit) {
        return new DeltaLimitedMino(block, deltaLimit);
    }

    private DeltaLimitedMino(Block block, DeltaLimit deltaLimit) {
        this.block = block;
        this.deltaLimit = deltaLimit;
    }

    public Block getBlock() {
        return block;
    }

    public DeltaLimit getDeltaLimit() {
        return deltaLimit;
    }

    @Override
    public String toString() {
        return "DeltaLimitedMino{" +
                "block=" + block +
                ", deltaLimit=" + deltaLimit +
                '}';
    }
}
