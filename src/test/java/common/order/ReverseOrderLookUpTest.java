package common.order;

import common.datastore.pieces.LongPieces;
import core.mino.Block;
import lib.Randoms;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ReverseOrderLookUpTest {
    @Test
    void parseJustBlocksCount() throws Exception {
        List<Block> blockList = Block.valueList();
        int toDepth = blockList.size();

        ReverseOrderLookUp lookUp = new ReverseOrderLookUp(blockList.size(), toDepth);
        long count = lookUp.parse(blockList).count();

        assertThat(count).isEqualTo(64);
    }

    @Test
    void parseOverBlocksCount() throws Exception {
        List<Block> blockList = Block.valueList();
        int toDepth = blockList.size() + 1;

        ReverseOrderLookUp lookUp = new ReverseOrderLookUp(blockList.size(), toDepth);
        long count = lookUp.parse(blockList).count();

        assertThat(count).isEqualTo(128);
    }

    @Test
    void parseOver() throws Exception {
        List<Block> blockList = Arrays.asList(Block.I, Block.T, Block.Z, Block.O, Block.I, Block.L);
        int fromDepth = blockList.size() + 1;

        Comparator<List<Block>> comparator = (o1, o2) -> {
            int size1 = o1.size();
            int size2 = o2.size();
            int compareSize = Integer.compare(size1, size2);
            if (compareSize != 0)
                return compareSize;

            Comparator<Block> blockComparator = Comparator.nullsLast(Enum::compareTo);
            for (int index = 0; index < size1; index++) {
                int compare = blockComparator.compare(o1.get(index), o2.get(index));
                if (compare != 0)
                    return compare;
            }

            return 0;
        };

        List<List<Block>> reverse1 = OrderLookup.reverseBlocks(blockList, fromDepth).stream()
                .map(StackOrder::toList)
                .sorted(comparator)
                .collect(Collectors.toList());

        ReverseOrderLookUp lookUp = new ReverseOrderLookUp(blockList.size(), fromDepth);
        List<List<Block>> reverse2 = lookUp.parse(blockList)
                .map(blockStream -> blockStream.collect(Collectors.toList()))
                .sorted(comparator)
                .collect(Collectors.toList());

        assertThat(reverse2).isEqualTo(reverse1);
    }

    @Test
    void parseJustRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 2; size <= 13; size++) {
            List<Block> blocks = randoms.blocks(size);
            int fromDepth = blocks.size();

            ReverseOrderLookUp reverseOrderLookUp = new ReverseOrderLookUp(blocks.size(), fromDepth);
            List<LongPieces> reverse = reverseOrderLookUp.parse(blocks)
                    .map(LongPieces::new)
                    .collect(Collectors.toList());

            LongPieces target = new LongPieces(blocks);
            ForwardOrderLookUp forwardOrderLookUp = new ForwardOrderLookUp(blocks.size(), fromDepth);
            for (LongPieces pieces : reverse) {
                boolean isFound = forwardOrderLookUp.parse(pieces.getBlocks())
                        .map(LongPieces::new)
                        .anyMatch(target::equals);
                assertThat(isFound).isTrue();
            }
        }
    }

    @Test
    @Tag("long")
    void parseOverRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 2; size <= 13; size++) {
            List<Block> blocks = randoms.blocks(size);
            int fromDepth = blocks.size() + 1;

            ReverseOrderLookUp reverseOrderLookUp = new ReverseOrderLookUp(blocks.size(), fromDepth);
            List<Stream<Block>> reverse = reverseOrderLookUp.parse(blocks)
                    .collect(Collectors.toList());

            LongPieces target = new LongPieces(blocks);
            ForwardOrderLookUp forwardOrderLookUp = new ForwardOrderLookUp(blocks.size(), fromDepth);
            for (Stream<Block> stream : reverse) {
                List<Block> sample = stream
                        .map(block -> block != null ? block : randoms.block())
                        .collect(Collectors.toList());
                boolean isFound = forwardOrderLookUp.parse(sample)
                        .map(LongPieces::new)
                        .anyMatch(target::equals);
                assertThat(isFound).isTrue();
            }
        }
    }

    @Test
    void parseOver2Random() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 2; size <= 12; size++) {
            List<Block> blocks = randoms.blocks(size);
            int fromDepth = blocks.size() + 2;

            ReverseOrderLookUp reverseOrderLookUp = new ReverseOrderLookUp(blocks.size(), fromDepth);
            List<Stream<Block>> reverse = reverseOrderLookUp.parse(blocks)
                    .collect(Collectors.toList());

            LongPieces target = new LongPieces(blocks);
            ForwardOrderLookUp forwardOrderLookUp = new ForwardOrderLookUp(blocks.size(), fromDepth);
            for (Stream<Block> stream : reverse) {
                List<Block> sample = stream
                        .map(block -> block != null ? block : randoms.block())
                        .collect(Collectors.toList());
                boolean isFound = forwardOrderLookUp.parse(sample)
                        .map(LongPieces::new)
                        .anyMatch(target::equals);
                assertThat(isFound).isTrue();
            }
        }
    }
}