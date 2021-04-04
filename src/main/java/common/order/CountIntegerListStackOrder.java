package common.order;

import common.NotImplementedException;
import common.datastore.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CountIntegerListStackOrder {
    public static CountIntegerListStackOrder createBeforeHold(int first, int second) {
        CountIntegerListStackOrder order = new CountIntegerListStackOrder(0);
        order.addLast(first);
        order.addLast(second);
        return order;
    }

    public static CountIntegerListStackOrder createAfterHold(int first, int second) {
        CountIntegerListStackOrder order = new CountIntegerListStackOrder(1);
        order.addLast(first);
        order.addLast(second);
        return order;
    }

    private final List<Integer> blocks;
    private final int stockIndex;
    private int holdCount;

    private CountIntegerListStackOrder(int holdCount) {
        this(new ArrayList<>(), 0, holdCount);
    }

    private CountIntegerListStackOrder(List<Integer> blocks, int stockIndex, int holdCount) {
        this.blocks = blocks;
        this.stockIndex = stockIndex;
        this.holdCount = holdCount;
    }

    public void addLast(Integer number) {
        assert number != null;
        blocks.add(number);
    }

    public void addLastAndIncrementHold(Integer number) {
        addLast(number);
        incrementHoldCount();
    }

    public void addLastTwo(Integer number) {
        assert number != null;
        blocks.add(blocks.size() - 1, number);
    }

    public void addLastTwoAndIncrementHold(Integer number) {
        addLastTwo(number);
        incrementHoldCount();
    }

    public void addLastTwoAndRemoveLast(Integer number) {
        assert number != null;
        blocks.add(blocks.size() - 1, number);
        blocks.remove(blocks.size() - 1);
    }

    public void addLastTwoAndRemoveLastAndIncrementHold(Integer number) {
        addLastTwoAndRemoveLast(number);
        incrementHoldCount();
    }

    public void incrementHoldCount() {
        holdCount += 1;
    }

    public CountIntegerListStackOrder freeze() {
        return new CountIntegerListStackOrder(new ArrayList<>(blocks), stockIndex, holdCount);
    }

    public CountIntegerListStackOrder fix() {
        return new CountIntegerListStackOrder(Collections.unmodifiableList(blocks), stockIndex, holdCount);
    }

    @Override
    public boolean equals(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("No immutable");
    }

    public Pair<List<Integer>, Integer> toListWithHoldCount() {
        return new Pair<>(blocks, holdCount);
    }

    public boolean existsHoldPiece() {
        return 0 < holdCount;
    }
}
