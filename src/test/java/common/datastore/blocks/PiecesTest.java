package common.datastore.blocks;

import core.mino.Piece;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PiecesTest {
    @Test
    void testHashCode() throws Exception {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextIntOpen(4, 10);
            List<Piece> pieces = randoms.blocks(size);

            ReadOnlyListPieces readOnlyListPieces = new ReadOnlyListPieces(pieces);
            LongPieces longPieces = new LongPieces(pieces);
            assertThat(readOnlyListPieces.hashCode())
                    .as(pieces.toString())
                    .isEqualTo(longPieces.hashCode());
        }
    }
}
