package common.datastore.blocks;

import core.mino.Piece;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReadOnlyListPiecesTest {
    @Test
    void create() throws Exception {
        Pieces pieces = new ReadOnlyListPieces(Arrays.asList(Piece.I, Piece.O, Piece.J, Piece.Z, Piece.S, Piece.T, Piece.L));
        assertThat(pieces.getPieces()).containsExactly(
                Piece.I, Piece.O, Piece.J, Piece.Z, Piece.S, Piece.T, Piece.L
        );
    }

    @Test
    void checkStream() throws Exception {
        Pieces pieces = new ReadOnlyListPieces(Arrays.asList(Piece.S, Piece.I, Piece.J, Piece.T, Piece.L, Piece.O, Piece.Z));
        assertThat(pieces.blockStream()).containsExactly(
                Piece.S, Piece.I, Piece.J, Piece.T, Piece.L, Piece.O, Piece.Z
        );
    }

    @Test
    void createRandom() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 22);
            List<Piece> blocks = randoms.blocks(size);
            Pieces pieces = new ReadOnlyListPieces(blocks);
            assertThat(pieces.getPieces()).isEqualTo(blocks);
        }
    }

    @Test
    void createRandomStream() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 22);
            List<Piece> blocks = randoms.blocks(size);
            Pieces pieces = new ReadOnlyListPieces(blocks);
            assertThat(pieces.getPieces()).isEqualTo(blocks);
        }
    }

    @Test
    void equalToLongPieces() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 22);
            List<Piece> pieces = randoms.blocks(size);
            Pieces readOnlyListPieces = new ReadOnlyListPieces(pieces);
            LongPieces longPieces = new LongPieces(pieces);
            assertThat(readOnlyListPieces.equals(longPieces))
                    .as(longPieces.getPieces().toString())
                    .isTrue();
        }
    }
}