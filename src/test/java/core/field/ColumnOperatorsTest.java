package core.field;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ColumnOperatorsTest {
    @Test
    public void parseColumnToBoard() throws Exception {
        for (int column = 0; column < 4096; column++) {
            SmallField field = new SmallField();
            for (int index = 0; index < 12; index++) {
                int flag = 1 << index;
                if ((column & flag) != 0) {
                    int x = index / 6;
                    int y = index % 6;
                    field.setBlock(x, y);
                }
            }

            long board = ColumnOperators.parseColumnToBoard(column);
            assertThat(board, is(field.getBoard(0)));
        }
    }

    @Test
    public void parseColumnToInvertedBoard() throws Exception {
        for (int column = 0; column < 4096; column++) {
            SmallField invertedField = new SmallField();
            for (int index = 0; index < 12; index++) {
                int flag = 1 << index;
                if ((column & flag) == 0) {
                    int x = index / 6;
                    int y = index % 6;
                    invertedField.setBlock(x, y);
                }
            }

            long inverted = ColumnOperators.parseColumnToInvertedBoard(column);
            assertThat(inverted, is(invertedField.getBoard(0)));
        }
    }
}