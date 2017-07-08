package common.datastore.pieces;

import core.mino.Block;
import lib.Randoms;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PiecesTest {
    @Test
    public void testHashCode() throws Exception {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(4, 10);
            List<Block> blocks = randoms.blocks(size);

            ReadOnlyListPieces readOnlyListPieces = new ReadOnlyListPieces(blocks);
            LongPieces longPieces = new LongPieces(blocks);
            assertThat(blocks.toString(), readOnlyListPieces.hashCode(), is(longPieces.hashCode()));
        }
    }
}
