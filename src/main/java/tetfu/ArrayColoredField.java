package tetfu;

import core.mino.Block;
import core.mino.Mino;

public class ArrayColoredField implements ColoredField {
    public static final int FIELD_WIDTH = 10;
    private final short[][] field;

    public ArrayColoredField(int maxHeight) {
        this.field = new short[maxHeight][FIELD_WIDTH];
    }

    public ArrayColoredField(ArrayColoredField coloredField) {
        this.field = coloredField.field;
    }

    @Override
    public ColoredField freeze(int maxHeight) {
        return new ArrayColoredField(this);
    }

    @Override
    public int getBlockNumber(int x, int y) {
        return field[y][x];
    }

    @Override
    public void setBlock(Block block, int x, int y) {
        field[y][x] = (short) block.getNumber();
    }

    @Override
    public void putMino(Mino mino, int x, int y) {
        Block block = mino.getBlock();
        for (int[] positions : mino.getPositions())
            setBlock(block, x + positions[0], y + positions[1]);
    }

    @Override
    public void setBlockNumber(int x, int y, int number) {
        field[y][x] = (short) number;
    }
}
