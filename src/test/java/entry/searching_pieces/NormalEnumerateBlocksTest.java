package entry.searching_pieces;

import common.datastore.pieces.LongBlocks;
import common.pattern.BlocksGenerator;
import common.pattern.IBlocksGenerator;
import core.mino.Block;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class NormalEnumerateBlocksTest {
    @Test
    void enumerateHoldOver1() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, true);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(840);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateHoldOver2() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, true);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(2520);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateHoldOver3() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("I, *p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, true);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(840);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateHoldOverOne() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("I, S, Z, O, T, J, L");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, true);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(1);
        assertThat(core.getCounter()).isEqualTo(1);
    }

    @Test
    void enumerateHoldMulti() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator(Arrays.asList(
                "T, J, O, Z, I",
                "J, O, S, T, Z",
                "T, J, O, I, S",
                "T, J, O, Z, I"
        ));
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, true);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(3);
        assertThat(core.getCounter()).isEqualTo(4);
    }

    @Test
    void enumerateNoHoldOver1() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, false);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(210);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateNoHoldOver2() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, false);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(840);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateNoHoldOver3() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("I, *p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, false);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(210);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateNoHoldOverOne() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("I, S, Z, O");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, false);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(1);
        assertThat(core.getCounter()).isEqualTo(1);
    }

    @Test
    void enumerateNoHoldMulti() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator(Arrays.asList(
                "T, J, O, Z",
                "J, O, S, T",
                "T, J, O, I"
        ));
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, false);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(2);
        assertThat(core.getCounter()).isEqualTo(3);
    }

    @Test
    void enumerateHoldJust() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("*p3");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, true);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(210);
        assertThat(core.getCounter()).isEqualTo(210);
    }

    @Test
    void enumerateNoHoldJust() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator("*p3");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, false);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(210);
        assertThat(core.getCounter()).isEqualTo(210);
    }

    @Test
    void enumerateHoldJustMulti() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator(Arrays.asList(
                "T, J, O, Z, I",
                "J, O, S, T, Z",
                "T, J, O, I, S",
                "T, J, O, Z, I"
        ));
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 5, true);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(3);
        assertThat(core.getCounter()).isEqualTo(4);
    }

    @Test
    void enumerateNoHoldJustMulti() throws Exception {
        IBlocksGenerator generator = new BlocksGenerator(Arrays.asList(
                "T, J, O, Z",
                "J, O, S, T",
                "T, J, O, I"
        ));
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, false);
        Set<LongBlocks> pieces = core.enumerate();
        assertThat(pieces).hasSize(3);
        assertThat(core.getCounter()).isEqualTo(3);
    }

    @Test
    void enumerateJustRandomNoHold() throws Exception {
        List<Block> failedBlocks = Arrays.asList(Block.I, Block.O, Block.L, Block.J, Block.S, Block.Z);
        List<Block> allBlocks = new ArrayList<>(Block.valueList());

        for (int size = 1; size <= 7; size++) {
            IBlocksGenerator blocksGenerator = new BlocksGenerator("T, *p" + size);
            NormalEnumeratePieces core = new NormalEnumeratePieces(blocksGenerator, size + 1, false);
            Set<LongBlocks> pieces = core.enumerate();

            for (int count = 0; count < 1000; count++) {
                List<Block> sample = new ArrayList<>();
                sample.add(Block.T);

                Collections.shuffle(allBlocks);
                sample.addAll(allBlocks.subList(0, size));

                assertThat(new LongBlocks(sample)).isIn(pieces);

                for (Block block : failedBlocks) {
                    sample.set(0, block);
                    assertThat(new LongBlocks(sample)).isNotIn(pieces);
                }
            }
        }
    }

    @Test
    void enumerateOverRandomNoHold() throws Exception {
        List<Block> failedBlocks = Arrays.asList(Block.I, Block.O, Block.L, Block.J, Block.S, Block.Z);
        List<Block> allBlocks = new ArrayList<>(Block.valueList());

        for (int size = 1; size <= 7; size++) {
            IBlocksGenerator blocksGenerator = new BlocksGenerator("T, *p" + size);
            NormalEnumeratePieces core = new NormalEnumeratePieces(blocksGenerator, size, false);
            Set<LongBlocks> pieces = core.enumerate();

            for (int count = 0; count < 1000; count++) {
                List<Block> sample = new ArrayList<>();
                sample.add(Block.T);

                Collections.shuffle(allBlocks);
                sample.addAll(allBlocks.subList(0, size - 1));

                assertThat(new LongBlocks(sample)).isIn(pieces);

                for (Block block : failedBlocks) {
                    sample.set(0, block);
                    assertThat(new LongBlocks(sample)).isNotIn(pieces);
                }
            }
        }
    }
}