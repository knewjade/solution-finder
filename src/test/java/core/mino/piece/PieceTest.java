package core.mino.piece;

import core.mino.Block;
import core.mino.Mino;
import core.mino.piece.Piece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PieceTest {
    @Test
    void testEquals() {
        Piece piece1 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        Piece piece2 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        assertThat(piece1.equals(piece2)).isTrue();
    }

    @Test
    void testEqualsDiffBlock() {
        Piece piece1 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        Piece piece2 = new Piece(new Mino(Block.L, Rotate.Spawn), 3, 4, 10);
        assertThat(piece1.equals(piece2)).isFalse();
    }

    @Test
    void testEqualsDiffRotate() {
        Piece piece1 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        Piece piece2 = new Piece(new Mino(Block.I, Rotate.Left), 3, 4, 10);
        assertThat(piece1.equals(piece2)).isFalse();
    }

    @Test
    void testEqualsDiffX() {
        Piece piece1 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        Piece piece2 = new Piece(new Mino(Block.I, Rotate.Left), 2, 4, 10);
        assertThat(piece1.equals(piece2)).isFalse();
    }

    @Test
    void testEqualsDiffY() {
        Piece piece1 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        Piece piece2 = new Piece(new Mino(Block.I, Rotate.Left), 3, 8, 10);
        assertThat(piece1.equals(piece2)).isFalse();
    }

    @Test
    void testEqualsDiffFieldHeight() {
        Piece piece1 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        Piece piece2 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 5);
        assertThat(piece1.equals(piece2)).isTrue();
    }

    @Test
    void testHashCode() {
        Piece piece1 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        Piece piece2 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        assertThat(piece1.hashCode()).isEqualTo(piece2.hashCode());
    }

    @Test
    void testHashCodeDiffBlock() {
        Piece piece1 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        Piece piece2 = new Piece(new Mino(Block.L, Rotate.Spawn), 3, 4, 10);
        assertThat(piece1.hashCode()).isNotEqualTo(piece2.hashCode());
    }

    @Test
    void testHashCodeDiffRotate() {
        Piece piece1 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        Piece piece2 = new Piece(new Mino(Block.I, Rotate.Left), 3, 4, 10);
        assertThat(piece1.hashCode()).isNotEqualTo(piece2.hashCode());
    }

    @Test
    void testHashCodeDiffX() {
        Piece piece1 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        Piece piece2 = new Piece(new Mino(Block.I, Rotate.Left), 2, 4, 10);
        assertThat(piece1.hashCode()).isNotEqualTo(piece2.hashCode());
    }

    @Test
    void testHashCodeDiffY() {
        Piece piece1 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        Piece piece2 = new Piece(new Mino(Block.I, Rotate.Left), 3, 8, 10);
        assertThat(piece1.hashCode()).isNotEqualTo(piece2.hashCode());
    }

    @Test
    void testHashCodeDiffFieldHeight() {
        Piece piece1 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 10);
        Piece piece2 = new Piece(new Mino(Block.I, Rotate.Spawn), 3, 4, 5);
        assertThat(piece1.hashCode()).isEqualTo(piece2.hashCode());
    }
}