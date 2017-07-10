package common.order;

import core.mino.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// 指定したミノ列からホールドを利用して並び替えられるミノ列をすべて列挙
public class ForwardOrderLookUp {
    private final List<List<Integer>> indexesList;

    public ForwardOrderLookUp(int toDepth, int fromDepth) {
        this(toDepth, toDepth < fromDepth);
    }

    public ForwardOrderLookUp(int toDepth, boolean isOverBlock) {
        this.indexesList = forward(toDepth, isOverBlock);
    }

    private List<List<Integer>> forward(int toDepth, boolean isOverBlock) {
        assert 1 < toDepth;
        List<Integer> indexes = IntStream.range(0, toDepth).boxed().collect(Collectors.toList());

        ArrayList<StackOrder<Integer>> candidates = new ArrayList<>();
        StackOrder<Integer> e = new IntegerListStackOrder();
        e.addLast(indexes.get(0));
        e.addLast(indexes.get(1));
        candidates.add(e);

        StackOrder<Integer> e2 = new IntegerListStackOrder();
        e2.addLast(indexes.get(1));
        e2.addLast(indexes.get(0));
        candidates.add(e2);

        for (int depth = 2; depth < toDepth; depth++) {
            Integer number = indexes.get(depth);
            int size = candidates.size();
            for (int index = 0; index < size; index++) {
                StackOrder<Integer> pieces = candidates.get(index);
                StackOrder<Integer> freeze = pieces.freeze();

                pieces.addLastTwo(number);  // おく
                freeze.addLast(number);  // holdする

                candidates.add(freeze);
            }
        }

        if (isOverBlock) {
            Integer number = toDepth;
            int size = candidates.size();
            for (int index = 0; index < size; index++) {
                StackOrder<Integer> pieces = candidates.get(index);
                StackOrder<Integer> freeze = pieces.freeze();

                pieces.addLastTwoAndRemoveLast(number);  // おく

                candidates.add(freeze);
            }
        }

        return candidates.stream()
                .map(StackOrder::toList)
                .collect(Collectors.toList());
    }

    public Stream<Stream<Block>> parse(List<Block> blocks) {
        assert 1 < indexesList.get(0).size() && indexesList.get(0).size() <= blocks.size();

        return indexesList.stream()
                .map(indexes -> indexes.stream().map(index -> index != -1 ? blocks.get(index) : null));
    }
}
