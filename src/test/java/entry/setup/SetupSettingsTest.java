package entry.setup;

import com.google.common.collect.Lists;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import entry.setup.operation.FieldOperation;
import exceptions.FinderParseException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SetupSettingsTest {
    @Test
    void setAddOperations1() throws FinderParseException {
        SetupSettings settings = new SetupSettings();
        settings.setAddOperations(Lists.newArrayList("T-2(2,1)", "s-0(4, 0)", "I-LEFT(9,1)", "row(2)"));

        Field field = FieldFactory.createField(4);
        for (FieldOperation operation : settings.getAddOperations()) {
            operation.operate(field);
        }

        assertThat(field)
                .as(FieldView.toString(field))
                .isEqualTo(FieldFactory.createField("" +
                        "_________X" +
                        "XXXXXXXXXX" +
                        "_XXXXX___X" +
                        "__XXX____X" +
                        ""
                ));
    }

    @Test
    void setAddOperations1_2() throws FinderParseException {
        SetupSettings settings = new SetupSettings();
        settings.setAddOperations(Lists.newArrayList("L-r (0,1)", "Row (1)", "clear()", "block(1, 1)"));

        Field field = FieldFactory.createField(4);
        for (FieldOperation operation : settings.getAddOperations()) {
            operation.operate(field);
        }

        assertThat(field)
                .as(FieldView.toString(field))
                .isEqualTo(FieldFactory.createField("" +
                        "XX________" +
                        "XX________" +
                        ""
                ));
    }

    @Test
    void setAddOperations2() {
        // row < 0
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("row(-1)");
        assertThrows(FinderParseException.class, () -> settings.setAddOperations(values));
    }

    @Test
    void setAddOperations3x() {
        // x < 0
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("I-right(-1, 3)");
        assertThrows(FinderParseException.class, () -> settings.setAddOperations(values));
    }

    @Test
    void setAddOperations3x2() {
        // 10 <= x
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("I-right(10, 3)");
        assertThrows(FinderParseException.class, () -> settings.setAddOperations(values));
    }

    @Test
    void setAddOperations3y() {
        // y < 0
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("I-right(1, -1)");
        assertThrows(FinderParseException.class, () -> settings.setAddOperations(values));
    }

    @Test
    void setAddOperations4() {
        // x is String
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("I-right(a, 3)");
        assertThrows(FinderParseException.class, () -> settings.setAddOperations(values));
    }

    @Test
    void setAddOperations5() {
        // y is String
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("I-right(4, b)");
        assertThrows(FinderParseException.class, () -> settings.setAddOperations(values));
    }

    @Test
    void setAddOperations6() {
        // Unexpected piece
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("K(4, b)");
        assertThrows(FinderParseException.class, () -> settings.setAddOperations(values));
    }

    @Test
    void setAddOperations7() {
        // Unexpected rotation
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("I-dummy(4, b)");
        assertThrows(FinderParseException.class, () -> settings.setAddOperations(values));
    }

    @Test
    void setAddOperations8() {
        // Unexpected rotation
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("I-(4, b)");
        assertThrows(FinderParseException.class, () -> settings.setAddOperations(values));
    }

    @Test
    void setAddOperations9() {
        // No rotation
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("I(4, b)");
        assertThrows(FinderParseException.class, () -> settings.setAddOperations(values));
    }

    @Test
    void setAddOperations10() {
        // x < 0
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("block(-3, 1)");
        assertThrows(FinderParseException.class, () -> settings.setAddOperations(values));
    }

    @Test
    void setAddOperations11() {
        // y < 0
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("I-right(1, -2)");
        assertThrows(FinderParseException.class, () -> settings.setAddOperations(values));
    }
}