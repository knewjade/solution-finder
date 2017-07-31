package common.order;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerListStackOrderTest {
    @Test
    void add() throws Exception {
        IntegerListStackOrder stackOrder = new IntegerListStackOrder();
        stackOrder.addLast(0);
        stackOrder.addLast(1);
        stackOrder.addLast(2);
        stackOrder.addLastTwo(-1);

        List<Integer> blocks = stackOrder.toList();
        assertThat(blocks).isEqualTo(Arrays.asList(0, 1, -1, 2));
    }

    @Test
    void stock() throws Exception {
        IntegerListStackOrder stackOrder = new IntegerListStackOrder();
        stackOrder.addLast(0);
        stackOrder.stock(1);  // to head and memory TS*
        stackOrder.addLast(2);
        stackOrder.addLastTwo(3);
        stackOrder.stock(4);
        stackOrder.stock(-1);

        List<Integer> blocks = stackOrder.toList();
        assertThat(blocks).isEqualTo(Arrays.asList(1, 0, 4, 3, 2, -1));
    }
}