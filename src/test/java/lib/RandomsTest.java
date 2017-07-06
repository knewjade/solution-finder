package lib;

import core.mino.Block;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class RandomsTest {
    @Test
    public void nextInt() throws Exception {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            int next = randoms.nextInt(3, 19);
            assertThat(next, is(Matchers.greaterThanOrEqualTo(3)));
            assertThat(next, is(Matchers.lessThan(19)));
        }
    }

    @Test
    public void blocks() throws Exception {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(3, 19);
            List<Block> blocks = randoms.blocks(size);
            assertThat(blocks, hasSize(size));
            assertThat(blocks, is(allOf(notNullValue())));
        }
    }
}