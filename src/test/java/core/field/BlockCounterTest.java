package core.field;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BlockCounterTest {
    @Test
    void countBlocks() {
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
            long blockCounter = BlockCounter.countBlocks(board << slide);
            long[] array = BlockCounter.parseToArray(blockCounter);
            for (int x = 0; x < 10; x++) {
                if (x == slide) {
                    assertThat(array[x]).isEqualTo(6);
                } else {
                    assertThat(array[x]).isEqualTo(0);
                }
            }
        }
    }
}