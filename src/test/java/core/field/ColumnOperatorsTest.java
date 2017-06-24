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

            long board = ColumnOperators.parseToBoardWidth12(column);
            assertThat(board, is(field.getBoard(0)));
        }
    }

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

            long inverted = ColumnOperators.parseToInvertedBoardWidth12(column);
            assertThat(inverted, is(invertedField.getBoard(0)));
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