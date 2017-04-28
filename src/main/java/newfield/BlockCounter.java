package newfield;

import core.mino.Block;

import java.util.EnumMap;
import java.util.List;

class BlockCounter {
    private EnumMap<Block, Integer> counters = new EnumMap<>(Block.class);

    BlockCounter(List<Block> blocks) {
        for (Block block : Block.values())
            counters.putIfAbsent(block, 0);
        for (Block block : blocks)
            countUp(block);
    }

    private void countUp(Block block) {
        counters.compute(block, (blk, cnt) -> cnt + 1);
    }

    @Override
    public String toString() {
        return "BlockCounter{" +
                "counters=" + counters +
                '}';
    }

    int getCount(Block block) {
        return counters.get(block);
    }
}
