package common.order;

import common.NotImplementedException;
import common.datastore.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

// ホールドがある状態で開始したとき
public class CountStackOrderStartsWithAny implements StackOrderWithHoldCount<Integer> {
    private final List<Integer> blocks;
    private int stockIndex;
    private int holdCount;

    CountStackOrderStartsWithAny() {
        this(new ArrayList<>(), 0, 0);
    }

    private CountStackOrderStartsWithAny(List<Integer> blocks, int stockIndex, int holdCount) {
        this.blocks = blocks;
        this.stockIndex = stockIndex;
        this.holdCount = holdCount;
    }

    @Override
    public void addLast(Integer number) {
        assert number != null;
        blocks.add(number);
    }

    @Override
    public void addLastTwo(Integer number) {
        throw new NotImplementedException();
    }

    @Override
    public void addLastTwoAndRemoveLast(Integer number) {
        throw new NotImplementedException();
    }

    @Override
    public void stock(Integer number) {
        assert number != null;
        blocks.add(stockIndex, number);
        if (stockIndex != 0) {
            // 最初の追加まではホールドしなくても置けるパターンのため、ホールドとしてカウントしない
            // 位置調整のみ
            holdCount += 1;
        }
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
    public StackOrderWithHoldCount<Integer> freeze() {
        return new CountStackOrderStartsWithAny(new ArrayList<>(blocks), stockIndex, holdCount);
    }

    @Override
    public StackOrderWithHoldCount<Integer> fix() {
        return new CountStackOrderStartsWithAny(Collections.unmodifiableList(blocks), stockIndex, holdCount);
    }

    @Override
    public boolean equals(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("No immutable");
    }

    @Override
    public Pair<List<Integer>, Integer> toListWithHoldCount() {
        return new Pair<>(blocks, holdCount);
    }
}
