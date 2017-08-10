package common.datastore;

import core.mino.Block;
import lib.Randoms;
import org.assertj.core.util.Lists;
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
            List<Block> blocks = randoms.blocks(randoms.nextInt(0, 500));
            Map<Block, List<Block>> group = blocks.stream().collect(Collectors.groupingBy(Function.identity()));

            for (List<Block> eachBlock : group.values())
                if (128 <= eachBlock.size())
                    continue LOOP;

            BlockCounter blockCounter = new BlockCounter(blocks);
            EnumMap<Block, Integer> counterMap = blockCounter.getEnumMap();
            for (Block block : Block.values()) {
                int expected = group.getOrDefault(block, Collections.emptyList()).size();
                assertThat(counterMap.getOrDefault(block, 0)).isEqualTo(expected);
            }
        }
    }

    @Test
    void testContainsAll1() {
        BlockCounter counter1 = new BlockCounter(Collections.singletonList(Block.T));
        BlockCounter counter2 = new BlockCounter(Arrays.asList(Block.T, Block.I));
        assertThat(counter1.containsAll(counter2)).isFalse();
        assertThat(counter2.containsAll(counter1)).isTrue();
    }

    @Test
    void testContainsAll2() {
        BlockCounter counter1 = new BlockCounter(Arrays.asList(Block.S, Block.Z, Block.T));
        BlockCounter counter2 = new BlockCounter(Arrays.asList(Block.Z, Block.T, Block.S));
        assertThat(counter1.containsAll(counter2)).isTrue();
        assertThat(counter2.containsAll(counter1)).isTrue();
    }

    @Test
    void testContainsAllRandom() throws Exception {
        Randoms randoms = new Randoms();

        LOOP:
        for (int count = 0; count < 10000; count++) {
            List<Block> blocks1 = randoms.blocks(randoms.nextInt(0, 500));
            Map<Block, List<Block>> group1 = blocks1.stream().collect(Collectors.groupingBy(Function.identity()));

            for (List<Block> eachBlock : group1.values())
                if (128 <= eachBlock.size())
                    continue LOOP;

            List<Block> blocks2 = randoms.blocks(randoms.nextInt(0, 500));
            Map<Block, List<Block>> group2 = blocks2.stream().collect(Collectors.groupingBy(Function.identity()));

            for (List<Block> eachBlock : group2.values())
                if (128 <= eachBlock.size())
                    continue LOOP;

            boolean isChild1 = true;
            boolean isChild2 = true;
            List<Block> empty = Lists.emptyList();
            for (Block block : Block.values()) {
                isChild1 &= group1.getOrDefault(block, empty).size() <= group2.getOrDefault(block, empty).size();
                isChild2 &= group2.getOrDefault(block, empty).size() <= group1.getOrDefault(block, empty).size();
            }

            BlockCounter counter1 = new BlockCounter(blocks1);
            BlockCounter counter2 = new BlockCounter(blocks2);
            assertThat(counter1.containsAll(counter2)).isEqualTo(isChild2);
            assertThat(counter2.containsAll(counter1)).isEqualTo(isChild1);
        }
    }
}