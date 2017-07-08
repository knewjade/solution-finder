package common.datastore;

import core.mino.Block;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OperationsTest {
    @Test
    public void create() throws Exception {
        List<SimpleOperation> list = Arrays.asList(
                new SimpleOperation(Block.I, Rotate.Left, 0, 1),
                new SimpleOperation(Block.L, Rotate.Spawn, 2, 0),
                new SimpleOperation(Block.O, Rotate.Spawn, 1, 1),
                new SimpleOperation(Block.J, Rotate.Reverse, 2, 3)
        );
        Operations operations = new Operations(new ArrayList<>(list));
        assertThat(operations.getOperations(), contains(list.toArray()));
    }

    @Test
    public void compareRandom() throws Exception {
        for (int count = 0; count < 1000; count++) {
            Randoms randoms = new Randoms();
            List<Operation> list1 = Stream.generate(() -> createRandomOperation(randoms))
                    .limit(10L)
                    .collect(Collectors.toList());
            Operations operations1 = new Operations(list1);

            List<Operation> list2 = Stream.generate(() -> createRandomOperation(randoms))
                    .limit(10L)
                    .collect(Collectors.toList());
            Operations operations2 = new Operations(list2);

            assertThat(operations1.compareTo(new Operations(list1)), is(0));
            assertThat(operations2.compareTo(new Operations(list2)), is(0));

            if (list1.equals(list2))
                assertThat(operations1.compareTo(new Operations(list2)), is(0));
            else  // assert is not 0 & sign reversed
                assertThat(operations1.toString(), operations1.compareTo(operations2) * operations2.compareTo(operations1), is(lessThan(0)));
        }
    }

    private Operation createRandomOperation(Randoms randoms) {
        Block block = randoms.block();
        Rotate rotate = randoms.rotate();
        int x = randoms.nextInt(10);
        int y = randoms.nextInt(20);
        return new SimpleOperation(block, rotate, x, y);
    }
}