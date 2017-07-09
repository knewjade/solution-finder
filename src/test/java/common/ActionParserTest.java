package common;

import common.datastore.SimpleOperation;
import common.datastore.action.MinimalAction;
import core.mino.Block;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActionParserTest {
    @Test
    void parseToIntWithAction() throws Exception {
        assertThat(ActionParser.parseToInt(Block.T, MinimalAction.create(0, 0, Rotate.Spawn)))
                .isEqualTo(0);
        assertThat(ActionParser.parseToInt(Block.T, MinimalAction.create(5, 1, Rotate.Spawn)))
                .isEqualTo(15);
        assertThat(ActionParser.parseToInt(Block.T, MinimalAction.create(9, 10, Rotate.Right)))
                .isEqualTo(240 + 109);
        assertThat(ActionParser.parseToInt(Block.I, MinimalAction.create(2, 15, Rotate.Reverse)))
                .isEqualTo(960 + 480 + 152);
    }

    @Test
    void parseToInt() throws Exception {
        for (Block block : Block.values()) {
            for (Rotate rotate : Rotate.values()) {
                for (int y = 0; y < 24; y++) {
                    for (int x = 0; x < 10; x++) {
                        int expected = ActionParser.parseToInt(block, MinimalAction.create(x, y, rotate));
                        assertThat(ActionParser.parseToInt(block, rotate, x, y))
                                .isEqualTo(expected);
                    }
                }
            }
        }
    }

    @Test
    void parseToOperation() throws Exception {
        for (Block block : Block.values()) {
            for (Rotate rotate : Rotate.values()) {
                for (int y = 0; y < 24; y++) {
                    for (int x = 0; x < 10; x++) {
                        int value = ActionParser.parseToInt(block, rotate, x, y);
                        assertThat(ActionParser.parseToOperation(value))
                                .isEqualTo(new SimpleOperation(block, rotate, x, y));
                    }
                }
            }
        }
    }
}