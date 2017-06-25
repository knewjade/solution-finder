package common.datastore.pieces;

import core.mino.Block;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class FrozenLongPiecesTest {
    @Test
    public void create() throws Exception {
        LongPieces pieces = new LongPieces(Arrays.asList(Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L));
        FrozenLongPieces frozenLongPieces = pieces.fix();
        assertThat(frozenLongPieces.getBlocks(), is(Arrays.asList(
                Block.I, Block.O, Block.J, Block.Z, Block.S, Block.T, Block.L
        )));
    }



}