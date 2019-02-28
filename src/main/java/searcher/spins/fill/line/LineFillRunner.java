package searcher.spins.fill.line;

import common.datastore.PieceCounter;
import common.iterable.CombinationIterable;
import core.field.Field;
import core.field.FieldView;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.fill.line.next.RemainderField;
import searcher.spins.fill.line.next.RemainderFieldRunner;
import searcher.spins.fill.line.spot.*;
import searcher.spins.results.AddLastsResult;
import searcher.spins.results.EmptyResult;
import searcher.spins.results.Result;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LineFillRunner {
    private static final Set<PieceBlockCount> EMPTY_PIECE_BLOCK_COUNT_SET = Collections.emptySet();
    private static final int MAX_SIZE = 4;
    public static final int FIELD_WIDTH = 10;

    private final RemainderFieldRunner remainderFieldRunner;
    private final SpotRunner spotRunner;
    private final Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts;
    private final SimpleOriginalPieces simpleOriginalPieces;
    private final int fieldHeight;

    private final List<List<Integer>> indexes;
    private final ConcurrentMap<PieceBlockCounts, List<SpotResult>> spotResultCache = new ConcurrentHashMap<>();

    LineFillRunner(
            Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs,
            Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts,
            SimpleOriginalPieces simpleOriginalPieces,
            int maxPieceNum,
            int fieldHeight
    ) {
        this.remainderFieldRunner = new RemainderFieldRunner();
        this.spotRunner = new SpotRunner(pieceBlockCountToMinoDiffs, simpleOriginalPieces);
        this.pieceToPieceBlockCounts = pieceToPieceBlockCounts;
        this.simpleOriginalPieces = simpleOriginalPieces;
        this.fieldHeight = fieldHeight;

        this.indexes = createIndexes(maxPieceNum);
    }

    private List<List<Integer>> createIndexes(int maxPieceNum) {
        List<List<Integer>> indexes = new ArrayList<>();
        for (int index = 0; index <= maxPieceNum; index++) {
            indexes.add(index, IntStream.range(0, index).boxed().collect(Collectors.toList()));
        }
        return indexes;
    }

    public Stream<Result> search(Field initField, PieceCounter pieceCounter, int targetY) {
        EmptyResult emptyResult = new EmptyResult(initField, pieceCounter, fieldHeight);
        List<RemainderField> remainderFields = remainderFieldRunner.extract(initField, targetY);
        return search(emptyResult, targetY, remainderFields, 0);
    }

    private Stream<Result> search(
            Result prevResult, int targetY, List<RemainderField> remainderFields, int index
    ) {
        assert index < remainderFields.size() : index;

        RemainderField remainderField = remainderFields.get(index);
        int blockCount = remainderField.getTargetBlockCount();
        int startX = remainderField.getMinX();

        Field allMergedField = prevResult.getAllMergedField();
        SlidedField slidedField = SlidedField.create(allMergedField, targetY);

        Stream<Result> stream = searchBlockCounts(prevResult.getRemainderPieceCounter(), blockCount)
                .flatMap(pieceBlockCountList -> spot(slidedField, pieceBlockCountList, prevResult, startX, targetY));

        if (index == remainderFields.size() - 1) {
            return stream;
        }

        return stream.flatMap(result -> search(result, targetY, remainderFields, index + 1));
    }

    private Stream<List<PieceBlockCount>> searchBlockCounts(PieceCounter pieceCounter, int blockCount) {
        assert !pieceCounter.isEmpty();
        assert 0 < blockCount;
        Stream.Builder<List<PieceBlockCount>> builder = Stream.builder();
        searchBlockCounts(builder, new LinkedList<>(pieceCounter.getBlocks()), blockCount, new LinkedList<>());
        return builder.build();
    }

    private void searchBlockCounts(
            Stream.Builder<List<PieceBlockCount>> builder,
            LinkedList<Piece> remainderPieces, int remainderBlockCount, LinkedList<PieceBlockCount> result
    ) {
        // 残りのミノがないときは探索終了
        if (remainderPieces.isEmpty()) {
            return;
        }

        Piece headPiece = remainderPieces.pollFirst();

        // `headPiece` を使う場合
        Set<PieceBlockCount> pieceBlockCounts = pieceToPieceBlockCounts.getOrDefault(headPiece, EMPTY_PIECE_BLOCK_COUNT_SET);
        for (PieceBlockCount candidateBlockCount : pieceBlockCounts) {
            // 残りのブロック数
            int nextBlockCount = remainderBlockCount - candidateBlockCount.getBlockCount();

            // 残りのブロック数をオーバーしたときは次に進む
            if (nextBlockCount < 0) {
                continue;
            }

            // 候補を追加
            result.addLast(candidateBlockCount);

            if (nextBlockCount == 0) {
                builder.accept(new ArrayList<>(result));
            } else {
                searchBlockCounts(builder, remainderPieces, nextBlockCount, result);
            }

            result.removeLast();
        }

        // `headPiece` を使わない場合
        searchBlockCounts(builder, remainderPieces, remainderBlockCount, result);

        remainderPieces.addFirst(headPiece);
    }

    private Stream<Result> spot(
            SlidedField slidedField, List<PieceBlockCount> pieceBlockCountList, Result prevResult, int startX, int targetY
    ) {
        int size = pieceBlockCountList.size();

        if (size <= MAX_SIZE) {
            // 残っているミノが `MAX_SIZE` こ以内
            PieceBlockCounts pieceBlockCounts = new PieceBlockCounts(pieceBlockCountList);
            return spot(slidedField, prevResult, startX, targetY, pieceBlockCounts);
        }

        // 残っているミノが `MAX_SIZE+1` こ以上
        CombinationIterable<Integer> iterable = new CombinationIterable<>(indexes.get(size), MAX_SIZE);

        HashSet<Long> visited = new HashSet<>();

        // 分割する
        return StreamSupport.stream(iterable.spliterator(), false)
                .flatMap(selectedIndexes -> {
                    assert selectedIndexes.size() == MAX_SIZE;

                    Integer i1 = selectedIndexes.get(0);
                    Integer i2 = selectedIndexes.get(1);
                    Integer i3 = selectedIndexes.get(2);
                    Integer i4 = selectedIndexes.get(3);

                    PieceBlockCounts pieceBlockCounts = new PieceBlockCounts(
                            Arrays.asList(pieceBlockCountList.get(i1), pieceBlockCountList.get(i2), pieceBlockCountList.get(i3), pieceBlockCountList.get(i4))
                    );

                    if (!visited.add(pieceBlockCounts.getKey())) {
                        return Stream.empty();
                    }

                    int[] indexArray = new int[]{i1, i2, i3, i4};
                    Arrays.sort(indexArray);

                    List<PieceBlockCount> remain = new LinkedList<>(pieceBlockCountList);

                    // 要素がスライドされないようにindexが大きい順に取り除いていく
                    remain.remove(indexArray[3]);
                    remain.remove(indexArray[2]);
                    remain.remove(indexArray[1]);
                    remain.remove(indexArray[0]);

                    return spot(slidedField, prevResult, startX, targetY, pieceBlockCounts)
                            .flatMap(result -> {
                                int usingBlockCount = pieceBlockCounts.getUsingBlockCount();

                                Field allMergedField = result.getAllMergedField();
                                SlidedField nextSlidedField = SlidedField.create(allMergedField, slidedField);

                                return spot(nextSlidedField, remain, result, startX + usingBlockCount, targetY);
                            });
                });
    }

    private Stream<Result> spot(
            SlidedField slidedField, Result prevResult, int startX, int targetY, PieceBlockCounts pieceBlockCounts
    ) {
        assert pieceBlockCounts.getPieceBlockCountList().size() <= MAX_SIZE;

        Field field = slidedField.getField();
        long filledLine = slidedField.getFilledLine();
        int slideDownY = slidedField.getSlideDownY();

        int slideY = 3 - targetY;

        Stream<Result> stream = spot(pieceBlockCounts).stream()
                .map(spotResult -> {
                    int slideX = startX - spotResult.getStartX();
                    if (slideX < 0) {
                        // 左に移動しないといけないときは必ず置けない
                        return null;
                    }

                    int rightX = spotResult.getRightX() + slideX;
                    if (10 <= rightX) {
                        // 最も右にあるブロックが範囲外になる
                        return null;
                    }

                    int minY = spotResult.getMinY();
                    if (minY - slideY < 0) {
                        // 最も下にあるブロックが範囲外になる
                        return null;
                    }

                    // 揃えるラインをy=3にスライド済み
                    Field usingField = spotResult.getUsingField().freeze();
                    usingField.slideRight(slideX);
                    if (!field.canMerge(usingField)) {
                        // マージできない
                        return null;
                    }

                    List<SimpleOriginalPiece> operations = spotResult.getOperations().stream()
                            .map(originalPiece -> {
                                SimpleOriginalPiece slidedPiece = simpleOriginalPieces.get(
                                        originalPiece.getPiece(), originalPiece.getRotate(),
                                        originalPiece.getX() + slideX, originalPiece.getY() + slideDownY
                                );
                                Field freeze = slidedPiece.getMinoField().freeze();
                                freeze.insertWhiteLineWithKey(filledLine);
                                return simpleOriginalPieces.get(freeze);
                            })
                            .collect(Collectors.toList());

                    return AddLastsResult.create(prevResult, operations);
                });

        return stream.filter(Objects::nonNull);
    }

    private List<SpotResult> spot(PieceBlockCounts pieceBlockCounts) {
        return spotResultCache.computeIfAbsent(pieceBlockCounts, (key) -> (
                spotRunner.search(pieceBlockCounts.getPieceBlockCountList()))
        );
    }
}