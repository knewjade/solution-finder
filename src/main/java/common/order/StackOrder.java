package common.order;

import java.util.List;
import java.util.stream.Stream;

public interface StackOrder<T> {
    void addLast(T block);

    void addLastTwo(T block);

    void stock(T block);

    List<T> toList();

    Stream<T> toStream();

    StackOrder<T> freeze();

    StackOrder<T> fix();

    void addLastTwoAndRemoveLast(T block);
}
