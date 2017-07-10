package common.order;

import common.comparator.PiecesNumberComparator;
import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import core.mino.Block;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ForwardOrderLookUpTest {
    @Test
    void parseJustBlocksCount() throws Exception {
        List<Block> blockList = Block.valueList();
        int toDepth = blockList.size();

        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth, blockList.size());
        HashSet<LongPieces> forward = lookUp.parse(blockList)
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));

        assertThat(forward).hasSize(64);
    }

    @Test
    void parseOverBlocksCount() throws Exception {
        List<Block> blockList = Block.valueList();
        int toDepth = blockList.size() - 1;

        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth, blockList.size());
        HashSet<LongPieces> forward = lookUp.parse(blockList)
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));

        assertThat(forward).hasSize(64);
    }

    @Test
    void parseJustBlocks() throws Exception {
        List<Block> blockList = Arrays.asList(Block.I, Block.T, Block.Z, Block.O, Block.I, Block.L);
        int toDepth = blockList.size();

        PiecesNumberComparator comparator = new PiecesNumberComparator();
        List<LongPieces> forward1 = OrderLookup.forwardBlocks(blockList, toDepth).stream()
                .map(StackOrder::toList)
                .map(LongPieces::new)
                .sorted(comparator)
                .collect(Collectors.toList());

        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth, blockList.size());
        List<LongPieces> forward2 = lookUp.parse(blockList)
                .map(blockStream -> blockStream.collect(Collectors.toList()))
                .map(LongPieces::new)
                .sorted(comparator)
                .collect(Collectors.toList());

        assertThat(forward2).isEqualTo(forward1);
    }

    @Test
    void parseOverBlocks() throws Exception {
        List<Block> blockList = Arrays.asList(Block.I, Block.T, Block.Z, Block.O, Block.I, Block.L);
        int toDepth = blockList.size() - 1;

        PiecesNumberComparator comparator = new PiecesNumberComparator();
        List<LongPieces> forward1 = OrderLookup.forwardBlocks(blockList, toDepth).stream()
                .map(StackOrder::toList)
                .map(LongPieces::new)
                .sorted(comparator)
                .collect(Collectors.toList());

        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth, blockList.size());
        List<LongPieces> forward2 = lookUp.parse(blockList)
                .map(blockStream -> blockStream.collect(Collectors.toList()))
                .map(LongPieces::new)
                .sorted(comparator)
                .collect(Collectors.toList());

        assertThat(forward2).isEqualTo(forward1);
    }

    @Test
    void parseJustBlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 2; size <= 15; size++) {
            List<Block> blocks = randoms.blocks(size);
            int toDepth = blocks.size();

            ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth, blocks.size());
            HashSet<LongPieces> forward = lookUp.parse(blocks)
                    .map(LongPieces::new)
                    .collect(Collectors.toCollection(HashSet::new));

            for (int count = 0; count < 10000; count++) {
                ArrayList<Block> sample = new ArrayList<>();
                int holdIndex = 0;
                for (int index = 1; index < size; index++) {
                    if (randoms.nextBoolean()) {
                        // そのまま追加
                        sample.add(blocks.get(index));
                    } else {
                        // ホールドを追加
                        sample.add(blocks.get(holdIndex));
                        holdIndex = index;
                    }
                }

                // ホールドを追加
                sample.add(blocks.get(holdIndex));

                assertThat(new LongPieces(sample)).isIn(forward);
            }
        }
    }

    @Test
    void parseOverBlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 3; size <= 15; size++) {
            List<Block> blocks = randoms.blocks(size);
            int toDepth = blocks.size();

            ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth - 1, blocks.size());
            HashSet<LongPieces> forward = lookUp.parse(blocks)
                    .map(LongPieces::new)
                    .collect(Collectors.toCollection(HashSet::new));

            for (int count = 0; count < 10000; count++) {
                ArrayList<Block> sample = new ArrayList<>();
                int holdIndex = 0;
                for (int index = 1; index < size; index++) {
                    if (randoms.nextBoolean()) {
                        // そのまま追加
                        sample.add(blocks.get(index));
                    } else {
                        // ホールドを追加
                        sample.add(blocks.get(holdIndex));
                        holdIndex = index;
                    }
                }

                assertThat(new LongPieces(sample)).isIn(forward);
            }
        }
    }

    @Test
    void parseOver2BlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 4; size <= 15; size++) {
            List<Block> blocks = randoms.blocks(size);
            int toDepth = blocks.size();

            ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth - 2, blocks.size());
            HashSet<LongPieces> forward = lookUp.parse(blocks)
                    .map(LongPieces::new)
                    .collect(Collectors.toCollection(HashSet::new));

            for (int count = 0; count < 10000; count++) {
                ArrayList<Block> sample = new ArrayList<>();
                int holdIndex = 0;
                for (int index = 1; index < size - 1; index++) {
                    if (randoms.nextBoolean()) {
                        // そのまま追加
                        sample.add(blocks.get(index));
                    } else {
                        // ホールドを追加
                        sample.add(blocks.get(holdIndex));
                        holdIndex = index;
                    }
                }

                assertThat(new LongPieces(sample)).isIn(forward);
            }
        }
    }
}