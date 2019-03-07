package searcher.spins.fill.line;

import core.field.Field;
import core.field.FieldFactory;
import core.field.KeyOperators;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlidedFieldTest {
    @Test
    void case1() {
        Field field = FieldFactory.createField("" +
                "XXXXX_____" +
                "XXXXX_____"
        );
        SlidedField slidedField = SlidedField.create(field, 1, field.getFilledLine());
        assertThat(slidedField)
                .returns(FieldFactory.createField("" +
                        "XXXXX_____" +
                        "XXXXX_____" +
                        "XXXXXXXXXX" +
                        "XXXXXXXXXX"
                ), SlidedField::getField)
                .returns(-2, SlidedField::getSlideDownY)
                .returns(0L, SlidedField::getFilledLine)
        ;
    }

    @Test
    void case2() {
        Field field = FieldFactory.createField("" +
                "X_________" +
                "XXXXXXXXXX" +
                "XX________" +
                "XXXXXXXXXX" +
                "XXX_______" +
                "XXXX______"
        );
        SlidedField slidedField = SlidedField.create(field, 5, field.getFilledLine());
        assertThat(slidedField)
                .returns(FieldFactory.createField("" +
                        "X_________" +
                        "XX________" +
                        "XXX_______" +
                        "XXXX______"
                ), SlidedField::getField)
                .returns(0, SlidedField::getSlideDownY)
                .returns(KeyOperators.getBitKeys(2, 4), SlidedField::getFilledLine)
        ;
    }

    @Test
    void case3() {
        Field field = FieldFactory.createField("" +
                "X_________" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX"
        );
        SlidedField slidedField = SlidedField.create(field, 2, field.getFilledLine());
        assertThat(slidedField)
                .returns(FieldFactory.createField("" +
                        "X_________" +
                        "XXXXXXXXXX" +
                        "XXXXXXXXXX" +
                        "XXXXXXXXXX"
                ), SlidedField::getField)
                .returns(-3, SlidedField::getSlideDownY)
                .returns(KeyOperators.getBitKeys(0, 1), SlidedField::getFilledLine)
        ;
    }
}