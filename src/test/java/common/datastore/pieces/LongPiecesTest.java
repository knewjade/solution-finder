package common.datastore.pieces;

import core.mino.Block;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LongPiecesTest {
    @Test
    void create() throws Exception {
        Pieces pieces = new LongPieces(Arrays.asList(Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L));
        pieces = pieces.addAndReturnNew(Arrays.asList(Block.I, Block.J, Block.L));
        pieces = pieces.addAndReturnNew(Block.O);
        assertThat(pieces.getBlocks()).containsExactly(
                Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L, Block.I, Block.J, Block.L, Block.O
        );
    }

    @Test
    void createByStream() throws Exception {
        Pieces pieces = new LongPieces(Stream.of(Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L));
        pieces = pieces.addAndReturnNew(Arrays.asList(Block.I, Block.J, Block.L));
        pieces = pieces.addAndReturnNew(Block.O);
        assertThat(pieces.getBlocks()).containsExactly(
                Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L, Block.I, Block.J, Block.L, Block.O
        );
    }

    @Test
    void checkStream() throws Exception {
        Pieces pieces = new LongPieces(Arrays.asList(Block.S, Block.I, Block.J, Block.T, Block.L, Block.O, Block.Z));
        assertThat(pieces.getBlockStream()).containsExactly(
                Block.S, Block.I, Block.J, Block.T, Block.L, Block.O, Block.Z
        );
    }

    @Test
    void checkEquals() throws Exception {
        Block block = Block.getBlock(0);
        Pieces pieces1 = new LongPieces(Arrays.asList(block, block, block));
        Pieces pieces2 = new LongPieces(Arrays.asList(block, block, block, block));
        assertThat(pieces1.equals(pieces2)).isFalse();
    }

    @Test
    void createRandom() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            ArrayList<Block> blocks = new ArrayList<>(randoms.blocks(1));
            Pieces pieces = new LongPieces(blocks);

            for (int addCount = 0; addCount < 3; addCount++) {
                List<Block> newBlocks = randoms.blocks(randoms.nextInt(0, 7));
                blocks.addAll(newBlocks);
                pieces = pieces.addAndReturnNew(newBlocks);
            }

            assertThat(pieces.getBlocks()).isEqualTo(blocks);
        }
    }

    @Test
    void createRandomSize22() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            List<Block> blocks = randoms.blocks(22);
            Pieces pieces = new LongPieces(blocks);
            assertThat(pieces.getBlockStream()).containsExactlyElementsOf(blocks);

            assertThat(pieces.getBlocks()).isEqualTo(blocks);
        }
    }

    @Test
    void createEqualsRandom() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            int size1 = randoms.nextInt(1, 21);
            List<Block> blocks1 = randoms.blocks(size1);

            int size2 = randoms.nextInt(1, 22);
            List<Block> blocks2 = randoms.blocks(size2);

            if (blocks1.equals(blocks2))
                blocks1.add(Block.I);

            Pieces pieces1 = new LongPieces(blocks1);
            Pieces pieces2 = new LongPieces(blocks2);

            assertThat(pieces1.equals(pieces2)).isFalse();
        }
    }

    @Test
    void equalToReadOnlyPieces() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 22);
            List<Block> blocks = randoms.blocks(size);
            LongPieces longPieces = new LongPieces(blocks);
            Pieces readOnlyListPieces = new ReadOnlyListPieces(blocks);
            assertThat(longPieces.equals(readOnlyListPieces))
                    .as(longPieces.getBlocks().toString())
                    .isTrue();
        }
    }
}