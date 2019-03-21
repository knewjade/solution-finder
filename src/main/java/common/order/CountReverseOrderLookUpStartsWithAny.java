package common.order;

import common.datastore.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// 「あるミノ列」からホールドを利用して指定したミノ列にできるとき、その「あるミノ列」をすべて逆算して列挙
// ホールドを使用せずに指定したミノ列になるパターン（最後にホールド[任意のミノ]を追加しただけのミノ列は除く）
// 「あるミノ列」の先頭のミノはホールドされているとする（初手に取り出すことは可能）
public class CountReverseOrderLookUpStartsWithAny {
    private final List<Pair<List<Integer>, Integer>> indexesList;

    /**
     * @param toDepth   ホールドした後のミノ列の長さ
     * @param fromDepth 元のミノ列の長さ（ホールド前のミノ列）
     */
    public CountReverseOrderLookUpStartsWithAny(int toDepth, int fromDepth) {
        this.indexesList = reverse(toDepth, fromDepth);
    }

    private List<Pair<List<Integer>, Integer>> reverse(int toDepth, int fromDepth) {
        assert 1 <= toDepth;
        assert toDepth <= fromDepth;
        List<Integer> indexes = IntStream.range(0, toDepth).boxed().collect(Collectors.toList());

        ArrayList<StackOrderWithHoldCount<Integer>> candidates = new ArrayList<>();
        StackOrderWithHoldCount<Integer> e = new CountStackOrderStartsWithAny();
        candidates.add(e);

        for (int depth = 0; depth < fromDepth; depth++) {
            Integer number = depth < indexes.size() ? indexes.get(depth) : -1;
            if (depth < fromDepth - 1) {
                for (int index = 0, size = candidates.size(); index < size; index++) {
                    StackOrderWithHoldCount<Integer> pieces = candidates.get(index);
                    StackOrderWithHoldCount<Integer> freeze = pieces.freeze();

                    pieces.addLast(number);
                    freeze.stock(number);

                    candidates.add(freeze);
                }
            } else {
                for (StackOrderWithHoldCount<Integer> pieces : candidates)
                    pieces.stock(number);
            }
        }

        return candidates.stream()
                .map(StackOrderWithHoldCount::toListWithHoldCount)
                .collect(Collectors.toList());
    }

    public <T> Stream<WithHoldCount<T>> parse(List<T> pieces) {
        return parse(pieces, null);
    }

    private <T> Stream<WithHoldCount<T>> parse(List<T> pieces, T nullPiece) {
        assert pieces.size() <= indexesList.get(0).getKey().size();
        return indexesList.stream()
                .map(pair -> {
                    List<Integer> indexes = pair.getKey();
                    Stream<T> stream = indexes.stream().map(index -> index != -1 ? pieces.get(index) : nullPiece);
                    return new WithHoldCount<T>(stream, pair.getValue());
                });
    }
}
