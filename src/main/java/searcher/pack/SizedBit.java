package searcher.pack;

import core.field.Field;
import core.field.FieldFactory;

public class SizedBit {
    private final int width;
    private final int height;
    private final int maxBitDigit;
    private final long fillBoard;
    private final Field fillField;

    public SizedBit(int width, int height) {
        this.width = width;
        this.height = height;
        this.maxBitDigit = height * width;
        this.fillBoard = (1L << getMaxBitDigit()) - 1L;

        {
            Field field = FieldFactory.createField(height);
            for (int y = 0; y < height; y++)
                for (int x = 0; x < 10; x++)
                    field.setBlock(x, y);
            this.fillField = field;
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getMaxBitDigit() {
        return maxBitDigit;
    }

    public long getFillBoard() {
        return fillBoard;
    }

    public Field getFillField() {
        return fillField.freeze(height);
    }
}
