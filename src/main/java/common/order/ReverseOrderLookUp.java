package common.order;

import core.mino.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// 他のミノ列からホールドを利用して指定したミノ列にできるとき、その他のミノ列をすべて逆算して列挙
public class ReverseOrderLookUp {
    private final List<List<Integer>> indexesList;

    public ReverseOrderLookUp(int toDepth, int fromDepth) {
        this.indexesList = reverse(toDepth, fromDepth);
    }

    private List<List<Integer>> reverse(int toDepth, int fromDepth) {
        assert 1 <= toDepth;
        assert toDepth <= fromDepth;
        List<Integer> indexes = IntStream.range(0, toDepth).boxed().collect(Collectors.toList());

        ArrayList<StackOrder<Integer>> candidates = new ArrayList<>();
        StackOrder<Integer> e = new IntegerListStackOrder();
        candidates.add(e);

        for (int depth = 0; depth < fromDepth; depth++) {
            Integer number = depth < indexes.size() ? indexes.get(depth) : -1;
            int size = candidates.size();
            if (depth < fromDepth - 1) {
                for (int index = 0; index < size; index++) {
                    StackOrder<Integer> pieces = candidates.get(index);
                    StackOrder<Integer> freeze = pieces.freeze();

                    pieces.addLast(number);
                    freeze.stock(number);

                    candidates.add(freeze);
                }
            } else {
                for (StackOrder<Integer> pieces : candidates)
                    pieces.stock(number);
            }
        }

        return candidates.stream()
                .map(StackOrder::toList)
                .collect(Collectors.toList());
    }

    public Stream<Stream<Block>> parse(List<Block> blocks) {
        assert blocks.size() <= indexesList.get(0).size();
        return indexesList.stream()
                .map(indexes -> indexes.stream().map(index -> index != -1 ? blocks.get(index) : null));
    }
}
