package searcher.common.validator;

import core.field.Field;
import core.field.FieldFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PerfectValidatorTest {
    private final PerfectValidator validator = new PerfectValidator();

    @Test
    void testValidate1() throws Exception {
        String marks = "" +
                "_X________" +
                "X_X_______" +
                "X_X_______" +
                "X_X_______" +
                "";
        Field field = FieldFactory.createSmallField(marks);


        assertThat(validator.validate(field, 4)).isFalse();
    }

    @Test
    void testValidate2() throws Exception {
        String marks = "" +
                "__________" +
                "__________" +
                "____XX____" +
                "___XX_____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(validator.validate(field, 4)).isTrue();
    }

    @Test
    void testValidate3() throws Exception {
        String marks = "" +
                "____XX____" +
                "___XX_____" +
                "____XX____" +
                "___XX_____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(validator.validate(field, 4)).isFalse();
    }

    @Test
    void testValidate4() throws Exception {
        String marks = "" +
                "_____XX___" +
                "____XX____" +
                "____XX____" +
                "___XX_____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        assertThat(validator.validate(field, 4)).isTrue();
    }

    @Test
    void testValidate5() throws Exception {
        String marks = "" +
                "______XX__" +
                "_____XX___" +
                "____XX____" +
                "___XX_____" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        // this is no possible perfect, but validator return True
        assertThat(validator.validate(field, 4)).isTrue();
    }
}