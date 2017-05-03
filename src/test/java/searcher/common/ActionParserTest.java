package searcher.common;

import common.datastore.Operation;
import core.mino.Block;
import core.srs.Rotate;
import org.junit.Test;
import common.datastore.action.MinimalAction;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ActionParserTest {
    @Test
    public void parseToIntWithAction() throws Exception {
        assertThat(ActionParser.parseToInt(Block.T, MinimalAction.create(0, 0, Rotate.Spawn)), is(0));
        assertThat(ActionParser.parseToInt(Block.T, MinimalAction.create(5, 1, Rotate.Spawn)), is(15));
        assertThat(ActionParser.parseToInt(Block.T, MinimalAction.create(9, 10, Rotate.Right)), is(240 + 109));
        assertThat(ActionParser.parseToInt(Block.I, MinimalAction.create(2, 15, Rotate.Reverse)), is(960 + 480 + 152));
    }

    @Test
    public void parseToInt() throws Exception {
        for (Block block : Block.values()) {
            for (Rotate rotate : Rotate.values()) {
                for (int y = 0; y < 24; y++) {
                    for (int x = 0; x < 10; x++) {
                        int expected = ActionParser.parseToInt(block, MinimalAction.create(x, y, rotate));
                        assertThat(ActionParser.parseToInt(block, rotate, x, y), is(expected));
                    }
                }
            }
        }
    }

    @Test
    public void parseToOperation() throws Exception {
        for (Block block : Block.values()) {
            for (Rotate rotate : Rotate.values()) {
                for (int y = 0; y < 24; y++) {
                    for (int x = 0; x < 10; x++) {
                        int value = ActionParser.parseToInt(block, rotate, x, y);
                        assertThat(ActionParser.parseToOperation(value), is(new Operation(block, rotate, x, y)));
                    }
                }
            }
        }
    }
}