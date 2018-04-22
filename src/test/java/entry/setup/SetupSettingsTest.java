package entry.setup;

import com.google.common.collect.Lists;
import common.datastore.SimpleOperation;
import core.mino.Piece;
import core.srs.Rotate;
import exceptions.FinderParseException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SetupSettingsTest {
    @Test
    void setAddOperations1() throws FinderParseException {
        SetupSettings settings = new SetupSettings();
        settings.setAddOperations(Lists.newArrayList("T-Spawn(1,2)", "j(0,0)", "O-Left(9,3)"));
        assertThat(settings.getAddOperations())
                .hasSize(3)
                .contains(new SimpleOperation(Piece.T, Rotate.Spawn, 1, 2))
                .contains(new SimpleOperation(Piece.J, Rotate.Spawn, 0, 0))
                .contains(new SimpleOperation(Piece.O, Rotate.Left, 9, 3));
    }

    @Test
    void setAddOperations2() throws FinderParseException {
        SetupSettings settings = new SetupSettings();
        settings.setAddOperations(Lists.newArrayList("I-right(2, 3)", "t-2(3, 4)"));
        assertThat(settings.getAddOperations())
                .hasSize(2)
                .contains(new SimpleOperation(Piece.I, Rotate.Right, 2, 3))
                .contains(new SimpleOperation(Piece.T, Rotate.Reverse, 3, 4));
    }

    @Test
    void setAddOperations3() {
        // x < 0
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("I-right(-1, 3)");
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
        // Unexpected rotate
        SetupSettings settings = new SetupSettings();
        ArrayList<String> values = Lists.newArrayList("I-dummy(4, b)");
        assertThrows(FinderParseException.class, () -> settings.setAddOperations(values));
    }
}