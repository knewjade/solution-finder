package core.mino;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PieceTest {
    @Test
    void getBlock() {
        assertThat(Piece.getBlock(0)).isEqualTo(Piece.T);
        assertThat(Piece.getBlock(1)).isEqualTo(Piece.I);
        assertThat(Piece.getBlock(2)).isEqualTo(Piece.L);
        assertThat(Piece.getBlock(3)).isEqualTo(Piece.J);
        assertThat(Piece.getBlock(4)).isEqualTo(Piece.S);
        assertThat(Piece.getBlock(5)).isEqualTo(Piece.Z);
        assertThat(Piece.getBlock(6)).isEqualTo(Piece.O);
    }

    @Test
    void valueList() {
        List<Piece> pieces = Piece.valueList();
        assertThat(pieces)
                .hasSize(7)
                .containsExactly(Piece.T, Piece.I, Piece.L, Piece.J, Piece.S, Piece.Z, Piece.O);
    }

    @Test
    void getSize() {
        assertThat(Piece.getSize()).isEqualTo(7);
    }

    @Test
    void getNumber() {
        assertThat(Piece.T.getNumber()).isEqualTo(0);
        assertThat(Piece.I.getNumber()).isEqualTo(1);
        assertThat(Piece.L.getNumber()).isEqualTo(2);
        assertThat(Piece.J.getNumber()).isEqualTo(3);
        assertThat(Piece.S.getNumber()).isEqualTo(4);
        assertThat(Piece.Z.getNumber()).isEqualTo(5);
        assertThat(Piece.O.getNumber()).isEqualTo(6);
    }

    @Test
    void getName() {
        assertThat(Piece.T.getName()).isEqualTo("T");
        assertThat(Piece.I.getName()).isEqualTo("I");
        assertThat(Piece.L.getName()).isEqualTo("L");
        assertThat(Piece.J.getName()).isEqualTo("J");
        assertThat(Piece.S.getName()).isEqualTo("S");
        assertThat(Piece.Z.getName()).isEqualTo("Z");
        assertThat(Piece.O.getName()).isEqualTo("O");
    }
}