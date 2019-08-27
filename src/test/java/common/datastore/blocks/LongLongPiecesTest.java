package common.datastore.blocks;

import core.mino.Piece;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LongLongPiecesTest {
    @Test
    void case25() {
        List<Piece> list = Arrays.asList(
                Piece.S, Piece.Z, Piece.T, Piece.I, Piece.O,
                Piece.S, Piece.Z, Piece.T, Piece.I, Piece.O,
                Piece.S, Piece.Z, Piece.T, Piece.I, Piece.O,
                Piece.S, Piece.Z, Piece.T, Piece.I, Piece.O,
                Piece.S, Piece.Z, Piece.T, Piece.I, Piece.O
        );
        LongLongPieces pieces = new LongLongPieces(list);
        assertThat(pieces.getPieces()).isEqualTo(list);
        assertThat(new LongLongPieces(pieces).getPieces()).isEqualTo(list);
    }
}