package searcher.common.validator;

import core.field.Field;
import core.field.FieldFactory;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class PerfectValidatorTest {
    @Test
    public void testValidate1() throws Exception {
        String marks = "" +
                "_X________" +
                "X_X_______" +
                "X_X_______" +
                "X_X_______" +
                "";
        Field field = FieldFactory.createSmallField(marks);

        PerfectValidator validator = new PerfectValidator();
        assertThat(validator.validate(field, 4), is(false));
    }
}