package core.field;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BlockCounterTest {
    @Test
    void countColumnBlocks() {
        Field field = FieldFactory.createField("" +
                "X_________" +
                "X_________" +
                "X_________" +
                "X_________" +
                "X_________" +
                "X_________"
        );

        for (int slide = 0; slide < 10; slide++) {
            long board = field.getBoard(0);
            long blockCounter = BlockCounter.countColumnBlocks(board << slide);
            long[] array = BlockCounter.parseColumnIndexToArray(blockCounter);
            for (int x = 0; x < 10; x++) {
                if (x == slide) {
                    assertThat(array[x]).isEqualTo(6);
                } else {
                    assertThat(array[x]).isEqualTo(0);
                }
            }
        }
    }

    @Test
    void countRowBlocks() {
        Field field = FieldFactory.createField("" +
                "XXXXXXXXXX"
        );

        for (int slide = 0; slide < 6; slide++) {
            long board = field.getBoard(0);
            long blockCounter = BlockCounter.countRowBlocks(board << slide * 10);
            long[] array = BlockCounter.parseRowIndexToArray(blockCounter);
            for (int y = 0; y < 6; y++) {
                if (y == slide) {
                    assertThat(array[y]).isEqualTo(10);
                } else {
                    assertThat(array[y]).isEqualTo(0);
                }
            }
        }
    }
}