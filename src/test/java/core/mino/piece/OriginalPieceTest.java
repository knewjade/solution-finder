package core.mino.piece;

import core.mino.Piece;
import core.mino.Mino;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OriginalPieceTest {
    @Test
    void testEquals() {
        OriginalPiece piece1 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        OriginalPiece piece2 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        assertThat(piece1.equals(piece2)).isTrue();
    }

    @Test
    void testEqualsDiffBlock() {
        OriginalPiece piece1 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        OriginalPiece piece2 = new OriginalPiece(new Mino(Piece.L, Rotate.Spawn), 3, 4, 10);
        assertThat(piece1.equals(piece2)).isFalse();
    }

    @Test
    void testEqualsDiffRotate() {
        OriginalPiece piece1 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        OriginalPiece piece2 = new OriginalPiece(new Mino(Piece.I, Rotate.Left), 3, 4, 10);
        assertThat(piece1.equals(piece2)).isFalse();
    }

    @Test
    void testEqualsDiffX() {
        OriginalPiece piece1 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        OriginalPiece piece2 = new OriginalPiece(new Mino(Piece.I, Rotate.Left), 2, 4, 10);
        assertThat(piece1.equals(piece2)).isFalse();
    }

    @Test
    void testEqualsDiffY() {
        OriginalPiece piece1 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        OriginalPiece piece2 = new OriginalPiece(new Mino(Piece.I, Rotate.Left), 3, 8, 10);
        assertThat(piece1.equals(piece2)).isFalse();
    }

    @Test
    void testEqualsDiffFieldHeight() {
        OriginalPiece piece1 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        OriginalPiece piece2 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 5);
        assertThat(piece1.equals(piece2)).isTrue();
    }

    @Test
    void testHashCode() {
        OriginalPiece piece1 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        OriginalPiece piece2 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        assertThat(piece1.hashCode()).isEqualTo(piece2.hashCode());
    }

    @Test
    void testHashCodeDiffBlock() {
        OriginalPiece piece1 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        OriginalPiece piece2 = new OriginalPiece(new Mino(Piece.L, Rotate.Spawn), 3, 4, 10);
        assertThat(piece1.hashCode()).isNotEqualTo(piece2.hashCode());
    }

    @Test
    void testHashCodeDiffRotate() {
        OriginalPiece piece1 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        OriginalPiece piece2 = new OriginalPiece(new Mino(Piece.I, Rotate.Left), 3, 4, 10);
        assertThat(piece1.hashCode()).isNotEqualTo(piece2.hashCode());
    }

    @Test
    void testHashCodeDiffX() {
        OriginalPiece piece1 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        OriginalPiece piece2 = new OriginalPiece(new Mino(Piece.I, Rotate.Left), 2, 4, 10);
        assertThat(piece1.hashCode()).isNotEqualTo(piece2.hashCode());
    }

    @Test
    void testHashCodeDiffY() {
        OriginalPiece piece1 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        OriginalPiece piece2 = new OriginalPiece(new Mino(Piece.I, Rotate.Left), 3, 8, 10);
        assertThat(piece1.hashCode()).isNotEqualTo(piece2.hashCode());
    }

    @Test
    void testHashCodeDiffFieldHeight() {
        OriginalPiece piece1 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 10);
        OriginalPiece piece2 = new OriginalPiece(new Mino(Piece.I, Rotate.Spawn), 3, 4, 5);
        assertThat(piece1.hashCode()).isEqualTo(piece2.hashCode());
    }
}