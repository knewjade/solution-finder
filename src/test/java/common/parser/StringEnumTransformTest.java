package common.parser;

import core.mino.Piece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringEnumTransformTest {
    @Test
    void toBlockString() {
        assertThat(StringEnumTransform.toPiece("T")).isEqualTo(Piece.T);
        assertThat(StringEnumTransform.toPiece("J")).isEqualTo(Piece.J);
        assertThat(StringEnumTransform.toPiece("L")).isEqualTo(Piece.L);
        assertThat(StringEnumTransform.toPiece("S")).isEqualTo(Piece.S);
        assertThat(StringEnumTransform.toPiece("Z")).isEqualTo(Piece.Z);
        assertThat(StringEnumTransform.toPiece("O")).isEqualTo(Piece.O);
        assertThat(StringEnumTransform.toPiece("I")).isEqualTo(Piece.I);
    }

    @Test
    void toBlockChar() {
        assertThat(StringEnumTransform.toPiece('T')).isEqualTo(Piece.T);
        assertThat(StringEnumTransform.toPiece('J')).isEqualTo(Piece.J);
        assertThat(StringEnumTransform.toPiece('L')).isEqualTo(Piece.L);
        assertThat(StringEnumTransform.toPiece('S')).isEqualTo(Piece.S);
        assertThat(StringEnumTransform.toPiece('Z')).isEqualTo(Piece.Z);
        assertThat(StringEnumTransform.toPiece('O')).isEqualTo(Piece.O);
        assertThat(StringEnumTransform.toPiece('I')).isEqualTo(Piece.I);
    }

    @Test
    void toRotate() {
        assertThat(StringEnumTransform.toRotate("0")).isEqualTo(Rotate.Spawn);
        assertThat(StringEnumTransform.toRotate("2")).isEqualTo(Rotate.Reverse);
        assertThat(StringEnumTransform.toRotate("L")).isEqualTo(Rotate.Left);
        assertThat(StringEnumTransform.toRotate("R")).isEqualTo(Rotate.Right);
    }

    @Test
    void toNEWSRotate() {
        assertThat(StringEnumTransform.toNEWSRotate('N')).isEqualTo(Rotate.Spawn);
        assertThat(StringEnumTransform.toNEWSRotate('E')).isEqualTo(Rotate.Right);
        assertThat(StringEnumTransform.toNEWSRotate('W')).isEqualTo(Rotate.Left);
        assertThat(StringEnumTransform.toNEWSRotate('S')).isEqualTo(Rotate.Reverse);
    }

    @Test
    void toString1() {
        assertThat(StringEnumTransform.toString(Rotate.Spawn)).isEqualTo("0");
        assertThat(StringEnumTransform.toString(Rotate.Reverse)).isEqualTo("2");
        assertThat(StringEnumTransform.toString(Rotate.Left)).isEqualTo("L");
        assertThat(StringEnumTransform.toString(Rotate.Right)).isEqualTo("R");
    }
}