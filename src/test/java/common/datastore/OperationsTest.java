package common.datastore;

import core.mino.Piece;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OperationsTest {
    @Test
    void create() throws Exception {
        List<SimpleOperation> list = Arrays.asList(
                new SimpleOperation(Piece.I, Rotate.Left, 0, 1),
                new SimpleOperation(Piece.L, Rotate.Spawn, 2, 0),
                new SimpleOperation(Piece.O, Rotate.Spawn, 1, 1),
                new SimpleOperation(Piece.J, Rotate.Reverse, 2, 3)
        );
        Operations operations = new Operations(new ArrayList<>(list));
        assertThat(operations.getOperations()).isEqualTo(list);
    }

    @Test
    void compareRandom() throws Exception {
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

            assertThat(operations1.compareTo(new Operations(list1))).isEqualTo(0);
            assertThat(operations2.compareTo(new Operations(list2))).isEqualTo(0);

            if (list1.equals(list2))
                assertThat(operations1.compareTo(new Operations(list2))).isEqualTo(0);
            else  // assert is not 0 & sign reversed
                assertThat(operations1.compareTo(operations2) * operations2.compareTo(operations1))
                        .as(operations1.toString())
                        .isLessThan(0);
        }
    }

    private Operation createRandomOperation(Randoms randoms) {
        Piece piece = randoms.block();
        Rotate rotate = randoms.rotate();
        int x = randoms.nextIntOpen(10);
        int y = randoms.nextIntOpen(20);
        return new SimpleOperation(piece, rotate, x, y);
    }
}