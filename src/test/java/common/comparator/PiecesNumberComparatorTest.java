package common.comparator;

import common.datastore.blocks.LongPieces;
import core.mino.Piece;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PiecesNumberComparatorTest {
    @Test
    void compare() throws Exception {
        LongPieces pieces1 = new LongPieces(Arrays.asList(Piece.T, Piece.O, Piece.I));
        LongPieces pieces2 = new LongPieces(Arrays.asList(Piece.T, Piece.O, Piece.I));
        PiecesNumberComparator comparator = new PiecesNumberComparator();
        assertThat(comparator.compare(pieces1, pieces2)).isEqualTo(0);
        assertThat(comparator.compare(pieces2, pieces1)).isEqualTo(0);
    }

    @Test
    void compareDiffSize() throws Exception {
        LongPieces pieces1 = new LongPieces(Arrays.asList(Piece.T, Piece.O, Piece.I));
        LongPieces pieces2 = new LongPieces(Arrays.asList(Piece.T, Piece.O, Piece.I, Piece.J));

        // assert is not 0 & sign reversed
        PiecesNumberComparator comparator = new PiecesNumberComparator();
        assertThat(comparator.compare(pieces1, pieces2) * comparator.compare(pieces2, pieces1))
                .as(pieces2.toString())
                .isLessThan(0);
    }

    @Test
    void compareDiffRandom() throws Exception {
        List<Piece> allPieces = Arrays.asList(Piece.T, Piece.I, Piece.O, Piece.S, Piece.Z, Piece.J, Piece.L, Piece.T, Piece.I, Piece.O, Piece.S, Piece.Z, Piece.J, Piece.L);

        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            List<Piece> blocks1 = randoms.sample(allPieces, randoms.nextIntOpen(10));
            List<Piece> blocks2 = randoms.sample(allPieces, randoms.nextIntOpen(10));

            if (blocks1.equals(blocks2))
                blocks1.add(Piece.O);

            LongPieces pieces1 = new LongPieces(blocks1);
            LongPieces pieces2 = new LongPieces(blocks2);

            // assert is not 0 & sign reversed
            PiecesNumberComparator comparator = new PiecesNumberComparator();
            assertThat(comparator.compare(pieces1, pieces2) * comparator.compare(pieces2, pieces1))
                    .as(pieces2.toString())
                    .isLessThan(0);
        }
    }
}