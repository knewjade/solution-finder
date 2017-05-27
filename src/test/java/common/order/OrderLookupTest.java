package common.order;

import core.mino.Block;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OrderLookupTest {
    @Test
    public void reverseWithJustBlocks() throws Exception {
        List<Block> blocks = Arrays.asList(T, I, O, S, Z, J, L, T, I, O, S, Z, J, L);
        for (int depth = 1; depth < blocks.size(); depth++) {
            ArrayList<ListPieces> reverse = OrderLookup.reverse(blocks.subList(0, depth), depth);
            assertThat(reverse.size(), is((int) Math.pow(2, depth - 1)));
        }
    }

    @Test
    public void reverseWithOverBlocks() throws Exception {
        List<Block> blocks = Arrays.asList(T, I, O, S, Z, J, L, T, I, O, S, Z, J, L);
        for (int depth = 1; depth < blocks.size(); depth++) {
            ArrayList<ListPieces> reverse = OrderLookup.reverse(blocks.subList(0, depth), depth + 1);
            assertThat(reverse.size(), is((int) Math.pow(2, depth)));
        }
    }

    @Test
    public void reverseWithJustBlocks2() throws Exception {
        List<Block> blocks = Arrays.asList(O, J, L, T, I, S, Z);
        for (int depth = 1; depth < blocks.size(); depth++) {
            ArrayList<ListPieces> reverse = OrderLookup.reverse(blocks.subList(0, depth), depth + 1);
            assertThat(reverse.size(), is((int) Math.pow(2, depth)));
        }
    }

    @Test
    public void forwardWithJustBlocks() throws Exception {
        List<Block> blocks = Arrays.asList(T, I, O, S, Z, J, L, T, I, O, S, Z, J, L);
        for (int depth = 2; depth < blocks.size(); depth++) {
            ArrayList<ListPieces> forward = OrderLookup.forward(blocks.subList(0, depth), depth);
            assertThat(forward.size(), is((int) Math.pow(2, depth - 1)));
        }
    }
}