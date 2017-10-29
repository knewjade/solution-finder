package common.parser;

import core.mino.Piece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class StringEnumTransformTest {
    @Test
    void toBlockString() {
        assertThat(StringEnumTransform.toBlock("T")).isEqualTo(Piece.T);
        assertThat(StringEnumTransform.toBlock("J")).isEqualTo(Piece.J);
        assertThat(StringEnumTransform.toBlock("L")).isEqualTo(Piece.L);
        assertThat(StringEnumTransform.toBlock("S")).isEqualTo(Piece.S);
        assertThat(StringEnumTransform.toBlock("Z")).isEqualTo(Piece.Z);
        assertThat(StringEnumTransform.toBlock("O")).isEqualTo(Piece.O);
        assertThat(StringEnumTransform.toBlock("I")).isEqualTo(Piece.I);
    }

    @Test
    void toBlockChar() {
        assertThat(StringEnumTransform.toBlock('T')).isEqualTo(Piece.T);
        assertThat(StringEnumTransform.toBlock('J')).isEqualTo(Piece.J);
        assertThat(StringEnumTransform.toBlock('L')).isEqualTo(Piece.L);
        assertThat(StringEnumTransform.toBlock('S')).isEqualTo(Piece.S);
        assertThat(StringEnumTransform.toBlock('Z')).isEqualTo(Piece.Z);
        assertThat(StringEnumTransform.toBlock('O')).isEqualTo(Piece.O);
        assertThat(StringEnumTransform.toBlock('I')).isEqualTo(Piece.I);
    }

    @Test
    void toRotate() {
        assertThat(StringEnumTransform.toRotate("0")).isEqualTo(Rotate.Spawn);
        assertThat(StringEnumTransform.toRotate("2")).isEqualTo(Rotate.Reverse);
        assertThat(StringEnumTransform.toRotate("L")).isEqualTo(Rotate.Left);
        assertThat(StringEnumTransform.toRotate("R")).isEqualTo(Rotate.Right);
    }

    @Test
    void toString1() {
        assertThat(StringEnumTransform.toString(Rotate.Spawn)).isEqualTo("0");
        assertThat(StringEnumTransform.toString(Rotate.Reverse)).isEqualTo("2");
        assertThat(StringEnumTransform.toString(Rotate.Left)).isEqualTo("L");
        assertThat(StringEnumTransform.toString(Rotate.Right)).isEqualTo("R");
    }
}