package common.datastore.pieces;

import core.mino.Block;
import lib.Randoms;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class ReadOnlyListPiecesTest {
    @Test
    public void create() throws Exception {
        Pieces pieces = new ReadOnlyListPieces(Arrays.asList(Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L));
        assertThat(pieces.getBlocks(), is(Arrays.asList(
                Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L
        )));
    }

    @Test
    public void checkStream() throws Exception {
        Pieces pieces = new ReadOnlyListPieces(Arrays.asList(Block.S, Block.I, Block.J, Block.T, Block.L, Block.O, Block.Z));
        assertThat(pieces.getBlockStream().collect(Collectors.toList()), is(pieces.getBlocks()));
    }

    @Test
    public void createRandom() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 22);
            List<Block> blocks = randoms.blocks(size);
            Pieces pieces = new ReadOnlyListPieces(blocks);
            assertThat(pieces.getBlocks(), contains(blocks.toArray()));
        }
    }

    @Test
    public void createRandomStream() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 22);
            List<Block> blocks = randoms.blocks(size);
            Pieces pieces = new ReadOnlyListPieces(blocks);
            assertThat(pieces.getBlockStream().collect(Collectors.toList()), contains(blocks.toArray()));
        }
    }

    @Test
    public void equalToLongPieces() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 22);
            List<Block> blocks = randoms.blocks(size);
            Pieces readOnlyListPieces = new ReadOnlyListPieces(blocks);
            LongPieces longPieces = new LongPieces(blocks);
            assertThat(longPieces.getBlocks().toString(), readOnlyListPieces.equals(longPieces), is(true));
        }
    }
}