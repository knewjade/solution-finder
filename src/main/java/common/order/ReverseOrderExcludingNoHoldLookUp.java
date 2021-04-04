package common.order;

import core.mino.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// 「あるミノ列」からホールドを利用して指定したミノ列にできるとき、その「あるミノ列」をすべて逆算して列挙
// ホールドを使用せずに指定したミノ列になるパターン（最後にホールド[任意のミノ]を追加しただけのミノ列は除く）
public class ReverseOrderExcludingNoHoldLookUp {
    private final List<List<Integer>> indexesList;
    private final boolean containsNull;

    /**
     * @param toDepth   ホールドした後のミノ列の長さ
     * @param fromDepth 元のミノ列の長さ（ホールド前のミノ列）
     */
    public ReverseOrderExcludingNoHoldLookUp(int toDepth, int fromDepth) {
        this.indexesList = reverse(toDepth, fromDepth);
        this.containsNull = toDepth < fromDepth;
    }

    private List<List<Integer>> reverse(int toDepth, int fromDepth) {
        assert 1 <= toDepth;
        assert toDepth <= fromDepth;
        List<Integer> indexes = IntStream.range(0, toDepth).boxed().collect(Collectors.toList());

        ArrayList<StackOrder<Integer>> candidates = new ArrayList<>();
        StackOrder<Integer> e = IntegerListStackOrder.create();
        candidates.add(e);

        for (int depth = 0; depth < fromDepth; depth++) {
            Integer number = depth < indexes.size() ? indexes.get(depth) : -1;
            if (depth < fromDepth - 1) {
                for (int index = 0, size = candidates.size(); index < size; index++) {
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

        // ホールドせずに置けるパターンを除く
        List<Integer> candidateWithoutHold = new ArrayList<>(indexes);
        candidateWithoutHold.add(-1);

        return candidates.stream()
                .map(StackOrder::toList)
                .filter(it -> !candidateWithoutHold.equals(it))
                .collect(Collectors.toList());
    }

    public <T> Stream<Stream<T>> parse(List<T> pieces) {
        return parse(pieces, null);
    }

    private <T> Stream<Stream<T>> parse(List<T> pieces, T nullPiece) {
        assert pieces.size() <= indexesList.get(0).size();
        return indexesList.stream()
                .map(indexes -> indexes.stream().map(index -> index != -1 ? pieces.get(index) : nullPiece));
    }

    public Stream<Stream<Piece>> parseAndExpand(List<Piece> pieces) {
        assert pieces.size() <= indexesList.get(0).size();
        if (containsNull) {
            return Piece.valueList().stream().flatMap(piece -> parse(pieces, piece));
        } else {
            return parse(pieces);
        }
    }
}
