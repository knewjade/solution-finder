package newfield.step3;

import core.field.Field;
import newfield.ParityField;

public class LineCounterField {
    public static final int FIELD_WIDTH = 10;
    private final int[] counter;
    private final int[] parity;

    LineCounterField(Field field, int height) {
        this.counter = new int[height];
        for (int y = 0; y < height; y++)
            this.counter[y] = FIELD_WIDTH - field.getBlockCountOnY(y);

        ParityField parityField = new ParityField(field);
        this.parity = new int[]{
                height * 5 - parityField.calculateOddParity(),
                height * 5 - parityField.calculateEvenParity(),
        };
    }

    void decrease(int[][] yAndBlockCounts) {
        for (int[] counts : yAndBlockCounts)
            this.counter[counts[0]] -= counts[1];
    }

    void decrease(int[] parity) {
        assert parity.length == 2;
        this.parity[0] -= parity[0];
        this.parity[1] -= parity[1];
    }

    void increase(int[][] yAndBlockCounts) {
        for (int[] counts : yAndBlockCounts)
            this.counter[counts[0]] += counts[1];
    }

    void increase(int[] parity) {
        assert parity.length == 2;
        this.parity[0] += parity[0];
        this.parity[1] += parity[1];
    }

    boolean isValid() {
        if (this.parity[0] < 0 || this.parity[1] < 0)
            return false;

        for (int count : this.counter)
            if (count < 0)
                return false;
        return true;
    }
}
