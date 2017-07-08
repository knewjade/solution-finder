package lib;

import core.mino.Block;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class RandomsTest {
    @Test
    public void nextInt() throws Exception {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            int next = randoms.nextInt(3, 19);
            assertThat(next, is(greaterThanOrEqualTo(3)));
            assertThat(next, is(lessThan(19)));
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

    @Test
    public void choose() throws Exception {
        Randoms randoms = new Randoms();
        List<Integer> bag = IntStream.range(1, 100).boxed().collect(Collectors.toList());
        for (int count = 0; count < 10000; count++) {
            Integer choose = randoms.choose(bag);
            assertThat(choose, isIn(bag));
        }
    }

    @Test
    public void combinations() throws Exception {
        Randoms randoms = new Randoms();
        List<Integer> bag = IntStream.range(1, 100).boxed().collect(Collectors.toList());
        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 15);
            List<Integer> combinations = randoms.combinations(bag, size);
            assertThat(combinations.toString(), combinations, hasSize(size));
            assertThat(combinations.toString(), Collections.unmodifiableList(bag), hasItems(combinations.toArray()));
            assertThat(combinations.toString(), new HashSet<>(combinations), hasSize(size));
        }
    }
}