package _experimental.newfield;

import core.mino.Block;

import java.util.EnumMap;
import java.util.List;
import java.util.function.BiFunction;

public class BlockCounter {
    private static final BiFunction<Block, Integer, Integer> remapCountUp = (block, count) -> count + 1;

    private EnumMap<Block, Integer> counters = new EnumMap<>(Block.class);

    BlockCounter(List<Block> blocks) {
        for (Block block : Block.values())
            counters.putIfAbsent(block, 0);
        for (Block block : blocks)
            countUp(block);
    }

    private void countUp(Block block) {
        counters.compute(block, remapCountUp);
    }

    public int getCount(Block block) {
        return counters.get(block);
    }

    public int getAllBlock() {
        int sum = 0;
        for (Integer count : counters.values())
            sum += count;
        return sum;
    }

    @Override
    public String toString() {
        return "BlockCounter{" +
                "counters=" + counters +
                '}';
    }
}
