package core.field;

import core.mino.Mino;
import core.mino.Piece;
import core.neighbor.OriginalPiece;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class LargeFieldTest {
    private static final int FIELD_WIDTH = 10;
    private static final int FIELD_HEIGHT = 24;

    private ArrayList<OriginalPiece> createAllPieces(int fieldHeight) {
        ArrayList<OriginalPiece> pieces = new ArrayList<>();
        for (Piece piece : Piece.values()) {
            for (Rotate rotate : Rotate.values()) {
                Mino mino = new Mino(piece, rotate);
                for (int y = -mino.getMinY(); y < fieldHeight - mino.getMaxY(); y++) {
                    for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                        pieces.add(new OriginalPiece(mino, x, y, fieldHeight));
                    }
                }
            }
        }
        return pieces;
    }

    @Test
    void getMaxFieldHeight() throws Exception {
        Field field = FieldFactory.createLargeField();
        assertThat(field.getMaxFieldHeight()).isEqualTo(FIELD_HEIGHT);
    }

    @Test
    void block() {
        LargeField field = FieldFactory.createLargeField();

        for (int index = 0; index < FIELD_HEIGHT; index++)
            field.setBlock(index % FIELD_WIDTH, index);

        assertThat(field.getNumOfAllBlocks()).isEqualTo(FIELD_HEIGHT);

        for (int y = 0; y < FIELD_HEIGHT; y++)
            for (int x = 0; x < FIELD_WIDTH; x++)
                assertThat(field.isEmpty(x, y)).isEqualTo(x != y % FIELD_WIDTH);

        for (int index = 0; index < FIELD_HEIGHT; index++)
            field.removeBlock(index % FIELD_WIDTH, index);

        for (int y = 0; y < FIELD_HEIGHT; y++)
            for (int x = 0; x < FIELD_WIDTH; x++)
                assertThat(field.isEmpty(x, y)).isTrue();
    }

    @Test
    void put() throws Exception {
        for (int y = 1; y < FIELD_HEIGHT - 2; y++) {
            for (int x = 0; x < FIELD_WIDTH - 2; x++) {
                Field field = FieldFactory.createLargeField();

                field.put(new Mino(Piece.T, Rotate.Right), x, y);
                assertThat(field.isEmpty(x, y)).isFalse();
                assertThat(field.isEmpty(x, y - 1)).isFalse();
                assertThat(field.isEmpty(x, y + 1)).isFalse();
                assertThat(field.isEmpty(x + 1, y)).isFalse();
            }
        }
    }

    @Test
    void put2() throws Exception {
        ArrayList<OriginalPiece> pieces = createAllPieces(FIELD_HEIGHT);

        for (OriginalPiece piece : pieces) {
            LargeField field1 = FieldFactory.createLargeField();
            assertThat(field1.canPut(piece.getMino(), piece.getX(), piece.getY())).isTrue();
            field1.put(piece.getMino(), piece.getX(), piece.getY());
            assertThat(field1.canPut(piece.getMino(), piece.getX(), piece.getY())).isFalse();

            LargeField field2 = FieldFactory.createLargeField();
            assertThat(field2.canPut(piece)).isTrue();
            field2.put(piece);
            assertThat(field2.canPut(piece)).isFalse();

            assertThat(field1.getXBoardLow()).isEqualTo(field2.getXBoardLow());
            assertThat(field1.getXBoardMidLow()).isEqualTo(field2.getXBoardMidLow());
            assertThat(field1.getXBoardMidHigh()).isEqualTo(field2.getXBoardMidHigh());
            assertThat(field1.getXBoardHigh()).isEqualTo(field2.getXBoardHigh());

            assertThat(field1.isPerfect()).isFalse();
        }
    }

    @Test
    void remove() throws Exception {
        for (int y = 1; y < FIELD_HEIGHT - 2; y++) {
            for (int x = 0; x < FIELD_WIDTH - 2; x++) {
                Field field = FieldFactory.createLargeField();
                field.inverse();

                field.remove(new Mino(Piece.T, Rotate.Right), x, y);
                assertThat(field.isEmpty(x, y)).isTrue();
                assertThat(field.isEmpty(x, y - 1)).isTrue();
                assertThat(field.isEmpty(x, y + 1)).isTrue();
                assertThat(field.isEmpty(x + 1, y)).isTrue();
            }
        }
    }

    @Test
    void remove2() throws Exception {
        ArrayList<OriginalPiece> pieces = createAllPieces(FIELD_HEIGHT);

        for (OriginalPiece piece : pieces) {
            LargeField field1 = FieldFactory.createLargeField();
            field1.inverse();
            field1.remove(piece.getMino(), piece.getX(), piece.getY());

            LargeField field2 = FieldFactory.createLargeField();
            field2.inverse();
            field2.remove(piece);

            assertThat(field1.getXBoardLow()).isEqualTo(field2.getXBoardLow());
            assertThat(field1.getXBoardMidLow()).isEqualTo(field2.getXBoardMidLow());
            assertThat(field1.getXBoardMidHigh()).isEqualTo(field2.getXBoardMidHigh());
            assertThat(field1.getXBoardHigh()).isEqualTo(field2.getXBoardHigh());
        }
    }

    @Test
    void getYOnHarddrop() throws Exception {
        String marks = "" +
                "X_________" +
                "__________" +
                "__________" +
                "__________" +
                "_________X" +
                "____X_____" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "";
        Field field = FieldFactory.createLargeField(marks);

        assertThat(field.getYOnHarddrop(new Mino(Piece.T, Rotate.Spawn), 1, FIELD_HEIGHT)).isEqualTo(12);
        assertThat(field.getYOnHarddrop(new Mino(Piece.T, Rotate.Spawn), 2, FIELD_HEIGHT)).isEqualTo(0);
        assertThat(field.getYOnHarddrop(new Mino(Piece.T, Rotate.Spawn), 3, FIELD_HEIGHT)).isEqualTo(7);
        assertThat(field.getYOnHarddrop(new Mino(Piece.T, Rotate.Spawn), 8, FIELD_HEIGHT)).isEqualTo(8);
    }

    @Test
    void canReachOnHarddrop() throws Exception {
        String marks = "" +
                "X_________" +
                "__________" +
                "__________" +
                "__________" +
                "_________X" +
                "____X_____" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "";
        Field field = FieldFactory.createLargeField(marks);

        assertThat(field.canReachOnHarddrop(new Mino(Piece.T, Rotate.Spawn), 1, 4)).isFalse();
        assertThat(field.canReachOnHarddrop(new Mino(Piece.T, Rotate.Spawn), 2, 4)).isTrue();
        assertThat(field.canReachOnHarddrop(new Mino(Piece.T, Rotate.Spawn), 2, 3)).isTrue();
        assertThat(field.canReachOnHarddrop(new Mino(Piece.T, Rotate.Spawn), 1, 1)).isFalse();
    }

    @Test
    void canReachOnHarddrop2() throws Exception {
        Randoms randoms = new Randoms();
        LargeField field = createRandomLargeField(randoms);
        String string = FieldView.toString(field);

        ArrayList<OriginalPiece> pieces = createAllPieces(field.getMaxFieldHeight());
        for (OriginalPiece piece : pieces) {
            Mino mino = piece.getMino();
            int x = piece.getX();
            int y = piece.getY();

            assertThat(field.canReachOnHarddrop(piece))
                    .as(string + piece.toString())
                    .isEqualTo(field.canPut(mino, x, y) && field.canReachOnHarddrop(mino, x, y));
        }
    }

    private LargeField createRandomLargeField(Randoms randoms) {
        Field randomField = randoms.field(24, 50);
        return new LargeField(randomField.getBoard(0), randomField.getBoard(1), randomField.getBoard(2), randomField.getBoard(3));
    }

    @Test
    void existAbove() throws Exception {
        for (int y = 0; y < FIELD_HEIGHT; y++) {
            Field field = FieldFactory.createLargeField();
            field.setBlock(0, y);
            for (int y2 = 0; y2 < FIELD_HEIGHT; y2++) {
                assertThat(field.existsAbove(y2)).isEqualTo(y2 <= y);
            }
        }
    }

    @Test
    void isPerfect() throws Exception {
        Field field = FieldFactory.createLargeField();
        assertThat(field.isPerfect()).isTrue();
    }

    @Test
    void isFilledInColumn() throws Exception {
        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 1; x < FIELD_WIDTH; x++) {
                Field field = FieldFactory.createLargeField();
                for (int i = 0; i < y; i++)
                    field.setBlock(x, i);

                for (int i = 0; i < FIELD_HEIGHT; i++)
                    assertThat(field.isFilledInColumn(x, i)).isEqualTo(i <= y);
            }
        }
    }

    @Test
    void isWallBetweenLeft() throws Exception {
        Randoms randoms = new Randoms();
        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 1; x < FIELD_WIDTH; x++) {
                Field field = FieldFactory.createLargeField();
                for (int i = 0; i < y; i++) {
                    if (randoms.nextBoolean())
                        field.setBlock(x, i);
                    else
                        field.setBlock(x - 1, i);
                }

                for (int i = 0; i < FIELD_HEIGHT; i++)
                    assertThat(field.isWallBetweenLeft(x, i)).isEqualTo(i <= y);
            }
        }
    }

    @Test
    void isOnGround() {
        {
            Field field = FieldFactory.createLargeField();
            assertThat(field.isOnGround(new Mino(Piece.I, Rotate.Spawn), 3, 0)).isTrue();
            boolean onGround = field.isOnGround(new Mino(Piece.I, Rotate.Spawn), 3, 1);
            assertThat(onGround).isFalse();
        }

        for (int y = 2; y < FIELD_HEIGHT; y++) {
            Field field = FieldFactory.createLargeField();
            field.setBlock(4, y - 2);

            assertThat(field.isOnGround(new Mino(Piece.I, Rotate.Spawn), 4, y)).isFalse();
            assertThat(field.isOnGround(new Mino(Piece.I, Rotate.Spawn), 4, y-1)).isTrue();
        }
    }

    @Test
    void getBlockCountBelowOnX() {
        Randoms randoms = new Randoms();
        Field field = randoms.field(FIELD_HEIGHT, 25);

        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_WIDTH; x++) {
                int expected = 0;
                for (int y2 = 0; y2 < y; y2++)
                    expected += field.isEmpty(x, y2) ? 0 : 1;

                assertThat(field.getBlockCountBelowOnX(x, y)).isEqualTo(expected);
            }
        }
    }
}