package searcher.spins.fill.line;

import common.datastore.Operation;
import common.datastore.OperationWithKey;
import common.datastore.PieceCounter;
import common.datastore.SimpleMinoOperation;
import common.iterable.CombinationIterable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import core.srs.Rotate;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LineFillRunner {
    private static final Set<PieceBlockCount> EMPTY_PIECE_BLOCK_COUNT_SET = Collections.emptySet();
    public static final int MAX_SIZE = 3;

    public static LineFillRunner create(MinoFactory minoFactory, MinoShifter minoShifter, List<SimpleOriginalPiece> simpleOriginalPieces, int maxHeight) {
        Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts = new EnumMap<>(Piece.class);
        Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs = new HashMap<>();

        for (Piece piece : Piece.values()) {
            Set<PieceBlockCount> currentPieceToPieceBlockCounts = new HashSet<>();

            Set<Rotate> rotates = minoShifter.getUniqueRotates(piece);
            for (Rotate rotate : rotates) {
                Mino mino = minoFactory.create(piece, rotate);

                // 計算
                HashMap<Integer, MinXBlockCount> dyToMinXLineCount = new HashMap<>();
                int[][] positions = mino.getPositions();
                for (int[] position : positions) {
                    int dx = position[0];
                    int dy = position[1];

                    MinXBlockCount minXBlockCount = dyToMinXLineCount.computeIfAbsent(dy, (key) -> new MinXBlockCount());
                    minXBlockCount.incrementBlockCount();
                    minXBlockCount.updateMinX(dx);
                }

                // 更新: dy
                for (Map.Entry<Integer, MinXBlockCount> entry : dyToMinXLineCount.entrySet()) {
                    int dy = entry.getKey();
                    MinXBlockCount minXBlockCount = entry.getValue();
                    int blockCount = minXBlockCount.getBlockCount();
                    int minX = minXBlockCount.getMinX();

                    PieceBlockCount pieceBlockCount = new PieceBlockCount(piece, blockCount);
                    MinoDiff minoDiff = new MinoDiff(mino, minX, dy, blockCount);

                    // currentPieceToPieceBlockCounts
                    currentPieceToPieceBlockCounts.add(pieceBlockCount);

                    // currentPieceLineCountToMinos
                    List<MinoDiff> minoDiffs = pieceBlockCountToMinoDiffs.computeIfAbsent(pieceBlockCount, (key) -> new ArrayList<>());
                    minoDiffs.add(minoDiff);
                }
            }

            pieceToPieceBlockCounts.put(piece, currentPieceToPieceBlockCounts);
        }

        Map<Long, SimpleOriginalPiece> keyToOriginPiece = new HashMap<>();
        for (SimpleOriginalPiece originalPiece : simpleOriginalPieces) {
            long key = OperationWithKey.toUniqueKey(originalPiece);
            assert !keyToOriginPiece.containsKey(key) : originalPiece;
            keyToOriginPiece.put(key, originalPiece);
        }

//        return new LineFillRunner(pieceToPieceBlockCounts, pieceBlockCountToMinoDiffs, keyToOriginPiece, maxHeight);
        return null;
    }

//    private final ConcurrentMap<Long, List<PieceSpot>> map;
//    private final Map<Integer, List<Integer>> indexes;
//    private final Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts;
//    private final Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs;
//    private final Map<Long, SimpleMinoOperation> keyToOperation;

//    private final int maxHeight;


    private LineFillRunner(
            Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts,
            Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs,
            Map<Long, SimpleMinoOperation> keyToOperation,
            int maxHeight
    ) {
//        this.pieceToPieceBlockCounts = pieceToPieceBlockCounts;
//        this.pieceBlockCountToMinoDiffs = pieceBlockCountToMinoDiffs;
//        this.keyToOriginPiece = keyToOriginPiece;
//        this.maxHeight = maxHeight;
    }

    public Stream<LineFillResult> search(PieceCounter pieceCounter, Field initField, long filledLine, int startX, int targetY, int blockCount) {
        return Stream.empty();
    }
/**
    public Stream<LineFillResult> search(PieceCounter pieceCounter, Field initField, long filledLine, int startX, int targetY, int blockCount) {
        Map<Long, SimpleOriginalPiece> keyToOriginPiece = new HashMap<>()

        EmptyLineFillResult emptyLineFillResult = new EmptyLineFillResult(initField, pieceCounter, blockCount);
        return next(keyToOriginPiece, emptyLineFillResult, startX, targetY);
    }

    private Stream<LineFillResult> next(
            Map<Long, SimpleOriginalPiece> keyToOriginPiece, LineFillResult prevResult, int startX, int targetY
    ) {
        int remainBlockCount = prevResult.getRemainBlockCount();
        PieceCounter restPieceCounter = prevResult.getRemainPieceCounter();

        return searchBlockCounts(remainBlockCount, restPieceCounter)
                .flatMap(pieceBlockCountList -> {
                    return getStream(keyToOriginPiece, prevResult, startX, targetY, pieceBlockCountList);
                });
    }

    private Stream<? extends LineFillResult> getStream(Map<Long, SimpleOriginalPiece> keyToOriginPiece, LineFillResult prevResult, int startX, int targetY, List<PieceBlockCount> pieceBlockCountList) {
        int remainBlockCount = prevResult.getRemainBlockCount();

        // `MAX_SIZE` ミノも残っていない
        int size = pieceBlockCountList.size();
        if (size <= MAX_SIZE) {
            PieceBlockCounts pieceBlockCounts = new PieceBlockCounts(pieceBlockCountList);
            return to(pieceBlockCounts).stream()
                    .filter(spot -> spot.getUsingBlockCount() == remainBlockCount)
                    .map(spot -> {
                        List<SimpleOriginalPiece> operations = slide(keyToOriginPiece, startX, targetY, spot);
                        return new AddLastLineFillResult(prevResult, operations, spot.getUsingBlockCount());
                    });
        }

        // `MAX_SIZE` ミノ以上残っている
        // 分割する
        List<Integer> indexList = indexes.get(size);
        CombinationIterable<Integer> iterable = new CombinationIterable<>(indexList, MAX_SIZE);
        return StreamSupport.stream(iterable.spliterator(), true)
                // TODO: 同じ組み合わせをフィルタする
                .flatMap(selected -> {
                    assert selected.size() == MAX_SIZE;

                    int[] arr = new int[]{selected.get(0), selected.get(1), selected.get(2)};
                    Arrays.sort(arr);

                    List<PieceBlockCount> remain = new LinkedList<>(pieceBlockCountList);

                    List<PieceBlockCount> first = Arrays.asList(remain.get(arr[0]), remain.get(arr[1]), remain.get((arr[2])));
                    PieceBlockCounts pieceBlockCounts = new PieceBlockCounts(first);

                    remain.remove(arr[2]);
                    remain.remove(arr[1]);
                    remain.remove(arr[0]);

                    return to(pieceBlockCounts).stream()
                            .flatMap(spot -> {
                                int usingBlockCount = spot.getUsingBlockCount();

                                if (remainBlockCount < usingBlockCount) {
                                    return Stream.empty();
                                }

                                List<SimpleOriginalPiece> operations = slide(keyToOriginPiece, startX, targetY, spot);
                                LineFillResult nextResult = new AddLastLineFillResult(prevResult, operations, spot.getUsingBlockCount());

                                if (nextResult.getRemainBlockCount() == 0) {
                                    return Stream.of(nextResult);
                                }

                                if (nextResult.getRemainPieceCounter().isEmpty()) {
                                    return Stream.empty();
                                }

                                return getStream(keyToOriginPiece, nextResult, startX + usingBlockCount, targetY, remain);
                            });
                });
    }

    private List<SimpleOriginalPiece> slide(Map<Long, SimpleOriginalPiece> keyToOriginPiece, int startX, int targetY, PieceSpot spot) {
        return spot.getOperations().stream()
                .map(operation -> {
                    Piece piece = operation.getPiece();
                    Rotate rotate = operation.getRotate();
                    int x = startX + operation.getX();
                    int y = operation.getY() - 3 + targetY;
                    return keyToOriginPiece.get(Operation.toUniqueKey(piece, rotate, x, y));
                })
                .collect(Collectors.toList());
    }

    private Stream<List<PieceBlockCount>> searchBlockCounts(int blockCount, PieceCounter pieceCounter) {
        return searchBlockCounts(blockCount, new LinkedList<>(pieceCounter.getBlocks()), new LinkedList<>());
    }

    private Stream<List<PieceBlockCount>> searchBlockCounts(int remainderBlockCount, LinkedList<Piece> remainderPieces, LinkedList<PieceBlockCount> result) {
        // 残りのミノがないときは探索終了
        if (remainderPieces.isEmpty()) {
            return Stream.empty();
        }

        Piece headPiece = remainderPieces.pollFirst();

        // `headPiece` を使う場合
        Stream<List<PieceBlockCount>> useStream = Stream.empty();

        for (PieceBlockCount candidateBlockCount : pieceToPieceBlockCounts.getOrDefault(headPiece, EMPTY_PIECE_BLOCK_COUNT_SET)) {
            int nextBlockCount = remainderBlockCount - candidateBlockCount.getBlockCount();

            if (nextBlockCount < 0) {
                continue;
            }

            result.addLast(candidateBlockCount);

            if (nextBlockCount == 0) {
                useStream = Stream.concat(useStream, Stream.of(new ArrayList<>(result)));
            } else {
                Stream<List<PieceBlockCount>> nextStream = searchBlockCounts(nextBlockCount, remainderPieces, result);
                useStream = Stream.concat(useStream, nextStream);
            }

            result.removeLast();
        }

        // `headPiece` を使わない場合
        Stream<List<PieceBlockCount>> noUseStream = searchBlockCounts(remainderBlockCount, remainderPieces, result);

        remainderPieces.addFirst(headPiece);

        return Stream.concat(useStream, noUseStream);
    }

    private List<PieceSpot> to(PieceBlockCounts pieceBlockCounts) {
        long key = pieceBlockCounts.getKey();
        return map.computeIfAbsent(key, (ignore) -> toNew(pieceBlockCounts.getPieceBlockCountList()));
    }


     **/
}