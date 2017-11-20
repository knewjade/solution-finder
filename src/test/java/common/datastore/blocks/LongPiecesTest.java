package common.datastore.blocks;

import core.mino.Piece;
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
        Pieces pieces = new LongPieces(Arrays.asList(Piece.I, Piece.O, Piece.J, Piece.Z, Piece.S, Piece.T, Piece.L));
        pieces = pieces.addAndReturnNew(Arrays.asList(Piece.I, Piece.J, Piece.L));
        pieces = pieces.addAndReturnNew(Piece.O);
        assertThat(pieces.getPieces()).containsExactly(
                Piece.I, Piece.O, Piece.J, Piece.Z, Piece.S, Piece.T, Piece.L, Piece.I, Piece.J, Piece.L, Piece.O
        );
    }

    @Test
    void createByStream() throws Exception {
        Pieces pieces = new LongPieces(Stream.of(Piece.I, Piece.O, Piece.J, Piece.Z, Piece.S, Piece.T, Piece.L));
        pieces = pieces.addAndReturnNew(Arrays.asList(Piece.I, Piece.J, Piece.L));
        pieces = pieces.addAndReturnNew(Piece.O);
        assertThat(pieces.getPieces()).containsExactly(
                Piece.I, Piece.O, Piece.J, Piece.Z, Piece.S, Piece.T, Piece.L, Piece.I, Piece.J, Piece.L, Piece.O
        );
    }

    @Test
    void checkStream() throws Exception {
        Pieces pieces = new LongPieces(Arrays.asList(Piece.S, Piece.I, Piece.J, Piece.T, Piece.L, Piece.O, Piece.Z));
        assertThat(pieces.blockStream()).containsExactly(
                Piece.S, Piece.I, Piece.J, Piece.T, Piece.L, Piece.O, Piece.Z
        );
    }

    @Test
    void checkPieceArray() throws Exception {
        Pieces pieces = new LongPieces(Arrays.asList(Piece.S, Piece.I, Piece.J, Piece.T, Piece.L, Piece.O, Piece.Z));
        assertThat(pieces.getPieceArray()).containsExactly(Piece.S, Piece.I, Piece.J, Piece.T, Piece.L, Piece.O, Piece.Z);
    }

    @Test
    void checkEquals() throws Exception {
        Piece piece = Piece.getBlock(0);
        Pieces pieces1 = new LongPieces(Arrays.asList(piece, piece, piece));
        Pieces pieces2 = new LongPieces(Arrays.asList(piece, piece, piece, piece));
        assertThat(pieces1.equals(pieces2)).isFalse();
    }

    @Test
    void createRandom() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            ArrayList<Piece> blocks = new ArrayList<>(randoms.blocks(1));
            Pieces pieces = new LongPieces(blocks);

            for (int addCount = 0; addCount < 3; addCount++) {
                List<Piece> newPieces = randoms.blocks(randoms.nextInt(0, 7));
                blocks.addAll(newPieces);
                pieces = pieces.addAndReturnNew(newPieces);
            }

            assertThat(pieces.getPieces()).isEqualTo(blocks);
        }
    }

    @Test
    void createRandomSize22() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            List<Piece> blocks = randoms.blocks(22);
            Pieces pieces = new LongPieces(blocks);
            assertThat(pieces.blockStream()).containsExactlyElementsOf(blocks);

            assertThat(pieces.getPieces()).isEqualTo(blocks);
        }
    }

    @Test
    void createEqualsRandom() throws Exception {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            int size1 = randoms.nextInt(1, 21);
            List<Piece> blocks1 = randoms.blocks(size1);

            int size2 = randoms.nextInt(1, 22);
            List<Piece> blocks2 = randoms.blocks(size2);

            if (blocks1.equals(blocks2))
                blocks1.add(Piece.I);

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
            List<Piece> pieces = randoms.blocks(size);
            LongPieces longPieces = new LongPieces(pieces);
            Pieces readOnlyListPieces = new ReadOnlyListPieces(pieces);
            assertThat(longPieces.equals(readOnlyListPieces))
                    .as(longPieces.getPieces().toString())
                    .isTrue();
        }
    }
}