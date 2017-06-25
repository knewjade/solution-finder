package common.datastore.pieces;

import core.mino.Block;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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
}