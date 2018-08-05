package searcher.common;

public interface DataPool<O, R> {
    void addOrder(O order);

    void addResult(R result);
}
