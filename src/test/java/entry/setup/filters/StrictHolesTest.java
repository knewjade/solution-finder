package entry.setup.filters;

import core.field.Field;
import core.field.FieldFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StrictHolesTest {
    @Test
    void fillHeight5() {
        Field field = FieldFactory.createField("" +
                "__________" +
                "____X_____" +
                "____X_____" +
                "____X_____" +
                "____X_____" +
                "____X_____"
        );

        Field fill = StrictHoles.fill(field, 5);
        Field expected = FieldFactory.createField("" +
                "__________" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX"
        );

        assertThat(fill).isEqualTo(expected);
    }

    @Test
    void fillHeight10() {
        Field field = FieldFactory.createField("" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "______X__X" +
                "X______XX_" +
                "_X____X___"
        );

        Field fill = StrictHoles.fill(field, 10);
        Field expected = FieldFactory.createField("" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXX_" +
                "_XXXXXX___"
        );

        assertThat(fill).isEqualTo(expected);
    }
}