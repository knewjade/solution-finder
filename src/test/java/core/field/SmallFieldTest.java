package core.field;

import core.mino.Block;
import core.srs.Rotate;
import core.mino.Mino;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SmallFieldTest {
    public static final int FIELD_WIDTH = 10;

    @Test
    public void testGetMaxFieldHeight() throws Exception {
        Field field = FieldFactory.createSmallField();
        assertThat(field.getMaxFieldHeight(), is(6));
    }

    @Test
    public void testPutAndRemoveBlock() throws Exception {
        Field field = FieldFactory.createSmallField();
        assertThat(field.isEmpty(0, 0), is(true));
        field.setBlock(0, 0);
        assertThat(field.isEmpty(0, 0), is(false));
        field.removeBlock(0, 0);
        assertThat(field.isEmpty(0, 0), is(true));
    }

    @Test
    public void testPutAndRemoveMino() throws Exception {
        Field field = FieldFactory.createSmallField();
        field.putMino(new Mino(Block.T, Rotate.Spawn), 1, 0);
        assertThat(field.isEmpty(0, 0), is(false));
        assertThat(field.isEmpty(1, 0), is(false));
        assertThat(field.isEmpty(2, 0), is(false));
        assertThat(field.isEmpty(1, 1), is(false));

        field.removeMino(new Mino(Block.T, Rotate.Spawn), 1, 0);
        assertThat(field.isPerfect(), is(true));
    }

    @Test
    public void testGetYOnHarddrop() throws Exception {
        String marks = "" +
                "__________" +
                "_________X" +
                "____X_____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 4), is(0));
        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 3, 4), is(1));
        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 8, 4), is(2));
    }

    @Test
    public void testCanReachOnHarddrop() throws Exception {
        String marks = "" +
                "x_________" +
                "_________X" +
                "____X_____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 4), is(true));
        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 3), is(true));
        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 1), is(false));
    }

    @Test
    public void testExistAbove() throws Exception {
        String marks = "" +
                "__________" +
                "__________" +
                "___X______" +
                "__________" +
                "_______X__" +
                "__________" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.existsAbove(0), is(true));
        assertThat(field.existsAbove(1), is(true));
        assertThat(field.existsAbove(2), is(true));
        assertThat(field.existsAbove(3), is(true));
        assertThat(field.existsAbove(4), is(false));
        assertThat(field.existsAbove(5), is(false));
    }

    @Test
    public void testIsPerfect() throws Exception {
        Field field = FieldFactory.createSmallField();

        assertThat(field.isEmpty(0, 0), is(true));
        assertThat(field.isPerfect(), is(true));

        field.setBlock(0, 0);

        assertThat(field.isEmpty(0, 0), is(false));
        assertThat(field.isPerfect(), is(false));
    }

    @Test
    public void testIsFilledInColumn() throws Exception {
        String marks = "" +
                "____X_____" +
                "____X_____" +
                "___XX_____" +
                "__XXX_____" +
                "X__XX_____" +
                "__XXXX____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.isFilledInColumn(0, 4), is(false));
        assertThat(field.isFilledInColumn(1, 4), is(false));
        assertThat(field.isFilledInColumn(2, 4), is(false));
        assertThat(field.isFilledInColumn(3, 4), is(true));
        assertThat(field.isFilledInColumn(3, 6), is(false));
        assertThat(field.isFilledInColumn(4, 4), is(true));
        assertThat(field.isFilledInColumn(4, 6), is(true));
        assertThat(field.isFilledInColumn(5, 0), is(true));
    }

    @Test
    public void testIsWallBetweenLeft() throws Exception {
        String marks = "" +
                "____X_____" +
                "____X_____" +
                "X__XX_____" +
                "_XXXX_____" +
                "XX_XX_____" +
                "_XXXXX____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.isWallBetweenLeft(1, 4), is(true));
        assertThat(field.isWallBetweenLeft(1, 5), is(false));
        assertThat(field.isWallBetweenLeft(2, 3), is(true));
        assertThat(field.isWallBetweenLeft(2, 4), is(false));
        assertThat(field.isWallBetweenLeft(2, 3), is(true));
        assertThat(field.isWallBetweenLeft(2, 4), is(false));
        assertThat(field.isWallBetweenLeft(4, 6), is(true));
        assertThat(field.isWallBetweenLeft(5, 6), is(true));
        assertThat(field.isWallBetweenLeft(6, 6), is(false));
    }

    @Test
    public void testCanPutMino() throws Exception {
        String marks = "" +
                "___X______" +
                "___XX_____" +
                "__X_X_____" +
                "X___X_____" +
                "__X_XX____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Spawn), 5, 4), is(true));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Right), 1, 1), is(true));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Reverse), 1, 3), is(true));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Left), 3, 1), is(true));

        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Spawn), 3, 0), is(false));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Right), 0, 1), is(false));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Reverse), 1, 1), is(false));
        assertThat(field.canPutMino(new Mino(Block.T, Rotate.Left), 1, 1), is(false));
    }

    @Test
    public void testIsOnGround() throws Exception {
        String marks = "" +
                "___X______" +
                "___XX_____" +
                "___XX_____" +
                "__X_X_____" +
                "X___X_____" +
                "__X_XX____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Spawn), 5, 5), is(true));
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Right), 8, 1), is(true));
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Reverse), 1, 3), is(true));
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Left), 1, 2), is(true));

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
                "__X_X_____" +
                "X___X_____" +
                "__X_XX____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.getBlockCountBelowOnX(0, 1), is(0));
        assertThat(field.getBlockCountBelowOnX(0, 2), is(1));
        assertThat(field.getBlockCountBelowOnX(2, 4), is(2));
        assertThat(field.getBlockCountBelowOnX(3, 4), is(1));
        assertThat(field.getBlockCountBelowOnX(3, 6), is(3));
        assertThat(field.getBlockCountBelowOnX(4, 4), is(4));
        assertThat(field.getBlockCountBelowOnX(4, 6), is(6));
    }

    @Test
    public void testGetAllBlockCount() throws Exception {
        String marks = "" +
                "___XX_____" +
                "___XX_____" +
                "___XX_____" +
                "__X_X_____" +
                "X___X_____" +
                "__X_XX____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.getNumOfAllBlocks(), is(13));
    }

    @Test
    public void testClearLine() throws Exception {
        String marks = "" +
                "XXX_XXXXXX" +
                "XXXXXXXXXX" +
                "X_XXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXX_XXXXX" +
                "XXXXXXXXXX";
        Field field = FieldFactory.createSmallField(marks);

        int deleteLine = field.clearLine();
        assertThat(deleteLine, is(3));

        assertThat(field.existsAbove(2), is(true));
        assertThat(field.existsAbove(3), is(false));

        assertThat(field.isEmpty(0, 0), is(false));
        assertThat(field.isEmpty(4, 0), is(true));
        assertThat(field.isEmpty(1, 1), is(true));
        assertThat(field.isEmpty(3, 2), is(true));
    }

    @Test
    public void testClearLine2() throws Exception {
        int fieldHeight = 6;

        for (int pattern = 0; pattern < 64; pattern++) {
            ArrayList<Boolean> leftFlags = new ArrayList<>();
            int value = pattern;
            int deleteLines = 0;
            for (int i = 0; i < fieldHeight; i++) {
                boolean isLeft = (value & 1) != 0;
                leftFlags.add(isLeft);
                value >>>= 1;
                if (!isLeft)
                    deleteLines++;
            }

            SmallField field = FieldFactory.createSmallField();
            for (int index = 0; index < leftFlags.size(); index++) {
                if (leftFlags.get(index)) {
                    for (int x = 0; x < FIELD_WIDTH - 1; x++)
                        field.setBlock(x, index);
                } else {
                    for (int x = 0; x < FIELD_WIDTH; x++)
                        field.setBlock(x, index);
                }
            }

            int line = field.clearLine();
            assertThat(line, is(deleteLines));

            if (deleteLines < 6)
                assertThat(field.existsAbove(fieldHeight - deleteLines - 1), is(true));
            assertThat(field.existsAbove(fieldHeight - deleteLines), is(false));
        }
    }

    @Test
    public void testClearLineAndInsertBlackLine() throws Exception {
        String marks = "" +
                "XXX_XXXXXX" +
                "XXXXXXXXXX" +
                "X_XXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXX_XXXXX" +
                "XXXXXXXXXX" +
                "";
        Field field = FieldFactory.createSmallField(marks);
        Field freeze = field.freeze(field.getMaxFieldHeight());

        long deleteKey = field.clearLineReturnKey();
        assertThat(Long.bitCount(deleteKey), is(3));
        field.insertBlackLineWithKey(deleteKey);

        for (int index = 0; index < freeze.getNumOfAllBlocks(); index++)
            assertThat(field.getBoard(index), is(freeze.getBoard(index)));
    }

    @Test
    public void testClearLineAndInsertWhiteLine() throws Exception {
        String marks = "" +
                "XXX_XXXXXX" +
                "XXXXXXXXXX" +
                "X_XXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXX_XXXXX" +
                "XXXXXXXXXX" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        String expectMarks = "" +
                "XXX_XXXXXX" +
                "__________" +
                "X_XXXXXXXX" +
                "__________" +
                "XXXX_XXXXX" +
                "__________" +
                "";
        Field expected = FieldFactory.createSmallField(expectMarks);

        long deleteKey = field.clearLineReturnKey();
        assertThat(Long.bitCount(deleteKey), is(3));
        field.insertWhiteLineWithKey(deleteKey);

        for (int index = 0; index < expected.getNumOfAllBlocks(); index++)
            assertThat(field.getBoard(index), is(expected.getBoard(index)));
    }

    @Test
    public void testGetBoard() throws Exception {
        String marks = "" +
                "X_________" +
                "X_________" +
                "X_________" +
                "X_________" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.getBoardCount(), is(1));
        assertThat(field.getBoard(0), is(0x40100401L));
    }

    @Test
    public void testFreeze() throws Exception {
        String marks = "" +
                "X_________" +
                "X_________" +
                "X_________" +
                "X_________" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.getNumOfAllBlocks(), is(4));
        Field freeze = field.freeze(field.getMaxFieldHeight());
        field.setBlock(9, 0);

        assertThat(field.getNumOfAllBlocks(), is(5));
        assertThat(freeze.getNumOfAllBlocks(), is(4));
    }

    @Test
    public void testEqual() throws Exception {
        String marks = "XXXXXX____";
        SmallField field1 = FieldFactory.createSmallField(marks);
        SmallField field2 = FieldFactory.createSmallField(marks);
        assertThat(field1.equals(field2), is(true));

        SmallField field3 = FieldFactory.createSmallField(marks + "XXXXXX____");
        assertThat(field1.equals(field3), is(false));

        MiddleField field4 = FieldFactory.createMiddleField(marks);
        assertThat(field1.equals(field4), is(true));
    }
}
