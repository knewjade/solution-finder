package tetfu.field;

import core.mino.Block;
import core.mino.Mino;
import tetfu.common.ColorConverter;
import tetfu.common.ColorType;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayColoredField implements ColoredField {
    public static final int FIELD_WIDTH = 10;
    private static final ColorConverter converter = new ColorConverter();
    public static final int EMPTY_NUMBER = ColorType.Empty.getNumber();

    private final short[][] field;

    public ArrayColoredField(int maxHeight) {
        this.field = new short[maxHeight][FIELD_WIDTH];
    }

    private ArrayColoredField(short[][] field) {
        this.field = field;
    }

    @Override
    public ColoredField freeze(int maxHeight) {
        short[][] newField = new short[maxHeight][];
        for (int y = 0; y < newField.length; y++)
            newField[y] = Arrays.copyOf(this.field[y], FIELD_WIDTH);
        return new ArrayColoredField(newField);
    }

    @Override
    public int getBlockNumber(int x, int y) {
        return field[y][x];
    }

    @Override
    public void putMino(Mino mino, int x, int y) {
        Block block = mino.getBlock();
        ColorType type = converter.parseToColorType(block);
        for (int[] positions : mino.getPositions())
            setColorType(type, x + positions[0], y + positions[1]);
    }

    @Override
    public void setBlockNumber(int x, int y, int number) {
        field[y][x] = (short) number;
    }

    @Override
    public void setColorType(ColorType colorType, int x, int y) {
        field[y][x] = (short) colorType.getNumber();
    }

    @Override
    public void clearLine() {
        int currentY = 0;
        for (int y = 0; y < field.length; y++) {
            for (int x = 0; x < FIELD_WIDTH; x++) {
                if (field[y][x] == EMPTY_NUMBER) {
                    if (currentY != y)
                        System.arraycopy(field[y], 0, field[currentY], 0, FIELD_WIDTH);
                    currentY += 1;
                    break;
                }
            }
        }

        for (int y = currentY; y < field.length; y++)
            Arrays.fill(field[y], (short) 0);
    }

    @Override
    public void blockUp() {
        short[] temp = field[field.length - 1];
        for (int y = field.length - 1; 0 < y; y--)
            field[y] = field[y - 1];
        field[0] = temp;
        Arrays.fill(field[0], (short) 0);
    }

    @Override
    public void mirror() {
        for (int y = 0; y < field.length; y++) {
            for (int x = 0; x < FIELD_WIDTH / 2; x++) {
                int reverseX = FIELD_WIDTH - x - 1;
                short temp = field[y][x];
                field[y][x] = field[y][reverseX];
                field[y][reverseX] = temp;
            }
        }
    }
}
