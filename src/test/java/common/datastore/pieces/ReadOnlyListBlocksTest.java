package common.datastore.pieces;

import core.mino.Block;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReadOnlyListBlocksTest {
    @Test
    void create() throws Exception {
        Blocks blocks = new ReadOnlyListBlocks(Arrays.asList(Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L));
        assertThat(blocks.getBlocks()).containsExactly(
                Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L
        );
    }

    @Test
    void checkStream() throws Exception {
        Blocks blocks = new ReadOnlyListBlocks(Arrays.asList(Block.S, Block.I, Block.J, Block.T, Block.L, Block.O, Block.Z));
        assertThat(blocks.blockStream()).containsExactly(
                Block.S, Block.I, Block.J, Block.T, Block.L, Block.O, Block.Z
        );
    }

    @Test
    void createRandom() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 22);
            List<Block> blocks = randoms.blocks(size);
            Blocks pieces = new ReadOnlyListBlocks(blocks);
            assertThat(pieces.getBlocks()).isEqualTo(blocks);
        }
    }

    @Test
    void createRandomStream() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 22);
            List<Block> blocks = randoms.blocks(size);
            Blocks pieces = new ReadOnlyListBlocks(blocks);
            assertThat(pieces.getBlocks()).isEqualTo(blocks);
        }
    }

    @Test
    void equalToLongPieces() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 22);
            List<Block> blocks = randoms.blocks(size);
            Blocks readOnlyListBlocks = new ReadOnlyListBlocks(blocks);
            LongBlocks longPieces = new LongBlocks(blocks);
            assertThat(readOnlyListBlocks.equals(longPieces))
                    .as(longPieces.getBlocks().toString())
                    .isTrue();
        }
    }
}