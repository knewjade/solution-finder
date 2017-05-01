package newfield.step3;

import core.field.Field;

public class LineCounterField {
    public static final int FIELD_WIDTH = 10;
    private final int[] counter;

    LineCounterField(Field field, int height) {
        this.counter = new int[height];
        for (int y = 0; y < height; y++)
            this.counter[y] = FIELD_WIDTH - field.getBlockCountOnY(y);
    }

    void decrease(int[][] yAndBlockCounts) {
        for (int[] counts : yAndBlockCounts)
            this.counter[counts[0]] -= counts[1];
    }

    void increase(int[][] yAndBlockCounts) {
        for (int[] counts : yAndBlockCounts)
            this.counter[counts[0]] += counts[1];
    }

    boolean isValid() {
        for (int count : counter)
            if (count < 0)
                return false;
        return true;
    }
}
