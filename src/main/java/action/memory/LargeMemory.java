package action.memory;

/**
 * 高さ < 24であること
 */
public class LargeMemory implements Memory {
    private static final int FIELD_ROW_BOARDER_Y_LOW = 6;
    private static final int FIELD_ROW_BOARDER_Y_HALF = 12;
    private static final int FIELD_ROW_BOARDER_Y_HIGH = 18;

    private long flag0 = 0L;  // Low
    private long flag1 = 0L;
    private long flag2 = 0L;
    private long flag3 = 0L;  // High

    @Override
    public boolean get(int x, int y) {
        if (y < FIELD_ROW_BOARDER_Y_HALF) {
            if (y < FIELD_ROW_BOARDER_Y_LOW)
                return (flag0 & getMask(x, y)) != 0L;
            else
                return (flag1 & getMask(x, y - FIELD_ROW_BOARDER_Y_LOW)) != 0L;
        } else {
            if (y < FIELD_ROW_BOARDER_Y_HIGH)
                return (flag2 & getMask(x, y - FIELD_ROW_BOARDER_Y_HALF)) != 0L;
            else
                return (flag3 & getMask(x, y - FIELD_ROW_BOARDER_Y_HIGH)) != 0L;
        }
    }

    @Override
    public void setTrue(int x, int y) {
        if (y < FIELD_ROW_BOARDER_Y_HALF) {
            if (y < FIELD_ROW_BOARDER_Y_LOW)
                flag0 |= getMask(x, y);
            else
                flag1 |= getMask(x, y - FIELD_ROW_BOARDER_Y_LOW);
        } else {
            if (y < FIELD_ROW_BOARDER_Y_HIGH)
                flag2 |= getMask(x, y - FIELD_ROW_BOARDER_Y_HALF);
            else
                flag3 |= getMask(x, y - FIELD_ROW_BOARDER_Y_HIGH);
        }
    }

    private long getMask(int x, int y) {
        return 1L << x + y * FIELD_WIDTH;
    }

    @Override
    public void clear() {
        this.flag0 = 0L;
        this.flag1 = 0L;
        this.flag2 = 0L;
        this.flag3 = 0L;
    }
}
