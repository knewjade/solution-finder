package core.field;

import core.mino.Block;
import core.mino.Mino;
import core.mino.piece.Piece;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class SmallFieldTest {
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

    private SmallField createRandomSmallField(Randoms randoms) {
        return new SmallField(randoms.field(6, 8).getBoard(0));
    }

    @Test
    void testGetMaxFieldHeight() throws Exception {
        Field field = FieldFactory.createSmallField();
        assertThat(field.getMaxFieldHeight()).isEqualTo(6);
    }

    @Test
    void testPutAndRemoveBlock() throws Exception {
        Field field = FieldFactory.createSmallField();
        assertThat(field.isEmpty(0, 0)).isTrue();
        field.setBlock(0, 0);
        assertThat(field.isEmpty(0, 0)).isFalse();
        field.removeBlock(0, 0);
        assertThat(field.isEmpty(0, 0)).isTrue();
    }

    @Test
    void testPutAndRemoveMino() throws Exception {
        Field field = FieldFactory.createSmallField();
        field.put(new Mino(Block.T, Rotate.Spawn), 1, 0);
        assertThat(field.isEmpty(0, 0)).isFalse();
        assertThat(field.isEmpty(1, 0)).isFalse();
        assertThat(field.isEmpty(2, 0)).isFalse();
        assertThat(field.isEmpty(1, 1)).isFalse();

        field.remove(new Mino(Block.T, Rotate.Spawn), 1, 0);
        assertThat(field.isPerfect()).isTrue();
    }

    @Test
    void testPutAndRemovePiece() throws Exception {
        SmallField field = FieldFactory.createSmallField();
        int maxFieldHeight = field.getMaxFieldHeight();

        ArrayList<Piece> pieces = createAllPieces(maxFieldHeight);
        for (Piece piece : pieces) {
            // Initialize
            Mino mino = piece.getMino();
            int x = piece.getX();
            int y = piece.getY();

            // Expect
            SmallField expected = FieldFactory.createSmallField();
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
                "__________" +
                "_________X" +
                "____X_____" +
                "";
        Field field = FieldFactory.createSmallField(marks);
        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 4)).isEqualTo(0);
        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 3, 4)).isEqualTo(1);
        assertThat(field.getYOnHarddrop(new Mino(Block.T, Rotate.Spawn), 8, 4)).isEqualTo(2);
    }

    @Test
    void testCanReachOnHarddrop() throws Exception {
        String marks = "" +
                "x_________" +
                "_________X" +
                "____X_____" +
                "";
        Field field = FieldFactory.createSmallField(marks);
        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 4)).isTrue();
        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 3)).isTrue();
        assertThat(field.canReachOnHarddrop(new Mino(Block.T, Rotate.Spawn), 1, 1)).isFalse();
    }


    @Test
    void testCanReachOnHarddrop2() throws Exception {
        Randoms randoms = new Randoms();
        SmallField field = createRandomSmallField(randoms);
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
                "__________" +
                "__________" +
                "___X______" +
                "__________" +
                "_______X__" +
                "__________" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.existsAbove(0)).isTrue();
        assertThat(field.existsAbove(1)).isTrue();
        assertThat(field.existsAbove(2)).isTrue();
        assertThat(field.existsAbove(3)).isTrue();
        assertThat(field.existsAbove(4)).isFalse();
        assertThat(field.existsAbove(5)).isFalse();
    }

    @Test
    void testIsPerfect() throws Exception {
        Field field = FieldFactory.createSmallField();

        assertThat(field.isEmpty(0, 0)).isTrue();
        assertThat(field.isPerfect()).isTrue();

        field.setBlock(0, 0);

        assertThat(field.isEmpty(0, 0)).isFalse();
        assertThat(field.isPerfect()).isFalse();
    }

    @Test
    void testIsFilledInColumn() throws Exception {
        String marks = "" +
                "____X_____" +
                "____X_____" +
                "___XX_____" +
                "__XXX_____" +
                "X__XX_____" +
                "__XXXX____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.isFilledInColumn(0, 4)).isFalse();
        assertThat(field.isFilledInColumn(1, 4)).isFalse();
        assertThat(field.isFilledInColumn(2, 4)).isFalse();
        assertThat(field.isFilledInColumn(3, 4)).isTrue();
        assertThat(field.isFilledInColumn(3, 6)).isFalse();
        assertThat(field.isFilledInColumn(4, 4)).isTrue();
        assertThat(field.isFilledInColumn(4, 6)).isTrue();
        assertThat(field.isFilledInColumn(5, 0)).isTrue();
    }

    @Test
    void testIsWallBetweenLeft() throws Exception {
        String marks = "" +
                "____X_____" +
                "____X_____" +
                "X__XX_____" +
                "_XXXX_____" +
                "XX_XX_____" +
                "_XXXXX____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.isWallBetweenLeft(1, 4)).isTrue();
        assertThat(field.isWallBetweenLeft(1, 5)).isFalse();
        assertThat(field.isWallBetweenLeft(2, 3)).isTrue();
        assertThat(field.isWallBetweenLeft(2, 4)).isFalse();
        assertThat(field.isWallBetweenLeft(2, 3)).isTrue();
        assertThat(field.isWallBetweenLeft(2, 4)).isFalse();
        assertThat(field.isWallBetweenLeft(4, 6)).isTrue();
        assertThat(field.isWallBetweenLeft(5, 6)).isTrue();
        assertThat(field.isWallBetweenLeft(6, 6)).isFalse();
    }

    @Test
    void testCanPutMino() throws Exception {
        String marks = "" +
                "___X______" +
                "___XX_____" +
                "__X_X_____" +
                "X___X_____" +
                "__X_XX____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.canPut(new Mino(Block.T, Rotate.Spawn), 5, 4)).isTrue();
        assertThat(field.canPut(new Mino(Block.T, Rotate.Right), 1, 1)).isTrue();
        assertThat(field.canPut(new Mino(Block.T, Rotate.Reverse), 1, 3)).isTrue();
        assertThat(field.canPut(new Mino(Block.T, Rotate.Left), 3, 1)).isTrue();

        assertThat(field.canPut(new Mino(Block.T, Rotate.Spawn), 3, 0)).isFalse();
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
                "XXXXXXXX_X";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.canPut(new Mino(Block.I, Rotate.Left), 8, 1)).isTrue();
        assertThat(field.canPut(new Mino(Block.I, Rotate.Left), 8, 6)).isTrue();
        assertThat(field.canPut(new Mino(Block.I, Rotate.Left), 8, 7)).isTrue();
        assertThat(field.canPut(new Mino(Block.I, Rotate.Left), 8, 8)).isTrue();
        assertThat(field.canPut(new Mino(Block.I, Rotate.Left), 8, 9)).isTrue();
    }

    @Test
    void testCanPutPiece() {
        Randoms randoms = new Randoms();
        SmallField field = createRandomSmallField(randoms);
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

    @Test
    void testIsOnGround() throws Exception {
        String marks = "" +
                "___X______" +
                "___XX_____" +
                "___XX_____" +
                "__X_X_____" +
                "X___X_____" +
                "__X_XX____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Spawn), 5, 5)).isTrue();
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Right), 8, 1)).isTrue();
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Reverse), 1, 3)).isTrue();
        assertThat(field.isOnGround(new Mino(Block.T, Rotate.Left), 1, 2)).isTrue();

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
                "__X_X_____" +
                "X___X_____" +
                "__X_XX____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.getBlockCountBelowOnX(0, 1)).isEqualTo(0);
        assertThat(field.getBlockCountBelowOnX(0, 2)).isEqualTo(1);
        assertThat(field.getBlockCountBelowOnX(2, 4)).isEqualTo(2);
        assertThat(field.getBlockCountBelowOnX(3, 4)).isEqualTo(1);
        assertThat(field.getBlockCountBelowOnX(3, 6)).isEqualTo(3);
        assertThat(field.getBlockCountBelowOnX(4, 4)).isEqualTo(4);
        assertThat(field.getBlockCountBelowOnX(4, 6)).isEqualTo(6);
    }

    @Test
    void testGetAllBlockCount() throws Exception {
        String marks = "" +
                "___XX_____" +
                "___XX_____" +
                "___XX_____" +
                "__X_X_____" +
                "X___X_____" +
                "__X_XX____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.getNumOfAllBlocks()).isEqualTo(13);
    }

    @Test
    void testClearLine() throws Exception {
        String marks = "" +
                "XXX_XXXXXX" +
                "XXXXXXXXXX" +
                "X_XXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXX_XXXXX" +
                "XXXXXXXXXX";
        Field field = FieldFactory.createSmallField(marks);

        int deleteLine = field.clearLine();
        assertThat(deleteLine).isEqualTo(3);

        assertThat(field.existsAbove(2)).isTrue();
        assertThat(field.existsAbove(3)).isFalse();

        assertThat(field.isEmpty(0, 0)).isFalse();
        assertThat(field.isEmpty(4, 0)).isTrue();
        assertThat(field.isEmpty(1, 1)).isTrue();
        assertThat(field.isEmpty(3, 2)).isTrue();
    }

    @Test
    void testClearLine2() throws Exception {
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
            assertThat(line).isEqualTo(deleteLines);

            if (deleteLines < 6)
                assertThat(field.existsAbove(fieldHeight - deleteLines - 1)).isTrue();
            assertThat(field.existsAbove(fieldHeight - deleteLines)).isFalse();
        }
    }

    @Test
    void testClearLineAndInsertBlackLine() throws Exception {
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
        assertThat(Long.bitCount(deleteKey)).isEqualTo(3);
        field.insertBlackLineWithKey(deleteKey);

        for (int index = 0; index < freeze.getNumOfAllBlocks(); index++)
            assertThat(field.getBoard(index)).isEqualTo(freeze.getBoard(index));
    }

    @Test
    void testClearLineAndInsertWhiteLine() throws Exception {
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
        assertThat(Long.bitCount(deleteKey)).isEqualTo(3);
        field.insertWhiteLineWithKey(deleteKey);

        for (int index = 0; index < expected.getNumOfAllBlocks(); index++)
            assertThat(field.getBoard(index)).isEqualTo(expected.getBoard(index));
    }

    @Test
    void testGetBoard() throws Exception {
        String marks = "" +
                "X_________" +
                "X_________" +
                "X_________" +
                "X_________" +
                "";
        SmallField field = FieldFactory.createSmallField(marks);

        assertThat(field.getBoardCount()).isEqualTo(1);
        assertThat(field.getBoard(0))
                .isEqualTo(0x40100401L)
                .isEqualTo(field.getXBoard());

        for (int index = 1; index < 100; index++)
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
        Field field = FieldFactory.createSmallField(marks);

        assertThat(field.getNumOfAllBlocks()).isEqualTo(4);
        Field freeze = field.freeze(field.getMaxFieldHeight());
        field.setBlock(9, 0);

        assertThat(field.getNumOfAllBlocks()).isEqualTo(5);
        assertThat(freeze.getNumOfAllBlocks()).isEqualTo(4);
    }

    @Test
    void testEqual() throws Exception {
        String marks = "XXXXXX____";
        SmallField field1 = FieldFactory.createSmallField(marks);
        SmallField field2 = FieldFactory.createSmallField(marks);
        assertThat(field1.equals(field2)).isTrue();

        SmallField field3 = FieldFactory.createSmallField(marks + "XXXXXX____");
        assertThat(field1.equals(field3)).isFalse();

        MiddleField field4 = FieldFactory.createMiddleField(marks);
        assertThat(field1.equals(field4)).isTrue();
    }

    @Test
    void testGetBlockCountOnY() {
        String marks = "" +
                "__________" +
                "X___XXX__X" +
                "X__X___XX_" +
                "X____X____" +
                "X_________" +
                "";
        Field field = FieldFactory.createSmallField(marks);
        assertThat(field.getBlockCountOnY(0)).isEqualTo(1);
        assertThat(field.getBlockCountOnY(1)).isEqualTo(2);
        assertThat(field.getBlockCountOnY(2)).isEqualTo(4);
        assertThat(field.getBlockCountOnY(3)).isEqualTo(5);
        assertThat(field.getBlockCountOnY(4)).isEqualTo(0);
    }

    @Test
    void testCanMerge1() {
        String marks1 = "" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "__________" +
                "__________" +
                "";
        Field field1 = FieldFactory.createSmallField(marks1);

        String marks2 = "" +
                "__________" +
                "__________" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "";
        Field field2 = FieldFactory.createSmallField(marks2);

        assertThat(field1.canMerge(field2)).isTrue();
    }

    @Test
    void testCanMerge2() {
        String marks1 = "" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "XXXXX_____" +
                "XXXXX_____" +
                "";
        Field field1 = FieldFactory.createSmallField(marks1);

        String marks2 = "" +
                "__________" +
                "__________" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "";
        Field field2 = FieldFactory.createSmallField(marks2);

        assertThat(field1.canMerge(field2)).isFalse();
    }

    @Test
    void testMerge1() {
        String marks1 = "" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "__________" +
                "__________" +
                "";
        Field field1 = FieldFactory.createSmallField(marks1);

        String marks2 = "" +
                "__________" +
                "__________" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "";
        Field field2 = FieldFactory.createSmallField(marks2);

        String expectedMarks = "" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "";
        Field fieldExpected = FieldFactory.createSmallField(expectedMarks);

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
                "";
        Field field1 = FieldFactory.createSmallField(marks1);

        String marks2 = "" +
                "__________" +
                "__________" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "";
        Field field2 = FieldFactory.createSmallField(marks2);

        String expectedMarks = "" +
                "XXX_XXX__X" +
                "X__X___XX_" +
                "XXXXXX_X__" +
                "XXXXXX___X" +
                "";
        Field fieldExpected = FieldFactory.createSmallField(expectedMarks);

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
                "";
        Field field1 = FieldFactory.createSmallField(marks1);

        String marks2 = "" +
                "XXXXX_____" +
                "_X___X____" +
                "X__X_X_X__" +
                "XXX_XX___X" +
                "";
        Field field2 = FieldFactory.createSmallField(marks2);

        String expectedMarks = "" +
                "_____XXXX_" +
                "__________" +
                "__________" +
                "___X__XXX_" +
                "";
        Field fieldExpected = FieldFactory.createSmallField(expectedMarks);

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
                "";
        Field field = FieldFactory.createSmallField(marks);
        assertThat(field.getUpperYWith4Blocks()).isEqualTo(2);
    }

    @Test
    void testGetUpperYWith4BlocksRandom() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            SmallField field = FieldFactory.createSmallField();
            int maxY = -1;
            while (field.getNumOfAllBlocks() != 4) {
                int x = randoms.nextInt(10);
                int y = randoms.nextInt(0, 6);
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
                "";
        Field field = FieldFactory.createSmallField(marks);
        assertThat(field.getLowerY()).isEqualTo(1);
    }

    @Test
    void testGetLowerYWithEmpty() {
        Field field = FieldFactory.createSmallField();
        assertThat(field.getLowerY()).isEqualTo(-1);
    }

    @Test
    void testGetLowerYRandom() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            SmallField field = FieldFactory.createSmallField();
            int minY = Integer.MAX_VALUE;

            int numOfBlocks = randoms.nextInt(1, 60);
            for (int block = 0; block < numOfBlocks; block++) {
                int x = randoms.nextInt(10);
                int y = randoms.nextInt(0, 6);
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
                "";
        Field field = FieldFactory.createSmallField(marks);

        field.slideLeft(3);

        String expectedMarks = "" +
                "__________" +
                "__X_______" +
                "_XXX______" +
                "__________" +
                "";
        Field expectedField = FieldFactory.createSmallField(expectedMarks);
        assertThat(field).isEqualTo(expectedField);
    }

    @Test
    void testSlideLeftRandom() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            int slide = randoms.nextInt(10);

            SmallField field = FieldFactory.createSmallField();
            SmallField expect = FieldFactory.createSmallField();

            int numOfBlocks = randoms.nextInt(1, 60);
            for (int block = 0; block < numOfBlocks; block++) {
                int x = randoms.nextInt(10);
                int y = randoms.nextInt(0, 6);
                field.setBlock(x, y);
                if (0 <= x - slide)
                    expect.setBlock(x - slide, y);
            }

            field.slideLeft(slide);

            assertThat(field).isEqualTo(expect);
        }
    }
}
