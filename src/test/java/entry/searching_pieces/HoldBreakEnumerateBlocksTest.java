package entry.searching_pieces;

import common.datastore.pieces.LongBlocks;
import common.pattern.BlocksGenerator;
import common.pattern.IBlocksGenerator;
import core.mino.Block;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class HoldBreakEnumerateBlocksTest {
    @Test
    void enumerate1() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("*p7");
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 3);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(210);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerate2() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("*p7");
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 4);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(840);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateOverAny() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("T, J, O, Z");
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 3);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(8);
        assertThat(core.getCounter()).isEqualTo(1);
    }

    @Test
    void enumerateMulti() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator(Arrays.asList(
                "T, J, O, Z",
                "T, O, J, T",
                "T, J, O, Z"
        ));
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 3);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(13);
        assertThat(core.getCounter()).isEqualTo(3);
    }

    @Test
    void enumerateJust() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("*p3");
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 3);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(210);
        assertThat(core.getCounter()).isEqualTo(210);
    }

    @Test
    void enumerateJustAny() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("T, O, S");
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 3);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(4);
        assertThat(core.getCounter()).isEqualTo(1);
    }

    @Test
    void enumerateJustRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 3; size <= 15; size++) {
            List<Block> blocks = randoms.blocks(size);
            String pattern = blocks.stream()
                    .map(Block::getName)
                    .collect(Collectors.joining(","));
            IBlocksGenerator blocksGenerator = new BlocksGenerator(pattern);
            HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(blocksGenerator, size);
            Set<LongBlocks> pieces = core.enumerate();

            for (int count = 0; count < 10000; count++) {
                ArrayList<Block> sample = new ArrayList<>();
                int holdIndex = 0;
                for (int index = 1; index < size; index++) {
                    if (randoms.nextBoolean(0.3)) {
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

                assertThat(new LongBlocks(sample)).isIn(pieces);
            }
        }
    }

    @Test
    void enumerateOverRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 3; size <= 15; size++) {
            List<Block> blocks = randoms.blocks(size);
            String pattern = blocks.stream()
                    .map(Block::getName)
                    .collect(Collectors.joining(","));
            IBlocksGenerator blocksGenerator = new BlocksGenerator(pattern);
            HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(blocksGenerator, size - 1);
            Set<LongBlocks> pieces = core.enumerate();

            for (int count = 0; count < 10000; count++) {
                ArrayList<Block> sample = new ArrayList<>();
                int holdIndex = 0;
                for (int index = 1; index < size; index++) {
                    if (randoms.nextBoolean(0.3)) {
                        // そのまま追加
                        sample.add(blocks.get(index));
                    } else {
                        // ホールドを追加
                        sample.add(blocks.get(holdIndex));
                        holdIndex = index;
                    }
                }

                assertThat(new LongBlocks(sample)).isIn(pieces);
            }
        }
    }
}