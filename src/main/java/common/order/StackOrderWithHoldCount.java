package common.order;

import common.datastore.Pair;

import java.util.List;

public interface StackOrderWithHoldCount<T> extends StackOrder<T> {
    StackOrderWithHoldCount<T> fix();

    StackOrderWithHoldCount<T> freeze();

    Pair<List<T>, Integer> toListWithHoldCount();

    boolean existsHoldPiece();

    void incrementHoldCount();
}
