package common.order;

import common.comparator.PiecesNumberComparator;
import common.datastore.blocks.LongBlocks;
import core.mino.Block;
import lib.ListComparator;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static core.mino.Block.*;
import static org.assertj.core.api.Assertions.assertThat;

class OrderLookupTest {
    @Test
    void reverseWithJustBlocks() throws Exception {
        List<Block> blocks = Arrays.asList(T, I, O, S, Z, J, L, T, I, O, S, Z, J, L);
        for (int depth = 1; depth < blocks.size(); depth++) {
            ArrayList<StackOrder<Block>> reverse = OrderLookup.reverseBlocks(blocks.subList(0, depth), depth);
            assertThat(reverse).hasSize((int) Math.pow(2, depth - 1));
        }
    }

    @Test
    void reverseWithOverBlocks() throws Exception {
        List<Block> blocks = Arrays.asList(T, I, O, S, Z, J, L, T, I, O, S, Z, J, L);
        for (int depth = 1; depth < blocks.size(); depth++) {
            ArrayList<StackOrder<Block>> reverse = OrderLookup.reverseBlocks(blocks.subList(0, depth), depth + 1);
            assertThat(reverse).hasSize((int) Math.pow(2, depth));
        }
    }

    @Test
    void reverseWithJustBlocks2() throws Exception {
        List<Block> blocks = Arrays.asList(O, J, L, T, I, S, Z);
        for (int depth = 1; depth < blocks.size(); depth++) {
            ArrayList<StackOrder<Block>> reverse = OrderLookup.reverseBlocks(blocks.subList(0, depth), depth + 1);
            assertThat(reverse).hasSize((int) Math.pow(2, depth));
        }
    }

    @Test
    void forwardWithJustBlocks() throws Exception {
        List<Block> blocks = Arrays.asList(T, I, O, S, Z, J, L, T, I, O, S, Z, J, L);
        for (int depth = 2; depth < blocks.size(); depth++) {
            ArrayList<StackOrder<Block>> forward = OrderLookup.forwardBlocks(blocks.subList(0, depth), depth);
            assertThat(forward).hasSize((int) Math.pow(2, depth - 1));
        }
    }

    @Test
    void forwardWithLessBlocks() throws Exception {
        List<Block> blocks = Arrays.asList(T, I, O, S, Z, J, L, T, I, O, S, Z, J, L);
        for (int depth = 3; depth < blocks.size(); depth++) {
            ArrayList<StackOrder<Block>> forward = OrderLookup.forwardBlocks(blocks.subList(0, depth), depth - 1);
            assertThat(forward).hasSize((int) Math.pow(2, depth - 1));
        }
    }

    @Test
    void forwardJustBlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 2; size <= 13; size++) {
            List<Block> blockList = randoms.blocks(size);
            int toDepth = blockList.size();

            PiecesNumberComparator comparator = new PiecesNumberComparator();
            List<LongBlocks> forward1 = OrderLookup.forwardBlocks(blockList, toDepth).stream()
                    .map(StackOrder::toList)
                    .map(LongBlocks::new)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth, blockList.size());
            List<LongBlocks> forward2 = lookUp.parse(blockList)
                    .map(blockStream -> blockStream.collect(Collectors.toList()))
                    .map(LongBlocks::new)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            assertThat(forward2).isEqualTo(forward1);
        }
    }

    @Test
    void forwardOverBlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 3; size <= 13; size++) {
            List<Block> blockList = randoms.blocks(size);
            int toDepth = blockList.size() - 1;

            PiecesNumberComparator comparator = new PiecesNumberComparator();
            List<LongBlocks> forward1 = OrderLookup.forwardBlocks(blockList, toDepth).stream()
                    .map(StackOrder::toList)
                    .map(LongBlocks::new)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth, blockList.size());
            List<LongBlocks> forward2 = lookUp.parse(blockList)
                    .map(blockStream -> blockStream.collect(Collectors.toList()))
                    .map(LongBlocks::new)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            assertThat(forward2).isEqualTo(forward1);
        }
    }

    @Test
    void forwardOver2BlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 4; size <= 13; size++) {
            List<Block> blockList = randoms.blocks(size);
            int toDepth = blockList.size() - 2;

            PiecesNumberComparator comparator = new PiecesNumberComparator();
            List<LongBlocks> forward1 = OrderLookup.forwardBlocks(blockList, toDepth).stream()
                    .map(StackOrder::toList)
                    .map(LongBlocks::new)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth, blockList.size());
            List<LongBlocks> forward2 = lookUp.parse(blockList)
                    .map(blockStream -> blockStream.collect(Collectors.toList()))
                    .map(LongBlocks::new)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            assertThat(forward2).isEqualTo(forward1);
        }
    }

    @Test
    void reverseJustBlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 1; size <= 13; size++) {
            List<Block> blockList = randoms.blocks(size);
            int fromDepth = blockList.size();

            PiecesNumberComparator comparator = new PiecesNumberComparator();
            List<LongBlocks> forward1 = OrderLookup.reverseBlocks(blockList, fromDepth).stream()
                    .map(StackOrder::toList)
                    .map(LongBlocks::new)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            ReverseOrderLookUp lookUp = new ReverseOrderLookUp(blockList.size(), fromDepth);
            List<LongBlocks> forward2 = lookUp.parse(blockList)
                    .map(blockStream -> blockStream.collect(Collectors.toList()))
                    .map(LongBlocks::new)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            assertThat(forward2).isEqualTo(forward1);
        }
    }

    @Test
    void reverseOverBlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 1; size <= 13; size++) {
            List<Block> blockList = randoms.blocks(size);
            int fromDepth = blockList.size() + 1;

            Comparator<List<Block>> comparator = new ListComparator<>(Comparator.nullsFirst(Comparator.comparingInt(Block::getNumber)));
            List<List<Block>> forward1 = OrderLookup.reverseBlocks(blockList, fromDepth).stream()
                    .map(StackOrder::toList)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            ReverseOrderLookUp lookUp = new ReverseOrderLookUp(blockList.size(), fromDepth);
            List<List<Block>> forward2 = lookUp.parse(blockList)
                    .map(blockStream -> blockStream.collect(Collectors.toList()))
                    .sorted(comparator)
                    .collect(Collectors.toList());

            assertThat(forward2).isEqualTo(forward1);
        }
    }

    @Test
    void reverseOver2BlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 1; size <= 13; size++) {
            List<Block> blockList = randoms.blocks(size);
            int fromDepth = blockList.size() + 2;

            Comparator<List<Block>> comparator = new ListComparator<>(Comparator.nullsFirst(Comparator.comparingInt(Block::getNumber)));
            List<List<Block>> forward1 = OrderLookup.reverseBlocks(blockList, fromDepth).stream()
                    .map(StackOrder::toList)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            ReverseOrderLookUp lookUp = new ReverseOrderLookUp(blockList.size(), fromDepth);
            List<List<Block>> forward2 = lookUp.parse(blockList)
                    .map(blockStream -> blockStream.collect(Collectors.toList()))
                    .sorted(comparator)
                    .collect(Collectors.toList());

            assertThat(forward2).isEqualTo(forward1);
        }
    }
}