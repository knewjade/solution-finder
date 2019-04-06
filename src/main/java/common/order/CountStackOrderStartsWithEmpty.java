package common.order;

import common.NotImplementedException;
import common.datastore.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

// ホールドが空で開始したとき
public class CountStackOrderStartsWithEmpty implements StackOrderWithHoldCount<Integer> {
    private final List<Integer> blocks;
    private int stockIndex;
    private int holdCount;
    private boolean isFirstAdd;

    CountStackOrderStartsWithEmpty() {
        this(new ArrayList<>(), 0, 0, false);
    }

    private CountStackOrderStartsWithEmpty(List<Integer> blocks, int stockIndex, int holdCount, boolean isFirstAdd) {
        this.blocks = blocks;
        this.stockIndex = stockIndex;
        this.holdCount = holdCount;
        this.isFirstAdd = isFirstAdd;
    }

    @Override
    public void addLast(Integer number) {
        assert number != null;
        blocks.add(number);
        isFirstAdd = true;
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
        stockIndex = blocks.size();
        if(isFirstAdd){
            // 最初の追加まではホールドしなくても置けるパターンのため、ホールドとしてカウントしない
            // 位置調整のみ
            holdCount += 1;
        }
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
        return new CountStackOrderStartsWithEmpty(new ArrayList<>(blocks), stockIndex, holdCount, isFirstAdd);
    }

    @Override
    public StackOrderWithHoldCount<Integer> fix() {
        return new CountStackOrderStartsWithEmpty(Collections.unmodifiableList(blocks), stockIndex, holdCount, isFirstAdd);
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
