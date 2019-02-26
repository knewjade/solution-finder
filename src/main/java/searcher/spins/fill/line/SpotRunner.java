package searcher.spins.fill.line;

import common.datastore.OperationWithKey;
import common.iterable.PermutationIterable;
import core.field.Field;
import core.field.FieldFactory;
import core.neighbor.SimpleOriginalPiece;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SpotRunner {
    private static final int MAX_HEIGHT = 7;

    private final Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs;
    private final Map<Long, SimpleOriginalPiece> keyToOperation;

    SpotRunner(
            Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs,
            Map<Long, SimpleOriginalPiece> keyToOperation
    ) {
        this.pieceBlockCountToMinoDiffs = pieceBlockCountToMinoDiffs;
        this.keyToOperation = keyToOperation;
    }

    // y=3で揃える
    List<SpotResult> toNew(List<PieceBlockCount> pieceBlockCounts) {
        assert 1 <= pieceBlockCounts.size();

        HashSet<Long> visited = new HashSet<>();

        Stream.Builder<SpotResult> builder = Stream.builder();

        PermutationIterable<PieceBlockCount> iterable = new PermutationIterable<>(pieceBlockCounts, pieceBlockCounts.size());
        for (List<PieceBlockCount> counts : iterable) {
            if (visited.add(toKey(counts))) {
                searchMinos(builder, counts, 0, counts.size(), new LinkedList<>());
            }
        }

        return builder.build().collect(Collectors.toList());
    }

    private long toKey(List<PieceBlockCount> counts) {
        long key = 0L;
        for (PieceBlockCount count : counts) {
            key *= 77;
            key += count.getPiece().getNumber() * 11 + count.getBlockCount();
        }
        return key;
    }

    private void searchMinos(
            Stream.Builder<SpotResult> builder,
            List<PieceBlockCount> pieceBlockCounts, int index, int maxIndex,
            LinkedList<MinoDiff> result
    ) {
        PieceBlockCount pieceBlockCount = pieceBlockCounts.get(index);
        List<MinoDiff> minoDiffs = pieceBlockCountToMinoDiffs.get(pieceBlockCount);

        for (MinoDiff minoDiff : minoDiffs) {
            result.addLast(minoDiff);

            if (index == maxIndex - 1) {
                // すべてのミノを使った
                fix(builder, result);
            } else {
                // ミノが残っている
                searchMinos(builder, pieceBlockCounts, index + 1, maxIndex, result);
            }

            result.removeLast();
        }
    }

    private void fix(Stream.Builder<SpotResult> builder, List<MinoDiff> minoDiffs) {
        int size = minoDiffs.size();
        int sumBlockCount = 0;
        int startX = 0;
        int[] xs = new int[size];

        Field field = FieldFactory.createField(MAX_HEIGHT);
        for (int index = 0; index < size; index++) {
            MinoDiff minoDiff = minoDiffs.get(index);
            Field minoField = minoDiff.getMinoField();

            // おくことができるか
            if (!field.canMerge(minoField)) {
                return;
            }

            field.merge(minoField);

            // 現在のxの位置を記録
            int x = sumBlockCount;
            xs[index] = x;

            // 左端を超えないようにスライドする
            int leftMargin = minoDiff.getLeftMargin();
            int left = -(x - leftMargin);
            if (startX < left) {
                startX = left;
            }

            // ブロックの数を増やす
            int blockCount = minoDiff.getBlockCount();
            sumBlockCount += blockCount;

            // ブロックとマージンの合計が10を超えていないか
            int rightMargin = minoDiff.getRightMargin();
            if (10 < startX + sumBlockCount + rightMargin) {
                return;
            }

            // フィールドをスライドする
            field.slideLeft(blockCount);
        }

        // 解として確定
        List<SimpleOriginalPiece> operations = new ArrayList<>();
        Field usingField = FieldFactory.createField(MAX_HEIGHT);
        for (int index = 0; index < size; index++) {
            MinoDiff minoDiff = minoDiffs.get(index);
            int minX = xs[index] + startX;

            long key = OperationWithKey.toUniqueKey(
                    minoDiff.getPiece(), minoDiff.getRotate(), minoDiff.calcCx(minX), minoDiff.getY()
            );

            SimpleOriginalPiece operation = keyToOperation.get(key);
            operations.add(operation);

            assert Field.isIn(operation.getMino(), operation.getX(), operation.getY()) : operation;
            assert usingField.canMerge(operation.getMinoField());

            usingField.merge(operation.getMinoField());
        }

        SpotResult spotResult = new SpotResult(operations, usingField, startX, sumBlockCount);
        builder.accept(spotResult);
    }
}
