package common.order;

import common.NotImplementedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class IntegerListStackOrder implements StackOrder<Integer> {
    public static IntegerListStackOrder create() {
        return new IntegerListStackOrder();
    }

    public static IntegerListStackOrder create(int first) {
        IntegerListStackOrder order = new IntegerListStackOrder();
        order.addLast(first);
        return order;
    }

    public static IntegerListStackOrder create(int first, int second) {
        IntegerListStackOrder order = new IntegerListStackOrder();
        order.addLast(first);
        order.addLast(second);
        return order;
    }

    private final List<Integer> blocks;
    private int stockIndex;

    private IntegerListStackOrder() {
        this(new ArrayList<>(), 0);
    }

    private IntegerListStackOrder(List<Integer> blocks, int stockIndex) {
        this.blocks = blocks;
        this.stockIndex = stockIndex;
    }

    @Override
    public void addLast(Integer number) {
        assert number != null;
        blocks.add(number);
    }

    @Override
    public void addLastTwo(Integer number) {
        assert number != null;
        blocks.add(blocks.size() - 1, number);
    }

    @Override
    public void addLastTwoAndRemoveLast(Integer number) {
        assert number != null;
        blocks.add(blocks.size() - 1, number);
        blocks.remove(blocks.size() - 1);
    }

    @Override
    public void stock(Integer number) {
        assert number != null;
        blocks.add(stockIndex, number);
        stockIndex = blocks.size();
    }

    @Override
    public List<Integer> toList() {
        return blocks;
    }

    @Override
    public Stream<Integer> toStream() {
        return blocks.stream();
    }

    @Override
    public IntegerListStackOrder freeze() {
        return new IntegerListStackOrder(new ArrayList<>(blocks), stockIndex);
    }

    @Override
    public IntegerListStackOrder fix() {
        return new IntegerListStackOrder(Collections.unmodifiableList(blocks), stockIndex);
    }

    @Override
    public boolean equals(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("No immutable");
    }
}
