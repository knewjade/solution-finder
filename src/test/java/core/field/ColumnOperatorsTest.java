package core.field;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnOperatorsTest {
    @Test
    void parseColumnToBoard6() throws Exception {
        for (int column = 0; column < 64; column++) {
            SmallField field = new SmallField();
            for (int y = 0; y < 6; y++) {
                int flag = 1 << y;
                if ((column & flag) != 0) {
                    field.setBlock(0, y);
                }
            }

            long board = ColumnOperators.parseToBoardWidth6(column);
            assertThat(board).isEqualTo(field.getBoard(0));
        }
    }

    @Test
    void parseColumnToInvertedBoard6() throws Exception {
        for (int column = 0; column < 64; column++) {
            SmallField invertedField = new SmallField();
            for (int y = 0; y < 6; y++) {
                int flag = 1 << y;
                if ((column & flag) == 0) {
                    invertedField.setBlock(0, y);
                }
            }

            long inverted = ColumnOperators.parseToInvertedBoardWidth6(column);
            assertThat(inverted).isEqualTo(invertedField.getBoard(0));
        }
    }

    @Test
    void parseColumn() throws Exception {
        for (int column = 0; column < 64; column++) {
            long board = ColumnOperators.parseToBoardWidth6(column);
            long inverted = ColumnOperators.parseToInvertedBoardWidth6(~column & 0b111111);
            assertThat(board).isEqualTo(inverted);
        }
    }
}