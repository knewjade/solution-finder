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

public class LongPiecesTest {
    @Test
    public void create() throws Exception {
        Pieces pieces = new LongPieces(Arrays.asList(Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L));
        pieces = pieces.addAndReturnNew(Arrays.asList(Block.I, Block.J, Block.L));
        pieces = pieces.addAndReturnNew(Block.O);
        assertThat(pieces.getBlocks(), is(Arrays.asList(
                Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L, Block.I, Block.J, Block.L, Block.O
        )));
    }

    @Test
    public void createByStream() throws Exception {
        Pieces pieces = new LongPieces(Arrays.asList(Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L).stream());
        pieces = pieces.addAndReturnNew(Arrays.asList(Block.I, Block.J, Block.L));
        pieces = pieces.addAndReturnNew(Block.O);
        assertThat(pieces.getBlocks(), is(Arrays.asList(
                Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L, Block.I, Block.J, Block.L, Block.O
        )));
    }

    @Test
    public void checkStream() throws Exception {
        Pieces pieces = new LongPieces(Arrays.asList(Block.S, Block.I, Block.J, Block.T, Block.L, Block.O, Block.Z));
        assertThat(pieces.getBlockStream().collect(Collectors.toList()), is(pieces.getBlocks()));
    }

    @Test
    public void createRandom() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            ArrayList<Block> blocks = new ArrayList<>(randoms.blocks(1));
            Pieces pieces = new LongPieces(blocks);

            for (int addCount = 0; addCount < 3; addCount++) {
                List<Block> newBlocks = randoms.blocks(randoms.nextInt(0, 7));
                blocks.addAll(newBlocks);
                pieces = pieces.addAndReturnNew(newBlocks);
            }

            assertThat(pieces.getBlockStream().collect(Collectors.toList()), contains(blocks.toArray()));
        }
    }

    @Test
    public void createRandomSize22() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            ArrayList<Block> blocks = new ArrayList<>(randoms.blocks(22));
            Pieces pieces = new LongPieces(blocks);
            assertThat(pieces.getBlocks(), contains(blocks.toArray()));
        }
    }

    @Test
    public void equalToReadOnlyPieces() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 22);
            ArrayList<Block> blocks = new ArrayList<>(size);
            LongPieces longPieces = new LongPieces(blocks);
            Pieces readOnlyListPieces = new ReadOnlyListPieces(blocks);
            assertThat(longPieces.getBlocks().toString(), longPieces.equals(readOnlyListPieces), is(true));
        }
    }
}