package _experimental.putter;

import core.field.Field;
import core.field.FieldView;
import searcher.common.validator.PerfectValidator;
import searcher.common.validator.Validator;

public class SetupValidator implements Validator {
    private final PerfectValidator perfectValidator;
    private final int expectedEmptyBlocks;

    SetupValidator(int expectedEmptyBlocks) {
        this.expectedEmptyBlocks = expectedEmptyBlocks;
        this.perfectValidator = new PerfectValidator();
    }

    @Override
    public boolean satisfies(Field field, int maxY) {
        return maxY * 10 - field.getNumOfAllBlocks() == expectedEmptyBlocks;
    }

    @Override
    public boolean validate(Field field, int maxClearLine) {
        return perfectValidator.validate(field, maxClearLine);
    }
}
