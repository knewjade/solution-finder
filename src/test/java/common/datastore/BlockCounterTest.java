package common.datastore;

import core.mino.Block;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class BlockCounterTest {
    @Test
    void testEmpty() throws Exception {
        List<Block> emptyList = Collections.emptyList();
        BlockCounter counter = new BlockCounter(emptyList);
        assertThat(counter.getCounter()).isEqualTo(0L);
    }

    @Test
    void testAdd() throws Exception {
        BlockCounter counter = new BlockCounter(Arrays.asList(Block.I, Block.J));
        BlockCounter actual = counter.addAndReturnNew(Collections.singletonList(Block.T));

        assertThat(counter.getCounter()).isEqualTo(new BlockCounter(Arrays.asList(Block.I, Block.J)).getCounter());
        assertThat(actual.getCounter()).isEqualTo(new BlockCounter(Arrays.asList(Block.I, Block.T, Block.J)).getCounter());
    }

    @Test
    void testGet() throws Exception {
        List<Block> blocks = Arrays.asList(Block.I, Block.J, Block.T, Block.S);
        BlockCounter counter = new BlockCounter(blocks);

        assertThat(counter.getBlockStream()).containsExactlyElementsOf(counter.getBlocks());
    }

    @Test
    void testGetMap() throws Exception {
        List<Block> blocks = Arrays.asList(Block.I, Block.J, Block.T, Block.I, Block.I, Block.T, Block.S);
        BlockCounter counter = new BlockCounter(blocks);
        EnumMap<Block, Integer> map = counter.getEnumMap();
        assertThat(map.get(Block.I)).isEqualTo(3);
        assertThat(map.get(Block.T)).isEqualTo(2);
        assertThat(map.get(Block.S)).isEqualTo(1);
        assertThat(map.get(Block.J)).isEqualTo(1);
        assertThat(map.get(Block.L)).isNull();
        assertThat(map.get(Block.Z)).isNull();
        assertThat(map.get(Block.O)).isNull();
    }

    @Test
    void testRandom() throws Exception {
        Randoms randoms = new Randoms();

        LOOP:
        for (int count = 0; count < 10000; count++) {
            List<Block> blocks = randoms.blocks(randoms.nextInt(0, 1000));
            Map<Block, List<Block>> group = blocks.stream().collect(Collectors.groupingBy(Function.identity()));

            for (List<Block> eachBlock : group.values())
                if (256 <= eachBlock.size())
                    continue LOOP;

            BlockCounter blockCounter = new BlockCounter(blocks);
            EnumMap<Block, Integer> counterMap = blockCounter.getEnumMap();
            for (Block block : Block.values()) {
                int expected = group.getOrDefault(block, Collections.emptyList()).size();
                assertThat(counterMap.getOrDefault(block, 0)).isEqualTo(expected);
            }
        }
    }
}