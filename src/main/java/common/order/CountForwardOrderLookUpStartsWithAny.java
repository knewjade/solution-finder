package common.order;

import common.datastore.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 指定したミノ列からホールドを利用して並び替えられるミノ列をすべて列挙
// ホールドの回数を記録する
// 開始時点でホールドがある前提。piecesの先頭でホールドを指定する
public class CountForwardOrderLookUpStartsWithAny implements CountForwardOrderLookUp {
    private final List<Pair<List<Integer>, Integer>> indexesList;

    public CountForwardOrderLookUpStartsWithAny(int toDepth, int fromDepth) {
        this(toDepth, toDepth < fromDepth);
    }

    public CountForwardOrderLookUpStartsWithAny(int toDepth, boolean isOverBlock) {
        this.indexesList = forward(toDepth, isOverBlock);
    }

    private List<Pair<List<Integer>, Integer>> forward(int toDepth, boolean isOverBlock) {
        if (toDepth == 1) {
            if (!isOverBlock) {
                throw new IllegalStateException("Hold is empty always");
            }

            ArrayList<Pair<List<Integer>, Integer>> candidates = new ArrayList<>();
            {
                candidates.add(new Pair<>(Collections.singletonList(0), 1));
                candidates.add(new Pair<>(Collections.singletonList(1), 0));
            }
            return candidates;
        }

        ArrayList<CountIntegerListStackOrder> candidates = new ArrayList<>();
        CountIntegerListStackOrder e = CountIntegerListStackOrder.createBeforeHold(1, 0);
        candidates.add(e);

        CountIntegerListStackOrder e2 = CountIntegerListStackOrder.createAfterHold(0, 1);
        candidates.add(e2);

        for (int depth = 2; depth < toDepth; depth++) {
            Integer number = depth;
            int size = candidates.size();
            for (int index = 0; index < size; index++) {
                CountIntegerListStackOrder pieces = candidates.get(index);
                CountIntegerListStackOrder freeze = pieces.freeze();

                pieces.addLastTwo(number);  // おく
                freeze.addLastAndIncrementHold(number);  // holdする

                candidates.add(freeze);
            }
        }

        if (isOverBlock) {
            Integer number = toDepth;
            int size = candidates.size();
            for (int index = 0; index < size; index++) {
                CountIntegerListStackOrder pieces = candidates.get(index);
                CountIntegerListStackOrder freeze = pieces.freeze();

                pieces.addLastTwoAndRemoveLast(number);  // おく
                freeze.incrementHoldCount();  // holdする

                candidates.add(freeze);
            }
        } else {
            // ミノの数がぴったりの場合、最後にホールされているミノを取り出す必要がある
            for (CountIntegerListStackOrder candidate : candidates) {
                candidate.incrementHoldCount();
            }
        }

        return candidates.stream()
                .map(CountIntegerListStackOrder::toListWithHoldCount)
                .collect(Collectors.toList());
    }

    @Override
    public <T> Stream<WithHoldCount<T>> parse(List<T> pieces) {
        assert 1 <= indexesList.get(0).getKey().size() && indexesList.get(0).getKey().size() <= pieces.size();

        return indexesList.stream()
                .map(pair -> {
                    List<Integer> indexes = pair.getKey();
                    Stream<T> stream = indexes.stream().map(index -> index != -1 ? pieces.get(index) : null);
                    return new WithHoldCount<>(stream, pair.getValue());
                });
    }
}
