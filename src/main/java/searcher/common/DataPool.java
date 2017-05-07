package searcher.common;

import common.datastore.Result;
import common.datastore.order.Order;

public interface DataPool {
    void addOrder(Order order);

    void addResult(Result result);
}
