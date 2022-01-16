package searcher.common.validator;

import core.field.Field;

// TODO: unittest
public class PerfectValidator implements Validator {
    @Override
    public boolean satisfies(Field field, int maxY) {
        return maxY == 0 && field.isEmpty();
    }

    @Override
    public boolean validate(Field field, int maxClearLine) {
        if (field.existsAbove(maxClearLine))
            return false;

        int sum = maxClearLine - field.getBlockCountBelowOnX(0, maxClearLine);
        for (int x = 1; x < FIELD_WIDTH; x++) {
            int emptyCountInColumn = maxClearLine - field.getBlockCountBelowOnX(x, maxClearLine);
            if (field.isWallBetweenLeft(x, maxClearLine)) {
                if (sum % 4 != 0)
                    return false;
                sum = emptyCountInColumn;
            } else {
                sum += emptyCountInColumn;
            }
        }

        return sum % 4 == 0;
    }
}
