package searcher.common.validator;

import core.field.Field;
import core.field.MiddleField;
import core.field.SmallField;

// TODO: unittest
public class BuildValidator implements Validator {
    private final Field expectedField;

    public BuildValidator(Field expectedField) {
        this.expectedField = expectedField;
    }

    @Override
    public boolean satisfies(Field field, int maxY) {
        for (int index = 0, size = field.getBoardCount(); index < size; index++)
            if (expectedField.getBoard(index) != field.getBoard(index))
                return false;
        return true;
    }

    @Override
    public boolean validate(Field field, int maxY) {
        if (field.existsAbove(maxY))
            return false;

        for (int index = 0, size = field.getBoardCount(); index < size; index++)
            if ((~expectedField.getBoard(index) & field.getBoard(index)) != 0L)
                return false;

        int boardCount = field.getBoardCount();
        Field newField;
        if (boardCount == 1) {
            long reverse = ~expectedField.getBoard(0);
            long board = field.getBoard(0);
            if ((reverse & board) != 0L)
                return false;

            newField = new SmallField(reverse | board);
        } else if (boardCount == 2) {
            long reverseLow = ~expectedField.getBoard(0);
            long boardLow = field.getBoard(0);
            if ((reverseLow & boardLow) != 0L)
                return false;

            long reverseHigh = ~expectedField.getBoard(1);
            long boardHigh = field.getBoard(1);
            if ((reverseHigh & boardHigh) != 0L)
                return false;

            newField = new MiddleField(
                    reverseLow | boardLow,
                    reverseHigh | boardHigh
            );
        } else {
            throw new UnsupportedOperationException();
        }

        int sum = maxY - newField.getBlockCountBelowOnX(0, maxY);
        for (int x = 1; x < FIELD_WIDTH; x++) {
            int emptyCountInColumn = maxY - newField.getBlockCountBelowOnX(x, maxY);
            if (newField.isWallBetweenLeft(x, maxY)) {
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
