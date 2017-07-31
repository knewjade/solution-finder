package common.datastore.pieces;

import core.mino.Block;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PiecesTest {
    @Test
    void testHashCode() throws Exception {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(4, 10);
            List<Block> blocks = randoms.blocks(size);

            ReadOnlyListPieces readOnlyListPieces = new ReadOnlyListPieces(blocks);
            LongPieces longPieces = new LongPieces(blocks);
            assertThat(readOnlyListPieces.hashCode())
                    .as(blocks.toString())
                    .isEqualTo(longPieces.hashCode());
        }
    }
}
