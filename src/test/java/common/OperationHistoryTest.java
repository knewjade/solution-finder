package common;

import common.datastore.Operation;
import common.datastore.SimpleOperation;
import common.datastore.action.MinimalAction;
import core.mino.Piece;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class OperationHistoryTest {
    @Test
    void random() throws ExecutionException, InterruptedException {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 1000; count++) {
            int size = randoms.nextIntOpen(1, 10);

            ArrayList<Operation> operations = new ArrayList<>();
            OperationHistory history = new OperationHistory(size);

            for (int index = 0; index < size; index++) {
                Piece piece = randoms.block();
                Rotate rotate = randoms.rotate();
                int y = randoms.nextIntOpen(4);
                int x = randoms.nextIntOpen(10);
                MinimalAction action = MinimalAction.create(x, y, rotate);
                history = history.recordAndReturnNew(piece, action);

                operations.add(new SimpleOperation(piece, rotate, x, y));
            }

            List<Operation> actual = history.getOperationStream().collect(Collectors.toList());
            assertThat(actual).isEqualTo(operations);
        }
    }
}