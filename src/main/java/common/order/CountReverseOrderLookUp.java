package common.order;

import java.util.List;
import java.util.stream.Stream;

public interface CountReverseOrderLookUp {
    <T> Stream<WithHoldCount<T>> parse(List<T> pieces);
}
