package core.field;

import core.mino.Block;
import core.srs.Rotate;
import core.mino.Mino;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MiddleFieldTest {
    @Test
    public void testGetMaxFieldHeight() throws Exception {
        Field field = FieldFactory.createMiddleField();
        assertThat(field.getMaxFieldHeight(), is(12));
    }

    @Test
    public void testPutAndRemoveBlock() throws Exception {
        Field field = FieldFactory.createSmallField();
        assertThat(field.isEmpty(0, 0), is(true));
        field.setBlock(0, 0);
        assertThat(field.isEmpty(0, 0), is(false));
        field.removeBlock(0, 0);
        assertThat(field.isEmpty(0, 0), is(true));

        assertThat(field.isEmpty(9, 9), is(true));
        field.setBlock(9, 9);
        assertThat(field.isEmpty(9, 9), is(false));
        field.removeBlock(9, 9);
        assertThat(field.isEmpty(9, 9), is(true));
    }

    @Test
    public void testPutAndRemoveMino() throws Exception {
        Field field = FieldFactory.createMiddleField();

        field.putMino(new Mino(Block.T, Rotate.Spawn), 1, 0);
        assertThat(field.isEmpty(0, 0), is(false));
        assertThat(field.isEmpty(1, 0), is(false));
        assertThat(field.isEmpty(2, 0), is(false));
        assertThat(field.isEmpty(1, 1), is(false));

        field.putMino(new Mino(Block.I, Rotate.Left), 4, 6);
        assertThat(field.isEmpty(4, 5), is(false));
        assertThat(field.isEmpty(4, 6), is(false));
        assertThat(field.isEmpty(4, 7), is(false));
        assertThat(field.isEmpty(4, 8), is(false));

        field.putMino(new Mino(Block.O, Rotate.Spawn), 8, 8);
        assertThat(field.isEmpty(8, 8), is(false));
        assertThat(field.isEmpty(8, 9), is(false));
        assertThat(field.isEmpty(9, 8), is(false));
        assertThat(field.isEmpty(9, 9), is(false));

        field.removeMino(new Mino(Block.T, Rotate.Spawn), 1, 0);
        field.removeMino(new Mino(Block.I, Rotate.Left), 4, 6);
        field.removeMino(new Mino(Block.O, Rotate.Spawn), 8, 8);
        assertThat(field.isPerfect(), is(true));
    }

    @Test
    public void testGetYOnHarddrop() throws Exception {
        String marks = "" +
                "X_________" +
                "__________" +
                "__________" +
                "__________" +
                "_________X" +
                "____X_____" +
                "";
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 10), is(6));
        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 2, 10), is(0));
        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 3, 10), is(1));
        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 8, 10), is(2));
    }

    @Test
    public void testCanReachOnHarddrop() throws Exception {
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

        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 4), is(false));
        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 2, 4), is(true));
        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 2, 3), is(true));
        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 1), is(false));
    }

    @Test
    public void testExistAbove() throws Exception {
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

        assertThat(field.existsAbove(0), is(true));
        assertThat(field.existsAbove(6), is(true));
        assertThat(field.existsAbove(7), is(true));
        assertThat(field.existsAbove(8), is(false));
        assertThat(field.existsAbove(9), is(false));
    }

    @Test
    public void testIsPerfect() throws Exception {
        Field field = FieldFactory.createMiddleField();

        assertThat(field.isEmpty(0, 0), is(true));
        assertThat(field.isPerfect(), is(true));

        field.setBlock(7, 8);

        assertThat(field.isEmpty(7, 8), is(false));
        assertThat(field.isPerfect(), is(false));
    }

    @Test
    public void testIsFilledInColumn() throws Exception {
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

        assertThat(field.isFilledInColumn(0, 4), is(false));
        assertThat(field.isFilledInColumn(1, 4), is(false));
        assertThat(field.isFilledInColumn(2, 4), is(false));
        assertThat(field.isFilledInColumn(3, 4), is(true));
        assertThat(field.isFilledInColumn(3, 6), is(false));
        assertThat(field.isFilledInColumn(4, 4), is(true));
        assertThat(field.isFilledInColumn(4, 6), is(true));
        assertThat(field.isFilledInColumn(4, 9), is(true));
        assertThat(field.isFilledInColumn(4, 10), is(false));
        assertThat(field.isFilledInColumn(5, 7), is(false));
    }

    @Test
    public void testIsWallBetweenLeft() throws Exception {
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

        assertThat(field.isWallBetweenLeft(1, 4), is(true));
        assertThat(field.isWallBetweenLeft(1, 5), is(false));
        assertThat(field.isWallBetweenLeft(2, 4), is(true));
        assertThat(field.isWallBetweenLeft(2, 5), is(false));
        assertThat(field.isWallBetweenLeft(3, 4), is(true));
        assertThat(field.isWallBetweenLeft(3, 5), is(false));
        assertThat(field.isWallBetweenLeft(4, 9), is(true));
        assertThat(field.isWallBetweenLeft(4, 10), is(false));
        assertThat(field.isWallBetweenLeft(5, 9), is(true));
        assertThat(field.isWallBetweenLeft(5, 10), is(false));
        assertThat(field.isWallBetweenLeft(6, 6), is(false));
    }

    @Test
    public void testCanPutMino() throws Exception {
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

        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Spawn), 4, 7), is(true));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Spawn), 5, 6), is(true));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Right), 1, 1), is(true));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Reverse), 1, 3), is(true));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Left), 3, 1), is(true));

        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Spawn), 5, 7), is(false));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Spawn), 4, 6), is(false));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Right), 0, 1), is(false));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Reverse), 1, 1), is(false));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Left), 1, 1), is(false));
    }

    @Test
    public void testIsOnGround() throws Exception {
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

        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Spawn), 1, 7), is(true));
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Spawn), 5, 5), is(true));
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Right), 8, 1), is(true));
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Reverse), 1, 3), is(true));
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Left), 1, 2), is(true));

        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Spawn), 1, 6), is(false));
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Spawn), 6, 5), is(false));
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Spawn), 8, 1), is(false));
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Right), 8, 2), is(false));
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Reverse), 7, 3), is(false));
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Left), 9, 2), is(false));
    }

    @Test
    public void testGetBlockCountBelowOnX() throws Exception {
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

        assertThat(field.getBlockCountBelowOnX(0, 1), is(0));
        assertThat(field.getBlockCountBelowOnX(0, 2), is(1));
        assertThat(field.getBlockCountBelowOnX(2, 4), is(2));
        assertThat(field.getBlockCountBelowOnX(3, 4), is(1));
        assertThat(field.getBlockCountBelowOnX(3, 6), is(3));
        assertThat(field.getBlockCountBelowOnX(3, 9), is(5));
        assertThat(field.getBlockCountBelowOnX(3, 10), is(6));
        assertThat(field.getBlockCountBelowOnX(4, 6), is(6));
        assertThat(field.getBlockCountBelowOnX(4, 9), is(9));
        assertThat(field.getBlockCountBelowOnX(4, 10), is(10));
    }

    @Test
    public void testGetAllBlockCount() throws Exception {
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

        assertThat(field.getAllBlockCount(), is(17));
    }

    @Test
    public void testClearLine1() throws Exception {
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
        assertThat(deleteLine, is(6));

        assertThat(field.existsAbove(3), is(true));
        assertThat(field.existsAbove(4), is(false));

        assertThat(field.isEmpty(0, 0), is(false));
        assertThat(field.isEmpty(4, 0), is(true));
        assertThat(field.isEmpty(1, 1), is(true));
        assertThat(field.isEmpty(3, 2), is(true));
        assertThat(field.isEmpty(8, 3), is(true));
    }

    @Test
    public void testClearLine2() throws Exception {
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
        assertThat(deleteLine, is(3));

        assertThat(field.existsAbove(8), is(true));
        assertThat(field.existsAbove(9), is(false));

        for (int index = 0; index < 9; index++)
            assertThat(field.isEmpty(index, index), is(true));
    }

    @Test
    public void testClearLine3() throws Exception {
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
        assertThat(deleteLine, is(6));

        assertThat(field.existsAbove(5), is(true));
        assertThat(field.existsAbove(6), is(false));

        for (int index = 0; index < 6; index++)
            assertThat(field.isEmpty(index, index), is(true));
    }

    @Test
    public void testClearLineAndInsertBlackLine() throws Exception {
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
        assertThat(Long.bitCount(deleteKey), is(6));
        field.insertBlackLineWithKey(deleteKey);

        for (int index = 0; index < freeze.getBoardCount(); index++)
            assertThat(field.getBoard(index), is(freeze.getBoard(index)));
    }

    @Test
    public void testClearLineAndInsertWhiteLine() throws Exception {
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
        assertThat(Long.bitCount(deleteKey), is(6));
        field.insertWhiteLineWithKey(deleteKey);

        for (int index = 0; index < expected.getBoardCount(); index++)
            assertThat(field.getBoard(index), is(expected.getBoard(index)));
    }

    @Test
    public void testGetBoard() throws Exception {
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
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.getBoardCount(), is(2));
        assertThat(field.getBoard(0), is(0x4010040100401L));
        assertThat(field.getBoard(1), is(0x20080200L));
    }

    @Test
    public void testFreeze() throws Exception {
        String marks = "" +
                "X_________" +
                "X_________" +
                "X_________" +
                "X_________" +
                "";
        Field field = FieldFactory.createMiddleField(marks);

        assertThat(field.getAllBlockCount(), is(4));
        Field freeze = field.freeze(field.getMaxFieldHeight());
        field.setBlock(9, 0);

        assertThat(field.getAllBlockCount(), is(5));
        assertThat(freeze.getAllBlockCount(), is(4));
    }
}
