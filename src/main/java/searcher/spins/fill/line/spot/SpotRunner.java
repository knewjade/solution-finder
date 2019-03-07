package searcher.spins.fill.line.spot;

import common.iterable.PermutationIterable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.pieces.SimpleOriginalPieces;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpotRunner {
    public static final int MAX_HEIGHT = 7;

    private final Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs;
    private final SimpleOriginalPieces simpleOriginalPieces;

    public SpotRunner(
            Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs,
            SimpleOriginalPieces simpleOriginalPieces
    ) {
        assert MAX_HEIGHT <= simpleOriginalPieces.getMaxHeight() : simpleOriginalPieces.getMaxHeight();
        this.pieceBlockCountToMinoDiffs = pieceBlockCountToMinoDiffs;
        this.simpleOriginalPieces = simpleOriginalPieces;
    }

    // y=3で揃える
    public List<SpotResult> search(List<PieceBlockCount> pieceBlockCounts) {
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

        assert 1 <= size : size;

        int sumBlockCount = 0;
        int startX = 0;
        int blockCountWithRightMargin = 0;
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
            int currentBlockCountWithRightMargin = sumBlockCount + rightMargin;
            if (10 < startX + currentBlockCountWithRightMargin) {
                return;
            }

            // 最も右にあるブロックまで個数を更新する
            if (blockCountWithRightMargin < currentBlockCountWithRightMargin) {
                blockCountWithRightMargin = currentBlockCountWithRightMargin;
            }

            // フィールドをスライドする
            field.slideLeft(blockCount);
        }

        // 解として確定
        List<SimpleOriginalPiece> operations = new ArrayList<>();
        Field usingField = FieldFactory.createField(MAX_HEIGHT);
        int minY = Integer.MAX_VALUE;
        int maxY = -1;
        for (int index = 0; index < size; index++) {
            MinoDiff minoDiff = minoDiffs.get(index);
            int minX = xs[index] + startX;

            SimpleOriginalPiece operation = simpleOriginalPieces.get(minoDiff.getPiece(), minoDiff.getRotate(), minoDiff.calcCx(minX), minoDiff.getY());

            assert operation != null : minoDiff;

            operations.add(operation);

            int y = operation.getY();
            Mino mino = operation.getMino();

            assert Field.isIn(mino, operation.getX(), y) : operation;
            assert usingField.canMerge(operation.getMinoField());

            usingField.merge(operation.getMinoField());

            // 最も下のブロックを更新
            int minoMinY = y + mino.getMinY();
            if (minoMinY < minY) {
                minY = minoMinY;
            }

            // 最も上のブロックを更新
            int minoMaxY = y + mino.getMaxY();
            if (maxY < minoMaxY) {
                maxY = minoMaxY;
            }
        }

        int rightX = startX + blockCountWithRightMargin - 1;
        SpotResult spotResult = new SpotResult(operations, usingField, startX, sumBlockCount, rightX, minY, maxY);
        builder.accept(spotResult);
    }
}
