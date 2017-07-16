package _implements.parity_based_pack;

import core.mino.Block;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

// TODO: 削除する
public class BlockCounterMap {
    private static final int[] HASHCODE_PRIMARIES = new int[]{2, 3, 5, 7, 11, 13, 17};
    private static final BiFunction<Block, Integer, Integer> remapCountUp = (block, count) -> count + 1;

    private EnumMap<Block, Integer> counters = new EnumMap<>(Block.class);

    BlockCounterMap(List<Block> blocks) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockCounterMap that = (BlockCounterMap) o;
        return counters.equals(that.counters);
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (Map.Entry<Block, Integer> entry : counters.entrySet()) {
            int number = entry.getKey().getNumber();
            result += HASHCODE_PRIMARIES[number] * entry.getValue();
        }
        return result;
    }

    @Override
    public String toString() {
        return "BlockCounter{" +
                "counters=" + counters +
                '}';
    }
}
