package core.neighbor;

import core.mino.Piece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OriginalPieceFactoryTest {
    @Test
    void create() {
        OriginalPieceFactory pieceFactory = new OriginalPieceFactory(4);
        Set<OriginalPiece> pieces = pieceFactory.createPieces();

        // ALL
        assertThat(pieces).hasSize(604);

        // T
        assertThat(pieces.stream().filter(piece -> piece.getPiece() == Piece.T && piece.getRotate() == Rotate.Spawn))
                .hasSize(24);
        assertThat(pieces.stream().filter(piece -> piece.getPiece() == Piece.T && piece.getRotate() == Rotate.Reverse))
                .hasSize(24);

        assertThat(pieces.stream().filter(piece -> piece.getPiece() == Piece.T && piece.getRotate() == Rotate.Right))
                .hasSize(18);
        assertThat(pieces.stream().filter(piece -> piece.getPiece() == Piece.T && piece.getRotate() == Rotate.Left))
                .hasSize(18);

        // J
        assertThat(pieces.stream().filter(piece -> piece.getPiece() == Piece.J))
                .hasSize(84);

        // L
        assertThat(pieces.stream().filter(piece -> piece.getPiece() == Piece.L))
                .hasSize(84);

        // S
        assertThat(pieces.stream().filter(piece -> piece.getPiece() == Piece.S))
                .hasSize(84);

        // Z
        assertThat(pieces.stream().filter(piece -> piece.getPiece() == Piece.Z))
                .hasSize(84);

        // O
        assertThat(pieces.stream().filter(piece -> piece.getPiece() == Piece.O))
                .hasSize(108);
    }
}