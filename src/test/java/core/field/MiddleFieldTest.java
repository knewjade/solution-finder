package core.field;

import core.mino.Block;
import core.mino.Mino;
import core.mino.piece.Piece;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class MiddleFieldTest {
    private static final int FIELD_WIDTH = 10;

    private ArrayList<Piece> createAllPieces(int fieldHeight) {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (Block block : Block.values()) {
            for (Rotate rotate : Rotate.values()) {
                Mino mino = new Mino(block, rotate);
                for (int y = -mino.getMinY(); y < fieldHeight - mino.getMaxY(); y++) {
                    for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                        pieces.add(new Piece(mino, x, y, fieldHeight));
                    }
                }
            }
        }
        return pieces;
    }

    @Test
    void testGetMaxFieldHeight() throws Exception {
        Field field = FieldFactory.createMiddleField();
        assertThat(field.getMaxFieldHeight()).isEqualTo(12);
    }

    @Test
    void testPutAndRemoveBlock() throws Exception {
        Field field = FieldFactory.createSmallField();
        assertThat(field.isEmpty(0, 0)).isTrue();
        field.setBlock(0, 0);
        assertThat(field.isEmpty(0, 0)).isFalse();
        field.removeBlock(0, 0);
        assertThat(field.isEmpty(0, 0)).isTrue();

        assertThat(field.isEmpty(9, 9)).isTrue();
        field.setBlock(9, 9);
        assertThat(field.isEmpty(9, 9)).isFalse();
        field.removeBlock(9, 9);
        assertThat(field.isEmpty(9, 9)).isTrue();
    }

    @Test
    void testPutAndRemoveMino() throws Exception {
        Field field = FieldFactory.createMiddleField();

        field.put(new Mino(Block.T, Rotate.Spawn), 1, 0);
        assertThat(field.isEmpty(0, 0)).isFalse();
        assertThat(field.isEmpty(1, 0)).isFalse();
        assertThat(field.isEmpty(2, 0)).isFalse();
        assertThat(field.isEmpty(1, 1)).isFalse();

        field.put(new Mino(Block.I, Rotate.Left), 4, 6);
        assertThat(field.isEmpty(4, 5)).isFalse();
        assertThat(field.isEmpty(4, 6)).isFalse();
        assertThat(field.isEmpty(4, 7)).isFalse();
        assertThat(field.isEmpty(4, 8)).isFalse();

        field.put(new Mino(Block.O, Rotate.Spawn), 8, 8);
        assertThat(field.isEmpty(8, 8)).isFalse();
        assertThat(field.isEmpty(8, 9)).isFalse();
        assertThat(field.isEmpty(9, 8)).isFalse();
        assertThat(field.isEmpty(9, 9)).isFalse();

        field.remove(new Mino(Block.T, Rotate.Spawn), 1, 0);
        field.remove(new Mino(Block.I, Rotate.Left), 4, 6);
        field.remove(new Mino(Block.O, Rotate.Spawn), 8, 8);
        assertThat(field.isPerfect()).isTrue();
    }

    @Test
    void testPutAndRemovePiece() throws Exception {
        MiddleField field = FieldFactory.createMiddleField();
        int maxFieldHeight = field.getMaxFieldHeight();

        ArrayList<Piece> pieces = createAllPieces(maxFieldHeight);
        for (Piece piece : pieces) {
            // Initialize
            Mino mino = piece.getMino();
            int x = piece.getX();
            int y = piece.getY();

            // Expect
            MiddleField expected = FieldFactory.createMiddleField();
            expected.put(mino, x, y);

            // Test
            field.put(piece);

            assertThat(field)
                    .as("%s (%d, %d)", mino, x, y)
                    .isEqualTo(expected);

            field.remove(piece);

            assertThat(field.isPerfect())
                    .as("%s (%d, %d)", mino, x, y)
                    .isTrue();
        }
    }


    @Test
    void testGetYOnHarddrop() throws Exception {
        String marks = "" +
                "X_________" +
                "__________" +
                "__________" +
                "__________" +
                "_________X" +
                "____X_____" +
                "";
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 10)).isEqualTo(6);
        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 2, 10)).isEqualTo(0);
        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 3, 10)).isEqualTo(1);
        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 8, 10)).isEqualTo(2);
    }

    @Test
    void testCanReachOnHarddrop() throws Exception {
        String marks = "" +
                "X_________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "_________X" +
                "____X_____" +
                "";
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 4)).isFalse();
        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 2, 4)).isTrue();
        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 2, 3)).isTrue();
        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 1)).isFalse();
    }

    @Test
    void testCanReachOnHarddrop2() throws Exception {
        Randoms randoms = new Randoms();
        MiddleField field = createRandomMiddleField(randoms);
        String string = FieldView.toString(field);

        ArrayList<Piece> pieces = createAllPieces(field.getMaxFieldHeight());
        for (Piece piece : pieces) {
            Mino mino = piece.getMino();
            int x = piece.getX();
            int y = piece.getY();

            assertThat(field.canReachOnHarddrop(piece))
                    .as(string + piece.toString())
                    .isEqualTo(field.canPut(mino, x, y) && field.canReachOnHarddrop(mino, x, y));
        }
    }

    @Test
    void testExistAbove() throws Exception {
        String marks = "" +
                "_____X____" +
                "__________" +
                "__________" +
                "__________" +
                "___X______" +
                "__________" +
                "_______X__" +
                "__________" +
                "";
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.existsAbove(0)).isTrue();
        assertThat(field.existsAbove(6)).isTrue();
        assertThat(field.existsAbove(7)).isTrue();
        assertThat(field.existsAbove(8)).isFalse();
        assertThat(field.existsAbove(9)).isFalse();
    }

    @Test
    void testIsPerfect() throws Exception {
        Field field = FieldFactory.createMiddleField();

        assertThat(field.isEmpty(0, 0)).isTrue();
        assertThat(field.isPerfect()).isTrue();

        field.setBlock(7, 8);

        assertThat(field.isEmpty(7, 8)).isFalse();
        assertThat(field.isPerfect()).isFalse();
    }

    @Test
    void testIsFilledInColumn() throws Exception {
        String marks = "" +
                "____X_____" +
                "____X_____" +
                "____X_____" +
                "____X_____" +
                "____X_____" +
                "___XX_____" +
                "__XXX_____" +
                "X__XX_____" +
                "__XXXX____" +
                "";
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.isFilledInColumn(0, 4)).isFalse();
        assertThat(field.isFilledInColumn(1, 4)).isFalse();
        assertThat(field.isFilledInColumn(2, 4)).isFalse();
        assertThat(field.isFilledInColumn(3, 4)).isTrue();
        assertThat(field.isFilledInColumn(3, 6)).isFalse();
        assertThat(field.isFilledInColumn(4, 4)).isTrue();
        assertThat(field.isFilledInColumn(4, 6)).isTrue();
        assertThat(field.isFilledInColumn(4, 9)).isTrue();
        assertThat(field.isFilledInColumn(4, 10)).isFalse();
        assertThat(field.isFilledInColumn(5, 7)).isFalse();
    }

    @Test
    void testIsWallBetweenLeft() throws Exception {
        String marks = "" +
                "____X_____" +
                "____X_____" +
                "____X_____" +
                "____X_____" +
                "____X_____" +
                "_X_XX_____" +
                "_XXXX_____" +
                "X_XXX_____" +
                "_XXXXX____" +
                "";
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.isWallBetweenLeft(1, 4)).isTrue();
        assertThat(field.isWallBetweenLeft(1, 5)).isFalse();
        assertThat(field.isWallBetweenLeft(2, 4)).isTrue();
        assertThat(field.isWallBetweenLeft(2, 5)).isFalse();
        assertThat(field.isWallBetweenLeft(3, 4)).isTrue();
        assertThat(field.isWallBetweenLeft(3, 5)).isFalse();
        assertThat(field.isWallBetweenLeft(4, 9)).isTrue();
        assertThat(field.isWallBetweenLeft(4, 10)).isFalse();
        assertThat(field.isWallBetweenLeft(5, 9)).isTrue();
        assertThat(field.isWallBetweenLeft(5, 10)).isFalse();
        assertThat(field.isWallBetweenLeft(6, 6)).isFalse();
    }

    @Test
    void testCanPutMino() throws Exception {
        String marks = "" +
                "______X___" +
                "___X______" +
                "___XX_____" +
                "___XX_____" +
                "___XX_____" +
                "__X_X_____" +
                "X___X_____" +
                "__X_XX____" +
                "";
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.canPut(new Mino(Block.T, Rotate.Spawn), 4, 7)).isTrue();
        assertThat(field.canPut(new Mino(Block.T, Rotate.Spawn), 5, 6)).isTrue();
        assertThat(field.canPut(new Mino(Block.T, Rotate.Right), 1, 1)).isTrue();
        assertThat(field.canPut(new Mino(Block.T, Rotate.Reverse), 1, 3)).isTrue();
        assertThat(field.canPut(new Mino(Block.T, Rotate.Left), 3, 1)).isTrue();

        assertThat(field.canPut(new Mino(Block.T, Rotate.Spawn), 5, 7)).isFalse();
        assertThat(field.canPut(new Mino(Block.T, Rotate.Spawn), 4, 6)).isFalse();
        assertThat(field.canPut(new Mino(Block.T, Rotate.Right), 0, 1)).isFalse();
        assertThat(field.canPut(new Mino(Block.T, Rotate.Reverse), 1, 1)).isFalse();
        assertThat(field.canPut(new Mino(Block.T, Rotate.Left), 1, 1)).isFalse();
    }

    @Test
    void testCanPutMino2() throws Exception {
        String marks = "" +
                "XXXXXXXX_X" +
                "XXXXXXXX_X" +
                "XXXXXXXX_X" +
                "XXXXXXXX_X" +
                "XXXXXXXX_X" +
                "XXXXXXXX_X" +
                "XXXXXXXX_X" +
                "XXXXXXXX_X" +
                "XXXXXXXX_X" +
                "XXXXXXXX_X" +
                "XXXXXXXX_X" +
                "XXXXXXXX_X";
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.canPut(new Mino(Block.I, Rotate.Left), 8, 1)).isTrue();
        assertThat(field.canPut(new Mino(Block.I, Rotate.Left), 8, 11)).isTrue();
        assertThat(field.canPut(new Mino(Block.I, Rotate.Left), 8, 12)).isTrue();
        assertThat(field.canPut(new Mino(Block.I, Rotate.Left), 8, 13)).isTrue();
        assertThat(field.canPut(new Mino(Block.I, Rotate.Left), 8, 14)).isTrue();
    }

    @Test
    void testCanPutPiece() {
        Randoms randoms = new Randoms();
        MiddleField field = createRandomMiddleField(randoms);
        int maxFieldHeight = field.getMaxFieldHeight();

        ArrayList<Piece> pieces = createAllPieces(maxFieldHeight);
        for (Piece piece : pieces) {
            Mino mino = piece.getMino();
            int x = piece.getX();
            int y = piece.getY();

            assertThat(field.canPut(piece))
                    .as("%s (%d, %d)", mino, x, y)
                    .isEqualTo(field.canPut(mino, x, y));
        }
    }

    private MiddleField createRandomMiddleField(Randoms randoms) {
        Field randomField = randoms.field(12, 25);
        return new MiddleField(randomField.getBoard(0), randomField.getBoard(1));
    }

    @Test
    void testIsOnGround() throws Exception {
        String marks = "" +
                "X_________" +
                "___X______" +
                "___XX_____" +
                "___XX_____" +
                "__X_X_____" +
                "X___X_____" +
                "__X_XX____" +
                "";
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Spawn), 1, 7)).isTrue();
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Spawn), 5, 5)).isTrue();
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Right), 8, 1)).isTrue();
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Reverse), 1, 3)).isTrue();
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Left), 1, 2)).isTrue();

        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Spawn), 1, 6)).isFalse();
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Spawn), 6, 5)).isFalse();
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Spawn), 8, 1)).isFalse();
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Right), 8, 2)).isFalse();
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Reverse), 7, 3)).isFalse();
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Left), 9, 2)).isFalse();
    }

    @Test
    void testGetBlockCountBelowOnX() throws Exception {
        String marks = "" +
                "___XX_____" +
                "___XX_____" +
                "___XX_____" +
                "____X_____" +
                "___XX_____" +
                "___XX_____" +
                "___XX_____" +
                "__X_X_____" +
                "X___X_____" +
                "__X_XX____" +
                "";
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.getBlockCountBelowOnX(0, 1)).isEqualTo(0);
        assertThat(field.getBlockCountBelowOnX(0, 2)).isEqualTo(1);
        assertThat(field.getBlockCountBelowOnX(2, 4)).isEqualTo(2);
        assertThat(field.getBlockCountBelowOnX(3, 4)).isEqualTo(1);
        assertThat(field.getBlockCountBelowOnX(3, 6)).isEqualTo(3);
        assertThat(field.getBlockCountBelowOnX(3, 9)).isEqualTo(5);
        assertThat(field.getBlockCountBelowOnX(3, 10)).isEqualTo(6);
        assertThat(field.getBlockCountBelowOnX(4, 6)).isEqualTo(6);
        assertThat(field.getBlockCountBelowOnX(4, 9)).isEqualTo(9);
        assertThat(field.getBlockCountBelowOnX(4, 10)).isEqualTo(10);
    }

    @Test
    void testGetAllBlockCount() throws Exception {
        String marks = "" +
                "___XX_____" +
                "___XX_____" +
                "___XX_____" +
                "___XX_____" +
                "___XX_____" +
                "__X_X_____" +
                "X___X_____" +
                "__X_XX____" +
                "";
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.getNumOfAllBlocks()).isEqualTo(17);
    }

    @Test
    void testClearLine1() throws Exception {
        String marks = "" +
                "XXXXXXXX_X" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXX_XXXXXX" +
                "XXXXXXXXXX" +
                "X_XXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXX_XXXXX" +
                "XXXXXXXXXX";
        Field field = FieldFactory.createMiddleField(marks);

        int deleteLine = field.clearLine();
        assertThat(deleteLine).isEqualTo(6);

        assertThat(field.existsAbove(3)).isTrue();
        assertThat(field.existsAbove(4)).isFalse();

        assertThat(field.isEmpty(0, 0)).isFalse();
        assertThat(field.isEmpty(4, 0)).isTrue();
        assertThat(field.isEmpty(1, 1)).isTrue();
        assertThat(field.isEmpty(3, 2)).isTrue();
        assertThat(field.isEmpty(8, 3)).isTrue();
    }

    @Test
    void testClearLine2() throws Exception {
        String marks = "" +
                "XXXXXXXXXX" +
                "XXXXXXXX_X" +
                "XXXXXXX_XX" +
                "XXXXXX_XXX" +
                "XXXXXXXXXX" +
                "XXXXX_XXXX" +
                "XXXX_XXXXX" +
                "XXX_XXXXXX" +
                "XX_XXXXXXX" +
                "XXXXXXXXXX" +
                "X_XXXXXXXX" +
                "_XXXXXXXXX";
        Field field = FieldFactory.createMiddleField(marks);

        int deleteLine = field.clearLine();
        assertThat(deleteLine).isEqualTo(3);

        assertThat(field.existsAbove(8)).isTrue();
        assertThat(field.existsAbove(9)).isFalse();

        for (int index = 0; index < 9; index++)
            assertThat(field.isEmpty(index, index)).isTrue();
    }

    @Test
    void testClearLine3() throws Exception {
        String marks = "" +
                "XXXXXXXXXX" +
                "XXXXX_XXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXX_XXXXX" +
                "XXX_XXXXXX" +
                "XX_XXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "X_XXXXXXXX" +
                "_XXXXXXXXX";
        Field field = FieldFactory.createMiddleField(marks);

        int deleteLine = field.clearLine();
        assertThat(deleteLine).isEqualTo(6);

        assertThat(field.existsAbove(5)).isTrue();
        assertThat(field.existsAbove(6)).isFalse();

        for (int index = 0; index < 6; index++)
            assertThat(field.isEmpty(index, index)).isTrue();
    }

    @Test
    void testClearLineAndInsertBlackLine() throws Exception {
        String marks = "" +
                "XXXXXXXX_X" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXX_XXXXXX" +
                "XXXXXXXXXX" +
                "X_XXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXX_XXXXX" +
                "XXXXXXXXXX";
        Field field = FieldFactory.createMiddleField(marks);
        Field freeze = field.freeze(field.getMaxFieldHeight());

        long deleteKey = field.clearLineReturnKey();
        assertThat(Long.bitCount(deleteKey)).isEqualTo(6);
        field.insertBlackLineWithKey(deleteKey);

        for (int index = 0; index < freeze.getBoardCount(); index++)
            assertThat(field.getBoard(index)).isEqualTo(freeze.getBoard(index));
    }

    @Test
    void testClearLineAndInsertWhiteLine() throws Exception {
        String marks = "" +
                "XXXXXXXX_X" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXX_XXXXXX" +
                "XXXXXXXXXX" +
                "X_XXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXX_XXXXX" +
                "XXXXXXXXXX";
        Field field = FieldFactory.createMiddleField(marks);

        String expectedMarks = "" +
                "XXXXXXXX_X" +
                "__________" +
                "__________" +
                "__________" +
                "XXX_XXXXXX" +
                "__________" +
                "X_XXXXXXXX" +
                "__________" +
                "XXXX_XXXXX" +
                "__________";
        Field expected = FieldFactory.createMiddleField(expectedMarks);

        long deleteKey = field.clearLineReturnKey();
        assertThat(Long.bitCount(deleteKey)).isEqualTo(6);
        field.insertWhiteLineWithKey(deleteKey);

        for (int index = 0; index < expected.getBoardCount(); index++)
            assertThat(field.getBoard(index)).isEqualTo(expected.getBoard(index));
    }

    @Test
    void testGetBoard() throws Exception {
        String marks = "" +
                "_________X" +
                "_________X" +
                "_________X" +
                "X_________" +
                "X_________" +
                "X_________" +
                "X_________" +
                "X_________" +
                "X_________" +
                "";
        MiddleField field = FieldFactory.createMiddleField(marks);

        assertThat(field.getBoardCount()).isEqualTo(2);
        assertThat(field.getBoard(0))
                .isEqualTo(0x4010040100401L)
                .isEqualTo(field.getXBoardLow());
        assertThat(field.getBoard(1))
                .isEqualTo(0x20080200L)
                .isEqualTo(field.getXBoardHigh());

        for (int index = 2; index < 100; index++)
            assertThat(field.getBoard(index)).isEqualTo(0L);
    }

    @Test
    void testFreeze() throws Exception {
        String marks = "" +
                "X_________" +
                "X_________" +
                "X_________" +
                "X_________" +
                "";
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.getNumOfAllBlocks()).isEqualTo(4);
        Field freeze = field.freeze(field.getMaxFieldHeight());
        field.setBlock(9, 0);

        assertThat(field.getNumOfAllBlocks()).isEqualTo(5);
        assertThat(freeze.getNumOfAllBlocks()).isEqualTo(4);
    }

    @Test
    void testEqual() throws Exception {
        String marks = "XXXXXX____";
        MiddleField field1 = FieldFactory.createMiddleField(marks);
        MiddleField field2 = FieldFactory.createMiddleField(marks);
        assertThat(field1.equals(field2)).isTrue();

        MiddleField field3 = FieldFactory.createMiddleField(marks + "XXXXXX____");
        assertThat(field1.equals(field3)).isFalse();

        SmallField field4 = FieldFactory.createSmallField(marks);
        assertThat(field1.equals(field4)).isTrue();
    }

    @Test
    void testGetBlockCountOnY() {
        String marks = "" +
                "X__X__X___" +
                "XXXXXXXXXX" +
                "XXX_XXX__X" +
                "__________" +
                "X___XXX__X" +
                "X__X___XX_" +
                "X____X____" +
                "X_________" +
                "";
        MiddleField field = FieldFactory.createMiddleField(marks);
        assertThat(field.getBlockCountOnY(0)).isEqualTo(1);
        assertThat(field.getBlockCountOnY(1)).isEqualTo(2);
        assertThat(field.getBlockCountOnY(2)).isEqualTo(4);
        assertThat(field.getBlockCountOnY(3)).isEqualTo(5);
        assertThat(field.getBlockCountOnY(4)).isEqualTo(0);
        assertThat(field.getBlockCountOnY(5)).isEqualTo(7);
        assertThat(field.getBlockCountOnY(6)).isEqualTo(10);
        assertThat(field.getBlockCountOnY(7)).isEqualTo(3);
    }

    @Test
    void testCanMerge1() {
        String marks1 = "" +
                "X_X_X_X__X" +
                "X__X____XX" +
                "__________" +
                "__________" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "__________" +
                "__________" +
                "";
        MiddleField field1 = FieldFactory.createMiddleField(marks1);

        String marks2 = "" +
                "__________" +
                "__________" +
                "X_XX_X_X_X" +
                "XXXXXXXXXX" +
                "__________" +
                "__________" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "";
        MiddleField field2 = FieldFactory.createMiddleField(marks2);

        assertThat(field1.canMerge(field2)).isTrue();
    }

    @Test
    void testCanMerge2() {
        String marks1 = "" +
                "__XX_X_X__" +
                "__________" +
                "__________" +
                "__XX_X_X__" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "XXXXX_____" +
                "XXXXX_____" +
                "";
        MiddleField field1 = FieldFactory.createMiddleField(marks1);

        String marks2 = "" +
                "__________" +
                "__________" +
                "X__X_X_X__" +
                "XXXXXXXXXX" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "";
        MiddleField field2 = FieldFactory.createMiddleField(marks2);

        assertThat(field1.canMerge(field2)).isFalse();
    }

    @Test
    void testMerge1() {
        String marks1 = "" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "__________" +
                "__________" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "__________" +
                "__________" +
                "";
        MiddleField field1 = FieldFactory.createMiddleField(marks1);

        String marks2 = "" +
                "__________" +
                "__________" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "__________" +
                "__________" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "";
        MiddleField field2 = FieldFactory.createMiddleField(marks2);

        String expectedMarks = "" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "";
        MiddleField fieldExpected = FieldFactory.createMiddleField(expectedMarks);

        field1.merge(field2);
        assertThat(field1).isEqualTo(fieldExpected);
        assertThat(field2).isNotEqualTo(fieldExpected);
    }

    @Test
    void testMerge2() {
        String marks1 = "" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "XXXXX_____" +
                "XXXXX_____" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "XXXXX_____" +
                "XXXXX_____" +
                "";
        MiddleField field1 = FieldFactory.createMiddleField(marks1);

        String marks2 = "" +
                "__________" +
                "__________" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "__________" +
                "__________" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "";
        MiddleField field2 = FieldFactory.createMiddleField(marks2);

        String expectedMarks = "" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "XXXXXX_X__" +
                "XXXXXX___X" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "XXXXXX_X__" +
                "XXXXXX___X" +
                "";
        MiddleField fieldExpected = FieldFactory.createMiddleField(expectedMarks);

        field1.merge(field2);
        assertThat(field1).isEqualTo(fieldExpected);
        assertThat(field2).isNotEqualTo(fieldExpected);
    }

    @Test
    void testReduce() {
        String marks1 = "" +
                "XXXXXXXXX_" +
                "__________" +
                "__________" +
                "XXXXXXXXX_" +
                "XXXXXXXXX_" +
                "__________" +
                "__________" +
                "XXXXXXXXX_" +
                "";
        MiddleField field1 = FieldFactory.createMiddleField(marks1);

        String marks2 = "" +
                "XXXXX_____" +
                "_X___X____" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "XXXXX_____" +
                "_X___X____" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "";
        MiddleField field2 = FieldFactory.createMiddleField(marks2);

        String expectedMarks = "" +
                "_____XXXX_" +
                "__________" +
                "__________" +
                "___X__XXX_" +
                "_____XXXX_" +
                "__________" +
                "__________" +
                "___X__XXX_" +
                "";
        MiddleField fieldExpected = FieldFactory.createMiddleField(expectedMarks);

        field1.reduce(field2);
        assertThat(field1).isEqualTo(fieldExpected);
        assertThat(field2).isNotEqualTo(fieldExpected);
    }

    @Test
    void testGetUpperYWith4Blocks() {
        String marks = "" +
                "__________" +
                "_____X____" +
                "____XXX___" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "";
        MiddleField field = FieldFactory.createMiddleField(marks);
        assertThat(field.getUpperYWith4Blocks()).isEqualTo(7);
    }

    @Test
    void testGetUpperYWith4BlocksRandom() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            MiddleField field = FieldFactory.createMiddleField();
            int maxY = -1;
            while (field.getNumOfAllBlocks() != 4) {
                int x = randoms.nextInt(10);
                int y = randoms.nextInt(0, 12);
                field.setBlock(x, y);

                if (maxY < y)
                    maxY = y;
            }

            assertThat(field.getUpperYWith4Blocks()).isEqualTo(maxY);
        }
    }

    @Test
    void testGetLowerY() {
        String marks = "" +
                "__________" +
                "_____X____" +
                "____XXX___" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "";
        MiddleField field = FieldFactory.createMiddleField(marks);
        assertThat(field.getLowerY()).isEqualTo(8);
    }

    @Test
    void testGetLowerYWithEmpty() {
        MiddleField field = FieldFactory.createMiddleField();
        assertThat(field.getLowerY()).isEqualTo(-1);
    }

    @Test
    void testGetLowerYRandom() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            MiddleField field = FieldFactory.createMiddleField();
            int minY = Integer.MAX_VALUE;

            int numOfBlocks = randoms.nextInt(1, 120);
            for (int block = 0; block < numOfBlocks; block++) {
                int x = randoms.nextInt(10);
                int y = randoms.nextInt(0, 12);
                field.setBlock(x, y);

                if (y < minY)
                    minY = y;
            }

            assertThat(field.getLowerY()).isEqualTo(minY);
        }
    }

    @Test
    void testSlideLeft() {
        String marks = "" +
                "__________" +
                "_____X____" +
                "____XXX___" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "";
        MiddleField field = FieldFactory.createMiddleField(marks);

        field.slideLeft(3);

        String expectedMarks = "" +
                "__________" +
                "__X_______" +
                "_XXX______" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "";
        MiddleField expectedField = FieldFactory.createMiddleField(expectedMarks);
        assertThat(field).isEqualTo(expectedField);
    }

    @Test
    void testSlideLeftRandom() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            int slide = randoms.nextInt(10);

            MiddleField field = FieldFactory.createMiddleField();
            MiddleField expect = FieldFactory.createMiddleField();

            int numOfBlocks = randoms.nextInt(1, 120);
            for (int block = 0; block < numOfBlocks; block++) {
                int x = randoms.nextInt(10);
                int y = randoms.nextInt(0, 12);
                field.setBlock(x, y);
                if (0 <= x - slide)
                    expect.setBlock(x - slide, y);
            }

            field.slideLeft(slide);

            assertThat(field).isEqualTo(expect);
        }
    }
}
