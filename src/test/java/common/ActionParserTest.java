package common;

import common.datastore.SimpleOperation;
import common.datastore.action.MinimalAction;
import core.mino.Piece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActionParserTest {
    @Test
    void parseToIntWithAction() throws Exception {
        assertThat(ActionParser.parseToInt(Piece.T, MinimalAction.create(0, 0, Rotate.Spawn)))
                .isEqualTo(0);
        assertThat(ActionParser.parseToInt(Piece.T, MinimalAction.create(5, 1, Rotate.Spawn)))
                .isEqualTo(15);
        assertThat(ActionParser.parseToInt(Piece.T, MinimalAction.create(9, 10, Rotate.Right)))
                .isEqualTo(240 + 109);
        assertThat(ActionParser.parseToInt(Piece.I, MinimalAction.create(2, 15, Rotate.Reverse)))
                .isEqualTo(960 + 480 + 152);
    }

    @Test
    void parseToInt() throws Exception {
        for (Piece piece : Piece.values()) {
            for (Rotate rotate : Rotate.values()) {
                for (int y = 0; y < 24; y++) {
                    for (int x = 0; x < 10; x++) {
                        int expected = ActionParser.parseToInt(piece, MinimalAction.create(x, y, rotate));
                        assertThat(ActionParser.parseToInt(piece, rotate, x, y))
                                .isEqualTo(expected);
                    }
                }
            }
        }
    }

    @Test
    void parseToOperation() throws Exception {
        for (Piece piece : Piece.values()) {
            for (Rotate rotate : Rotate.values()) {
                for (int y = 0; y < 24; y++) {
                    for (int x = 0; x < 10; x++) {
                        int value = ActionParser.parseToInt(piece, rotate, x, y);
                        assertThat(ActionParser.parseToOperation(value))
                                .isEqualTo(new SimpleOperation(piece, rotate, x, y));
                    }
                }
            }
        }
    }
}