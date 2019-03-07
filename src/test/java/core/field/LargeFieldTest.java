package core.field;

import common.tetfu.common.ColorType;
import common.tetfu.field.ArrayColoredField;
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
            assertThat(field.isOnGround(new Mino(Piece.I, Rotate.Spawn), 4, y - 1)).isTrue();
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

    @Test
    void clearLine() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 100000; count++) {
            Field field = randoms.field(FIELD_HEIGHT, randoms.nextIntOpen(5, 20));
            String format = String.format("%dL, %dL, %dL, %dL%n", field.getBoard(0), field.getBoard(1), field.getBoard(2), field.getBoard(3));

            // 配列ベースのフィールドに変換
            ArrayColoredField coloredField = new ArrayColoredField(FIELD_HEIGHT);
            for (int y = 0; y < FIELD_HEIGHT; y++)
                for (int x = 0; x < FIELD_WIDTH; x++)
                    coloredField.setColorType(field.isEmpty(x, y) ? ColorType.Empty : ColorType.Gray, x, y);

            // ライン消去
            field.clearLine();
            coloredField.clearLine();

            // 確認
            for (int y = 0; y < FIELD_HEIGHT; y++)
                for (int x = 0; x < FIELD_WIDTH; x++)
                    assertThat(field.isEmpty(x, y))
                            .as(format)
                            .isEqualTo(coloredField.getColorType(x, y) == ColorType.Empty);
        }
    }

    @Test
    void insertBlackLineWithKey() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 1000000; count++) {
            Field field = randoms.field(FIELD_HEIGHT, randoms.nextIntOpen(5, 20));
            String format = String.format("%dL, %dL, %dL, %dL%n", field.getBoard(0), field.getBoard(1), field.getBoard(2), field.getBoard(3));

            Field freeze = field.freeze(FIELD_HEIGHT);

            long key = field.clearLineReturnKey();
            field.insertBlackLineWithKey(key);

            assertThat(field)
                    .as(format)
                    .isEqualTo(freeze);
        }
    }

    @Test
    void insertWhiteLineWithKey() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 500000; count++) {
            Field field = randoms.field(FIELD_HEIGHT, randoms.nextIntOpen(5, 20));
            String format = String.format("%dL, %dL, %dL, %dL%n", field.getBoard(0), field.getBoard(1), field.getBoard(2), field.getBoard(3));

            Field freeze = field.freeze(FIELD_HEIGHT);

            LOOP_Y:
            for (int y = 0; y < FIELD_HEIGHT; y++) {
                for (int x = 0; x < FIELD_WIDTH; x++)
                    if (freeze.isEmpty(x, y)) continue LOOP_Y;

                for (int x = 0; x < FIELD_WIDTH; x++)
                    freeze.removeBlock(x, y);
            }

            long key = field.clearLineReturnKey();
            field.insertWhiteLineWithKey(key);

            assertThat(field)
                    .as(format)
                    .isEqualTo(freeze);
        }
    }

    @Test
    void fillLine() {
        for (int y = 0; y < FIELD_HEIGHT; y++) {
            LargeField field = new LargeField();
            field.fillLine(y);

            for (int x = 0; x < FIELD_WIDTH; x++)
                assertThat(field.isEmpty(x, y)).isFalse();

            field.clearLine();
            assertThat(field.isPerfect()).isTrue();
        }
    }

    @Test
    void getUpperYWith4BlocksRandom() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 1000000; count++) {
            LargeField field = FieldFactory.createLargeField();
            int maxY = -1;
            while (field.getNumOfAllBlocks() != 4) {
                int x = randoms.nextIntOpen(Randoms.FIELD_WIDTH);
                int y = randoms.nextIntOpen(0, FIELD_HEIGHT);
                field.setBlock(x, y);

                if (maxY < y)
                    maxY = y;
            }

            assertThat(field.getUpperYWith4Blocks()).isEqualTo(maxY);
        }
    }

    @Test
    void getLowerY() {
        // empty
        {
            LargeField field = FieldFactory.createLargeField();
            assertThat(field.getLowerY()).isEqualTo(-1);
        }

        // 10 blocks
        Randoms randoms = new Randoms();
        for (int count = 0; count < 1000000; count++) {
            LargeField field = FieldFactory.createLargeField();
            int minY = Integer.MAX_VALUE;
            for (int i = 0; i < 10; i++) {
                int x = randoms.nextIntOpen(Randoms.FIELD_WIDTH);
                int y = randoms.nextIntOpen(0, FIELD_HEIGHT);
                field.setBlock(x, y);

                if (y < minY)
                    minY = y;
            }

            assertThat(field.getLowerY()).isEqualTo(minY);
        }
    }

    @Test
    void contains() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 100000; count++) {
            Field initField = randoms.field(FIELD_HEIGHT, randoms.nextIntOpen(5, 20));

            {
                Field field = initField.freeze(FIELD_HEIGHT);
                for (int i = 0; i < 100; i++) {
                    int x = randoms.nextIntOpen(Randoms.FIELD_WIDTH);
                    int y = randoms.nextIntOpen(0, FIELD_HEIGHT);
                    field.removeBlock(x, y);

                    assertThat(initField.contains(field)).isTrue();
                }
            }

            {
                Field field = initField.freeze(FIELD_HEIGHT);
                for (int i = 0; i < 100; i++) {
                    int x = randoms.nextIntOpen(Randoms.FIELD_WIDTH);
                    int y = randoms.nextIntOpen(0, FIELD_HEIGHT);

                    if (!field.isEmpty(x, y))
                        continue;

                    field.setBlock(x, y);

                    assertThat(initField.contains(field)).isFalse();
                }
            }
        }
    }

    @Test
    void slideDown() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 100000; count++) {
            Field field = new LargeField();
            Field expected = new LargeField();

            for (int x = 0; x < FIELD_WIDTH; x++) {
                if (randoms.nextBoolean())
                    field.setBlock(x, 0);
            }

            for (int y = 1; y < FIELD_HEIGHT; y++) {
                for (int x = 0; x < FIELD_WIDTH; x++) {
                    if (randoms.nextBoolean()) {
                        field.setBlock(x, y);
                        expected.setBlock(x, y - 1);
                    }
                }
            }

            field.slideDown();

            assertThat(field).isEqualTo(expected);
        }
    }

    @Test
    void slideDownN() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 100000; count++) {
            Field field = randoms.field(FIELD_HEIGHT, 30);
            int slide = randoms.nextIntOpen(FIELD_HEIGHT + 1);

            Field freeze = field.freeze();
            for (int n = 0; n < slide; n++) {
                freeze.slideDown();
            }

            field.slideDown(slide);
            assertThat(field).isEqualTo(freeze);
        }
    }

    @Test
    void slideUpWithWhiteLine() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 100000; count++) {
            Field field = randoms.field(FIELD_HEIGHT, 30);

            Field freeze = field.freeze();
            freeze.slideDown();

            freeze.slideUpWithWhiteLine(1);

            for (int x = 0; x < FIELD_WIDTH; x++) {
                field.removeBlock(x, 0);
            }

            assertThat(field).isEqualTo(freeze);
        }
    }

    @Test
    void slideUpWithBlackLine() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 100000; count++) {
            Field field = randoms.field(FIELD_HEIGHT, 30);

            Field freeze = field.freeze();
            freeze.slideDown();

            freeze.slideUpWithBlackLine(1);

            for (int x = 0; x < FIELD_WIDTH; x++) {
                field.setBlock(x, 0);
            }

            assertThat(field).isEqualTo(freeze);
        }
    }

    @Test
    void slideUpWithWhiteLineN() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 100000; count++) {
            Field field = randoms.field(FIELD_HEIGHT, 30);
            int slide = randoms.nextIntOpen(FIELD_HEIGHT + 1);

            Field freeze = field.freeze();
            for (int n = 0; n < slide; n++) {
                freeze.slideUpWithWhiteLine(1);
            }

            field.slideUpWithWhiteLine(slide);
            assertThat(field).isEqualTo(freeze);
        }
    }

    @Test
    void slideUpWithBlackLineN() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 100000; count++) {
            Field field = randoms.field(FIELD_HEIGHT, 30);
            int slide = randoms.nextIntOpen(FIELD_HEIGHT + 1);

            Field freeze = field.freeze();
            for (int n = 0; n < slide; n++) {
                freeze.slideUpWithBlackLine(1);
            }

            field.slideUpWithBlackLine(slide);
            assertThat(field).isEqualTo(freeze);
        }
    }

    @Test
    void slideLeft() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 100000; count++) {
            Field field = new LargeField();
            Field expected = new LargeField();

            int slide = randoms.nextIntClosed(0, 9);

            for (int x = 0; x < slide; x++) {
                for (int y = 0; y < FIELD_HEIGHT; y++) {
                    if (randoms.nextBoolean())
                        field.setBlock(x, y);
                }
            }

            for (int x = slide; x < FIELD_WIDTH; x++) {
                for (int y = 0; y < FIELD_HEIGHT; y++) {
                    if (randoms.nextBoolean()) {
                        field.setBlock(x, y);
                        expected.setBlock(x - slide, y);
                    }
                }
            }

            field.slideLeft(slide);

            assertThat(field).isEqualTo(expected);
        }
    }

    @Test
    void slideRight() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 100000; count++) {
            Field field = new LargeField();
            Field expected = new LargeField();

            int slide = randoms.nextIntClosed(0, 9);

            for (int x = 9; 9 - slide < x; x--) {
                for (int y = 0; y < FIELD_HEIGHT; y++) {
                    if (randoms.nextBoolean())
                        field.setBlock(x, y);
                }
            }

            for (int x = 9 - slide; 0 <= x; x--) {
                for (int y = 0; y < FIELD_HEIGHT; y++) {
                    if (randoms.nextBoolean()) {
                        field.setBlock(x, y);
                        expected.setBlock(x + slide, y);
                    }
                }
            }

            field.slideRight(slide);

            assertThat(field).isEqualTo(expected);
        }
    }

    @Test
    void inverse() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            Field initField = randoms.field(FIELD_HEIGHT, randoms.nextIntOpen(5, 20));

            Field field = initField.freeze(FIELD_HEIGHT);
            field.inverse();

            for (int y = 0; y < FIELD_HEIGHT; y++)
                for (int x = 0; x < FIELD_WIDTH; x++)
                    assertThat(field.isEmpty(x, y)).isNotEqualTo(initField.isEmpty(x, y));
        }
    }

    @Test
    void mirror() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            Field initField = randoms.field(FIELD_HEIGHT, randoms.nextIntOpen(3, 10));

            Field field = initField.freeze(FIELD_HEIGHT);
            field.mirror();

            for (int y = 0; y < FIELD_HEIGHT; y++)
                for (int x = 0; x < FIELD_WIDTH; x++)
                    assertThat(field.isEmpty(x, y)).isEqualTo(initField.isEmpty(9 - x, y));
        }
    }

    @Test
    void getMinX() {
        {
            int minX = FieldFactory.createLargeField().getMinX();
            assertThat(minX).isEqualTo(-1);
        }

        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            Field initField = randoms.field(FIELD_HEIGHT, randoms.nextIntOpen(3, 10));

            Field field = initField.freeze(FIELD_HEIGHT);
            int minX = field.getMinX();

            int expectedMinX = -1;
            for (int x = 0; x < 10; x++) {
                boolean isExists = false;
                for (int y = 0; y < FIELD_HEIGHT; y++) {
                    if (!field.isEmpty(x, y)) {
                        isExists = true;
                        break;
                    }
                }
                if (isExists) {
                    expectedMinX = x;
                    break;
                }
            }

            assertThat(minX).isEqualTo(expectedMinX);
        }
    }

    @Test
    void existsBlockCountOnY() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            Field initField = randoms.field(FIELD_HEIGHT, randoms.nextIntOpen(3, 10));

            for (int y = 0; y < FIELD_HEIGHT; y++) {
                boolean expected = false;
                for (int x = 0; x < FIELD_WIDTH; x++) {
                    if (!initField.isEmpty(x, y)) {
                        expected = true;
                    }
                }

                assertThat(initField.existsBlockCountOnY(y)).isEqualTo(expected);
            }
        }
    }

    @Test
    void deleteLine() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            // 適度にフィールドのラインが揃うようにランダムに地形を作る
            Field field = randoms.field(FIELD_HEIGHT, randoms.nextIntOpen(3, 10));

            int maxCount = randoms.nextIntOpen(0, FIELD_HEIGHT * 2);
            for (int lineCount = 0; lineCount < maxCount; lineCount++) {
                field.fillLine(randoms.nextIntClosed(0, FIELD_HEIGHT));
            }

            Field expected = field.freeze();
            long deletedKey = expected.clearLineReturnKey();

            field.deleteLineWithKey(deletedKey);

            assertThat(field).isEqualTo(expected);
        }
    }

    @Test
    void mask() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            // 適度にフィールドのラインが揃うようにランダムに地形を作る
            Field field1 = randoms.field(FIELD_HEIGHT, randoms.nextIntOpen(3, 10));
            Field field2 = randoms.field(FIELD_HEIGHT, randoms.nextIntOpen(3, 10));

            // 期待値
            Field expected = FieldFactory.createField(field1.getMaxFieldHeight());
            for (int y = 0; y < FIELD_HEIGHT; y++) {
                for (int x = 0; x < FIELD_WIDTH; x++) {
                    if (!field1.isEmpty(x, y) && !field2.isEmpty(x, y)) {
                        expected.setBlock(x, y);
                    }
                }
            }

            {
                Field freeze = field1.freeze();
                freeze.mask(field2);
                assertThat(freeze).isEqualTo(expected);
            }

            {
                Field freeze = field2.freeze();
                freeze.mask(field1);
                assertThat(freeze).isEqualTo(expected);
            }
        }
    }

    @Test
    void getUsingKey() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            Field field = randoms.field(FIELD_HEIGHT, randoms.nextIntOpen(1, 10));

            // 期待値
            long expected = 0L;
            for (int y = 0; y < FIELD_HEIGHT; y++) {
                for (int x = 0; x < FIELD_WIDTH; x++) {
                    if (field.exists(x, y)) {
                        expected |= KeyOperators.getDeleteBitKey(y);
                        break;
                    }
                }
            }

            assertThat(field.getUsingKey()).isEqualTo(expected);
        }
    }
}