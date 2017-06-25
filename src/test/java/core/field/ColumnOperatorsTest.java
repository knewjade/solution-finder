package core.field;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ColumnOperatorsTest {
    @Test
    public void parseColumnToBoard6() throws Exception {
        for (int column = 0; column < 64; column++) {
            SmallField field = new SmallField();
            for (int y = 0; y < 6; y++) {
                int flag = 1 << y;
                if ((column & flag) != 0) {
                    field.setBlock(0, y);
                }
            }

            long board = ColumnOperators.parseToBoardWidth6(column);
            assertThat(board, is(field.getBoard(0)));
        }
    }

    @Test
    public void parseColumnToInvertedBoard6() throws Exception {
        for (int column = 0; column < 64; column++) {
            SmallField invertedField = new SmallField();
            for (int y = 0; y < 6; y++) {
                int flag = 1 << y;
                if ((column & flag) == 0) {
                    invertedField.setBlock(0, y);
                }
            }

            long inverted = ColumnOperators.parseToInvertedBoardWidth6(column);
            assertThat(inverted, is(invertedField.getBoard(0)));
        }
    }
}