package common.datastore;

import core.mino.Block;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class BlockCounterTest {
    @Test
    public void testEmpty() throws Exception {
        List<Block> emptyList = Collections.emptyList();
        BlockCounter counter = new BlockCounter(emptyList);
        assertThat(counter.getCounter(), is(0L));
    }

    @Test
    public void testAdd() throws Exception {
        BlockCounter counter = new BlockCounter(Arrays.asList(Block.I, Block.J));
        BlockCounter actual = counter.add(Collections.singletonList(Block.T));

        assertThat(counter.getCounter(), is(new BlockCounter(Arrays.asList(Block.I, Block.J)).getCounter()));
        assertThat(actual.getCounter(), is(new BlockCounter(Arrays.asList(Block.I, Block.T, Block.J)).getCounter()));
    }
}