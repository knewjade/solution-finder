package common.order;

import core.mino.Block;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LongStackOrderTest {
    @Test
    void add() throws Exception {
        LongStackOrder stackOrder = new LongStackOrder();
        stackOrder.addLast(Block.S);
        stackOrder.addLast(Block.Z);
        stackOrder.addLast(Block.O);
        stackOrder.addLastTwo(Block.I);

        List<Block> blocks = stackOrder.toList();
        assertThat(blocks).isEqualTo(Arrays.asList(Block.S, Block.Z, Block.I, Block.O));
    }

    @Test
    void stock() throws Exception {
        LongStackOrder stackOrder = new LongStackOrder();
        stackOrder.addLast(Block.S);
        stackOrder.stock(Block.T);  // to head and memory TS*
        stackOrder.addLast(Block.Z);
        stackOrder.addLastTwo(Block.O);
        stackOrder.stock(Block.I);
        stackOrder.stock(null);

        List<Block> blocks = stackOrder.toList();
        assertThat(blocks).isEqualTo(Arrays.asList(Block.T, Block.S, Block.I, Block.O, Block.Z, null));
    }
}