package common.order;

import core.mino.Block;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class LongStackOrderTest {
    @Test
    public void add() throws Exception {
        LongStackOrder stackOrder = new LongStackOrder();
        stackOrder.addLast(Block.S);
        stackOrder.addLast(Block.Z);
        stackOrder.addLast(Block.O);
        stackOrder.addLastTwo(Block.I);

        List<Block> blocks = stackOrder.toList();
        assertThat(blocks, is(Arrays.asList(Block.S, Block.Z, Block.I, Block.O)));
    }

    @Test
    public void stock() throws Exception {
        LongStackOrder stackOrder = new LongStackOrder();
        stackOrder.addLast(Block.S);
        stackOrder.stock(Block.T);  // to head and memory TS*
        stackOrder.addLast(Block.Z);
        stackOrder.addLastTwo(Block.O);
        stackOrder.stock(Block.I);
        stackOrder.stock(null);

        List<Block> blocks = stackOrder.toList();
        assertThat(blocks, is(Arrays.asList(Block.T, Block.S, Block.I, Block.O, Block.Z, null)));
    }
}