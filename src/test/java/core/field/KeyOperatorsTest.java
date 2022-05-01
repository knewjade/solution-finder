package core.field;

import lib.BooleanWalker;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KeyOperatorsTest {
    @Test
    void getDeleteKey() {
        Randoms randoms = new Randoms();
        BooleanWalker.walk(6)
                .forEach(booleans -> {
                    SmallField field = new SmallField();
                    long expectDeleteKey = 0L;
                    for (int y = 0; y < booleans.size(); y++) {
                        if (booleans.get(y)) {
                            // ラインを全て埋める
                            for (int x = 0; x < 10; x++)
                                field.setBlock(x, y);
                            expectDeleteKey += KeyOperators.getDeleteBitKey(y);
                        } else {
                            // ラインを全て埋めない
                            for (int x = 0; x < 10; x++)
                                if (randoms.nextBoolean(0.8))
                                    field.setBlock(x, y);
                            field.removeBlock(randoms.nextIntOpen(0, 10), y);
                        }
                    }

                    long board = field.getXBoard();
                    long deleteKey = KeyOperators.getDeleteKey(board);
                    assertThat(deleteKey).isEqualTo(expectDeleteKey);
                });
    }

    @Test
    void getMaskForKeyBelowY() {
        for (int y = 0; y <= 24; y++) {
            long mask = KeyOperators.getMaskForKeyBelowY(y);

            // y行より下の行が含まれることを確認
            for (int line = 0; line < y; line++)
                assertThat(mask & 1L << ((line % 6) * 10) + (line / 6)).isNotEqualTo(0L);

            // y行を含めた上の行が含まれないことを確認
            for (int line = y; line < 24; line++)
                assertThat(mask & 1L << ((line % 6) * 10) + (line / 6)).isEqualTo(0L);
        }
    }

    @Test
    void getMaskForKeyAboveY() {
        for (int y = 0; y <= 24; y++) {
            long mask = KeyOperators.getMaskForKeyAboveY(y);

            // y行より下の行が含まれないことを確認
            for (int line = 0; line < y; line++)
                assertThat(mask & 1L << ((line % 6) * 10) + (line / 6)).isEqualTo(0L);

            // y行を含めた上の行が含まれることを確認
            for (int line = y; line < 24; line++)
                assertThat(mask & 1L << ((line % 6) * 10) + (line / 6)).isNotEqualTo(0L);
        }
    }

    @Test
    void getDeleteBitKey() {
        for (int y = 0; y < 24; y++) {
            long mask = KeyOperators.getDeleteBitKey(y);
            assertThat(mask).isEqualTo(1L << ((y % 6) * 10) + (y / 6));
        }
    }

    @Test
    void mirrorSampleCase() {
        long mirror = KeyOperators.mirror(0b001111100011111000001101010001L);
        assertThat(mirror).isEqualTo(0b000111110000000111111000101011L);
    }

    @Test
    void bitToYFromKey() {
        for (int y = 0; y < 24; y++) {
            long key = KeyOperators.getBitKey(y);
            assertThat(KeyOperators.bitToYFromKey(key)).isEqualTo(y);
        }
    }

    @Test
    void extractLowerBit() {
        Randoms randoms = new Randoms();
        for (int y = 0; y < 24; y++) {
            long key = KeyOperators.getBitKey(y);

            long current = key;
            for (int dy = y + 1; dy < 24; dy++) {
                if (randoms.nextBoolean()) {
                    current |= KeyOperators.getBitKey(dy);
                }
            }

            assertThat(KeyOperators.extractLowerBit(current)).isEqualTo(key);
        }
    }

    @Test
    void getColumnKey() {
        assertThat(KeyOperators.getColumnKey(0)).isEqualTo(0b1);
        assertThat(KeyOperators.getColumnKey(1)).isEqualTo(0b10);
        assertThat(KeyOperators.getColumnKey(2)).isEqualTo(0b100);
        assertThat(KeyOperators.getColumnKey(10)).isEqualTo(0b10000000000);
    }

    @Test
    void toColumnKey() {
        for (int y = 0; y < 24; y++) {
            long bitKey = KeyOperators.getBitKey(y);
            assertThat(KeyOperators.toColumnKey(bitKey)).isEqualTo(KeyOperators.getColumnKey(y));
        }
    }

    @Test
    void toBitKey() {
        for (int y = 0; y < 24; y++) {
            long columnKey = KeyOperators.getColumnKey(y);
            assertThat(KeyOperators.toBitKey(columnKey)).isEqualTo(KeyOperators.getBitKeys(y));
        }
    }
}