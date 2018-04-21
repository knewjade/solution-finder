package common.tree;

import common.datastore.blocks.LongPieces;
import core.mino.Piece;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SuccessTreeHeadTest {
    @Test
    void checksWithHold1() {
        // 基本ケース
        SuccessTreeHead head = new SuccessTreeHead();
        head.register(pieces(Piece.I, Piece.O));

        assertThat(head.checksWithHold(pieces(Piece.I, Piece.O))).isTrue();
        assertThat(head.checksWithHold(pieces(Piece.O, Piece.I))).isTrue();
        assertThat(head.checksWithHold(pieces(Piece.S, Piece.I, Piece.O))).isTrue();

        assertThat(head.checksWithHold(pieces(Piece.O, Piece.O))).isFalse();
        assertThat(head.checksWithHold(pieces(Piece.I, Piece.I))).isFalse();
        assertThat(head.checksWithHold(pieces(Piece.S, Piece.I))).isFalse();
        assertThat(head.checksWithHold(pieces(Piece.S, Piece.O, Piece.I))).isFalse();
    }

    @Test
    void checksWithHold2() {
        // Pieceが少ない
        SuccessTreeHead head = new SuccessTreeHead();
        head.register(pieces(Piece.I, Piece.O));

        assertThat(head.checksWithHold(pieces())).isTrue();
        assertThat(head.checksWithHold(pieces(Piece.I))).isTrue();
        assertThat(head.checksWithHold(pieces(Piece.O))).isTrue();
    }

    @Test
    void checksWithoutHold1() {
        // 基本ケース
        SuccessTreeHead head = new SuccessTreeHead();
        head.register(pieces(Piece.I, Piece.O));

        assertThat(head.checksWithoutHold(pieces(Piece.I, Piece.O))).isTrue();
        assertThat(head.checksWithHold(pieces(Piece.I, Piece.O, Piece.Z))).isTrue();

        assertThat(head.checksWithoutHold(pieces(Piece.O, Piece.I))).isFalse();
        assertThat(head.checksWithoutHold(pieces(Piece.I, Piece.T, Piece.O))).isFalse();
        assertThat(head.checksWithoutHold(pieces(Piece.L, Piece.I, Piece.O))).isFalse();
    }

    @Test
    void checksWithoutHold2() {
        // Pieceが少ない
        SuccessTreeHead head = new SuccessTreeHead();
        head.register(pieces(Piece.I, Piece.O));

        assertThat(head.checksWithoutHold(pieces())).isTrue();
        assertThat(head.checksWithoutHold(pieces(Piece.I))).isTrue();
        assertThat(head.checksWithoutHold(pieces(Piece.O))).isFalse();
    }

    private LongPieces pieces(Piece... pieces) {
        return new LongPieces(pieces);
    }
}