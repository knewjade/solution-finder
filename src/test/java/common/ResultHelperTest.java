package common;

import common.datastore.Result;
import common.datastore.action.MinimalAction;
import common.datastore.order.Order;
import core.field.Field;
import core.mino.Piece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResultHelperTest {
    private static class DummyOrder implements Order {
        private final Piece hold;

        private DummyOrder(Piece hold) {
            this.hold = hold;
        }

        @Override
        public Piece getHold() {
            return hold;
        }

        @Override
        public Field getField() {
            return null;
        }

        @Override
        public OperationHistory getHistory() {
            return null;
        }

        @Override
        public int getMaxClearLine() {
            return 0;
        }
    }

    @Test
    void uniquify() {
        Result result1 = new Result(new DummyOrder(Piece.O), Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L);
        Result result2 = new Result(new DummyOrder(Piece.O), Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L);
        Result result3 = new Result(new DummyOrder(Piece.O), Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L);
        List<Result> uniquify = ResultHelper.uniquify(Arrays.asList(result1, result2, result3));
        assertThat(uniquify).hasSize(1);
    }

    @Test
    void uniquifyDiff() {
        List<Result> results = Arrays.asList(
                new Result(new DummyOrder(Piece.O), Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L),
                new Result(new DummyOrder(Piece.O), Piece.S, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L),
                new Result(new DummyOrder(Piece.O), Piece.I, MinimalAction.create(1, 1, Rotate.Spawn), Piece.L),
                new Result(new DummyOrder(Piece.O), Piece.I, MinimalAction.create(0, 2, Rotate.Spawn), Piece.L),
                new Result(new DummyOrder(Piece.O), Piece.I, MinimalAction.create(0, 1, Rotate.Left), Piece.L),
                new Result(new DummyOrder(Piece.O), Piece.I, MinimalAction.create(0, 1, Rotate.Left), Piece.J),
                new Result(new DummyOrder(Piece.T), Piece.I, MinimalAction.create(0, 1, Rotate.Spawn), Piece.L)
        );
        List<Result> uniquify = ResultHelper.uniquify(results);

        // 重複チェックは最終操作に依存するため、orderの影響をうけない
        assertThat(uniquify).hasSize(6);
    }
}