package searcher.spins.fill.line;

import core.field.Field;
import core.field.KeyOperators;

class SlidedField {
    static SlidedField create(Field field, SlidedField prevSlideField) {
        Field freeze = field.freeze();

        long filledLine = prevSlideField.filledLine;
        freeze.deleteLineWithKey(filledLine);

        int slideY = prevSlideField.slideDownY;
        slide(freeze, slideY);

        return new SlidedField(freeze, filledLine, slideY);
    }

    static SlidedField create(Field field, int targetY) {
        Field freeze = field.freeze();
        long filledLine = freeze.clearLineReturnKey();

        int bitCount = Long.bitCount(filledLine & KeyOperators.getMaskForKeyBelowY(targetY));

        int slideDownY = (targetY - bitCount) - 3;

        slide(freeze, slideDownY);

        return new SlidedField(freeze, filledLine, slideDownY);
    }

    static void slide(Field field, int slideDownY) {
        if (0 < slideDownY) {
            field.slideDown(slideDownY);
        } else if (slideDownY < 0) {
            field.slideUpWithBlackLine(-slideDownY);
        }
    }

    private final Field field;
    private final long filledLine;
    private final int slideDownY;

    private SlidedField(Field field, long filledLine, int slideDownY) {
        this.field = field;
        this.filledLine = filledLine;
        this.slideDownY = slideDownY;
    }

    Field getField() {
        return field;
    }

    int getSlideDownY() {
        return slideDownY;
    }

    long getFilledLine() {
        return filledLine;
    }
}
