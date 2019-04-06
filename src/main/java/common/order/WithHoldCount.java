package common.order;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WithHoldCount<T> {
    private final List<T> list;
    private final int holdCount;

    WithHoldCount(Stream<T> stream, int holdCount) {
        this(stream.collect(Collectors.toList()), holdCount);
    }

    WithHoldCount(List<T> list, int holdCount) {
        this.list = list;
        this.holdCount = holdCount;
    }

    public List<T> getList() {
        return list;
    }

    public int getHoldCount() {
        return holdCount;
    }
}
