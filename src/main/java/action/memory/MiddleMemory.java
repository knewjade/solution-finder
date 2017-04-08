package action.memory;

public class MiddleMemory implements Memory {
    private static final int FIELD_ROW_BOARDER_Y = 6;

    private long flagsLow = 0L;
    private long flagsHigh = 0L;

    @Override
    public boolean get(int x, int y) {
        if (y < FIELD_ROW_BOARDER_Y)
            return (flagsLow & getMask(x, y)) != 0L;
        else
            return (flagsHigh & getMask(x, y - FIELD_ROW_BOARDER_Y)) != 0L;
    }

    @Override
    public void setTrue(int x, int y) {
        if (y < FIELD_ROW_BOARDER_Y)
            flagsLow |= getMask(x, y);
        else
            flagsHigh |= getMask(x, y - FIELD_ROW_BOARDER_Y);
    }

    private long getMask(int x, int y) {
        return 1L << x + y * FIELD_WIDTH;
    }

    @Override
    public void clear() {
        this.flagsLow = 0L;
        this.flagsHigh = 0L;
    }
}
