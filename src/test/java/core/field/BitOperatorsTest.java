package core.field;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BitOperatorsTest {
    @Test
    void getColumnOneLineBelowY() {
        for (int y = 0; y <= 6; y++) {
            long mask = BitOperators.getColumnOneLineBelowY(y);

            // y行より下の行が含まれることを確認
            for (int line = 0; line < y; line++)
                assertThat(mask & 1L << (line * 10)).isNotEqualTo(0L);

            // y行を含めた上の行が含まれないことを確認
            for (int line = y; line <= 6; line++)
                assertThat(mask & 1L << (line * 10)).isEqualTo(0L);
        }
    }

    @Test
    void getColumnMaskRightX() {
        for (int x = 0; x <= 10; x++) {
            long mask = BitOperators.getColumnMaskRightX(x);

            // x列より左の列が含まれないことを確認
            for (int column = 0; column < x; column++)
                for (int y = 0; y < 6; y++)
                    assertThat(mask & 1L << (y * 10 + column)).isEqualTo(0L);

            // x列を含めた右の列が含まれることを確認
            for (int column = x; column < 10; column++)
                for (int y = 0; y < 6; y++)
                    assertThat(mask & 1L << (y * 10 + column)).isNotEqualTo(0L);
        }
    }

    @Test
    void getRowMaskBelowY() {
        for (int y = 0; y <= 6; y++) {
            long mask = BitOperators.getRowMaskBelowY(y);

            // y行を含めた下の行が含まれることを確認
            for (int line = 0; line < y; line++)
                for (int x = 0; x < 10; x++)
                    assertThat(mask & 1L << (line * 10 + x)).isNotEqualTo(0L);

            // y行より上の行が含まれないことを確認
            for (int line = y; line < 6; line++)
                for (int x = 0; x < 10; x++)
                    assertThat(mask & 1L << (line * 10 + x)).isEqualTo(0L);
        }
    }

    @Test
    void getRowMaskAboveY() {
        for (int y = 0; y <= 6; y++) {
            long mask = BitOperators.getRowMaskAboveY(y);

            // y行より下の行が含まれないことを確認
            for (int line = 0; line < y; line++)
                for (int x = 0; x < 10; x++)
                    assertThat(mask & 1L << (line * 10 + x)).isEqualTo(0L);

            // y行を含めた上の行が含まれることを確認
            for (int line = y; line < 6; line++)
                for (int x = 0; x < 10; x++)
                    assertThat(mask & 1L << (line * 10 + x)).isNotEqualTo(0L);
        }
    }

    @Test
    void bitToY() {
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 10; x++) {
                long bit = 1L << (y * 10 + x);
                int actualY = BitOperators.bitToY(bit);
                assertThat(actualY).isEqualTo(y);
            }
        }
    }
}