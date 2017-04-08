package searcher.common;

import searcher.common.Result;
import searcher.common.order.Order;

public interface DataPool {
    void addOrder(Order order);
    void addResult(Result result);
}
