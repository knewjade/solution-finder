package searcher.spins.fill;

import common.datastore.PieceCounter;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.fill.line.LineFillRunner;
import searcher.spins.fill.results.AddLastFillResult;
import searcher.spins.fill.results.AddScaffoldFillResult;
import searcher.spins.fill.results.EmptyFillResult;
import searcher.spins.fill.results.FillResult;
import searcher.spins.results.Result;
import searcher.spins.scaffold.ScaffoldRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class FillRunner {
    private static final PieceCounter T_PIECE_COUNTER = PieceCounter.getSinglePieceCounter(Piece.T);

    private final LineFillRunner lineFillRunner;
    private final ScaffoldRunner scaffoldRunner;
    private final int maxTargetHeight;

    FillRunner(LineFillRunner lineFillRunner, ScaffoldRunner scaffoldRunner, int maxTargetHeight) {
        this.lineFillRunner = lineFillRunner;
        this.scaffoldRunner = scaffoldRunner;
        this.maxTargetHeight = maxTargetHeight;
    }

    Stream<FillResult> search(Result result) {
        EmptyFillResult emptyFillResult = new EmptyFillResult(result);
        return search(emptyFillResult, 0)
                .flatMap(this::addScaffold);
    }

    private Stream<FillResult> search(FillResult fillResult, int lowerY) {
        Result result = fillResult.getLastResult();

        Field field = result.getAllMergedField();
        PieceCounter pieceCounter = result.getRemainderPieceCounter();
        long firstFilledLine = field.getFilledLine();

        if (pieceCounter.isEmpty()) {
            return Stream.empty();
        }

        long belowY = KeyOperators.getMaskForKeyBelowY(maxTargetHeight);

        return IntStream.range(lowerY, maxTargetHeight)
                .boxed()
                .filter(y -> (firstFilledLine & KeyOperators.getBitKey(y)) == 0L)
                .flatMap(y -> {
                    System.out.println(y);
                    long aboveY = belowY & KeyOperators.getMaskForKeyAboveY(y + 1);

                    return lineFillRunner.search(field, pieceCounter, y)
                            .flatMap(lineFillResult -> {
                                // すでに揃っているライン
                                long filledLine = lineFillResult.getAllMergedField().getFilledLine();

                                // 現在の状態を解として記録
                                List<SimpleOriginalPiece> operations = lineFillResult.operationStream().collect(Collectors.toList());
                                FillResult nextFillResult = new AddLastFillResult(fillResult, operations);

                                // yより上でまだ揃っていないラインがない
                                long nextLine = ~filledLine & aboveY;
                                if (nextLine == 0L) {
                                    return Stream.of(nextFillResult);
                                }

                                int nextTargetY = KeyOperators.bitToYFromKey(KeyOperators.extractLowerBit(nextLine));
                                return Stream.concat(
                                        Stream.of(nextFillResult),
                                        search(nextFillResult, nextTargetY)
                                );
                            });
                });
    }

    private Stream<AddScaffoldFillResult> addScaffold(FillResult fillResult) {
        Result lastResult = fillResult.getLastResult();
        List<SimpleOriginalPiece> operations = fillResult.operationStream().collect(Collectors.toList());
        return scaffoldRunner.build(lastResult, operations)
                .map(scaffoldResult -> new AddScaffoldFillResult(fillResult, scaffoldResult));
    }
}









        /*
//        System.out.println("fill " + firstFilledLine + " " + lowerY);
        Result initLastResult = fillResult.getLastResult();

        // 探索対象ミノ
        Field firstField = initLastResult.getAllMergedField();
        PieceCounter reminderPieceCounter = initLastResult.getRemainderPieceCounter();
        List<TargetPieceCandidate> targetPieceCandidates = filledPieces.search(firstField, reminderPieceCounter, firstFilledLine, requiredDeletedKey, lowerY);

        // 軸となるミノを1つ選ぶ
        return targetPieceCandidates.stream()
                .flatMap(targetPieceCandidate -> {
                    // 軸ミノに必要な足場を探索
                    SimpleOriginalPiece firstOriginalPiece = targetPieceCandidate.getSimpleOriginalPiece();

                    // 残りのミノ
                    return scaffoldRunner.buildAllowToFill(initLastResult, firstOriginalPiece)
                            .flatMap(scaffoldResult -> {
                                Result result = scaffoldResult.getLastResult();

                                // 新しく揃ったライン
                                long scaffoldFilledLine = result.getAllMergedField().getFilledLine();
                                long newFilledLine = scaffoldFilledLine ^ firstFilledLine;
                                int newFilledMinY = newFilledLine != 0
                                        ? KeyOperators.bitToYFromKey(KeyOperators.extractLowerBit(newFilledLine))
                                        : Integer.MAX_VALUE;

                                return targetPieceCandidate.getTargetPieces(maxHeight)
                                        .flatMap(targetPiece -> {
                                            // 消去する対象のライン
                                            int targetY = targetPiece.getTargetY();

                                            // ターゲットより下が揃っている
                                            if (newFilledMinY < targetY) {
                                                return Stream.empty();
                                            }

                                            long targetLineKey = targetPiece.getTargetLineKey();

                                            // ターゲットが揃ったとき
                                            if ((scaffoldFilledLine & targetLineKey) == targetLineKey) {
                                                // ターゲットがすでに揃っている
                                                if (targetY == newFilledMinY) {
                                                    // 一番下のラインが揃っている
                                                    FillResult currentResult = new AddLastFillResult(fillResult, scaffoldResult.getLastResult(), createTOperations(firstOriginalPiece));
                                                    return afterFill(currentResult, firstFilledLine, requiredDeletedKey, targetY);
                                                }

                                                // 一番下のライン以外が揃ったときは解が重複するためスキップする
                                                return Stream.empty();
                                            }

                                            // 指定されたラインが揃っていないので埋める
                                            return fill(scaffoldResult, targetPiece, firstFilledLine)
                                                    .flatMap(simpleOriginalPieces -> {
                                                        List<SimpleOriginalPiece> tOperations = Stream.concat(Stream.of(firstOriginalPiece), simpleOriginalPieces.stream())
                                                                .filter(it -> it.getPiece() == Piece.T)
                                                                .collect(Collectors.toList());

                                                        Result nextResult = scaffoldResult.getLastResult();
                                                        for (SimpleOriginalPiece originalPiece : simpleOriginalPieces) {
                                                            nextResult = AddLastResult.create(nextResult, originalPiece);
                                                        }

                                                        FillResult currentResult = new AddLastFillResult(fillResult, nextResult, tOperations);
                                                        return afterFill(currentResult, firstFilledLine, requiredDeletedKey, targetY);
                                                    });
                                        });
                            });
                });
        */

    /*



    // 指定したラインが揃ったとき、次の探索を始める
    private Stream<FillResult> afterFill(FillResult currentResult, long firstFilledLine, long requiredDeletedKey, int targetY) {
        // 結果を記録する
        Stream<FillResult> nextStream = Stream.of(currentResult);

        // 使えるミノが残っていないとき探索終了
        PieceCounter reminderPieceCounter = currentResult.getLastResult().getRemainderPieceCounter();
        if (reminderPieceCounter.isEmpty()) {
            return nextStream;
        }

        // 現在の解と積み上げた解を記録
        nextStream = Stream.concat(nextStream, stack(currentResult, firstFilledLine, requiredDeletedKey, targetY));

        // 残りにTが含まれていないので、再探索はしない
        if (!reminderPieceCounter.containsAll(T_PIECE_COUNTER)) {
            return nextStream;
        }

        // ライン消去後に再探索
        return Stream.concat(nextStream, clearLineAndResearch(currentResult, firstFilledLine, requiredDeletedKey));
    }

    private List<SimpleOriginalPiece> createTOperations(SimpleOriginalPiece piece) {
        if (piece.getPiece() == Piece.T) {
            return Collections.singletonList(piece);
        }
        return Collections.emptyList();
    }

    // ライン消去は行わず積み上げる
    private Stream<FillResult> stack(FillResult currentResult, long firstFilledLine, long requiredDeletedKey, int targetY) {
        Result lastResult = currentResult.getLastResult();

        // 探索範囲でまだ揃っていないラインを列挙
        long belowMaxHeight = KeyOperators.getMaskForKeyBelowY(maxHeight);
        long aboveTarget = KeyOperators.getMaskForKeyAboveY(targetY + 1);
        long filledLine = lastResult.getAllMergedField().getFilledLine();
        long nextTargetLine = belowMaxHeight & aboveTarget & ~filledLine;

        // 次に探索するラインが残っていない
        if (nextTargetLine == 0L) {
            return Stream.empty();
        }

        // 次に探索するラインの一番下のラインを取得して探索
        int nextY = KeyOperators.bitToYFromKey(KeyOperators.extractLowerBit(nextTargetLine));
        return this.search(currentResult, firstFilledLine, requiredDeletedKey, nextY);
    }

    // ライン消去した上で再探索
    private Stream<FillResult> clearLineAndResearch(FillResult currentResult, long firstFilledLine, long requiredDeletedKey) {
        Result result = currentResult.getLastResult();
        EmptyFillResult emptyFillResult = new EmptyFillResult(result);
        long nextFirstFilledLine = result.getAllMergedField().getFilledLine();
        long nextRequiredDeletedKey = requiredDeletedKey | (nextFirstFilledLine & ~firstFilledLine);
        return this.search(emptyFillResult, nextFirstFilledLine, nextRequiredDeletedKey, 0);
    }

    // 指定したラインを埋める
    private Stream<List<SimpleOriginalPiece>> fill(ScaffoldResult scaffoldResult, TargetPiece targetPiece, long firstFilledLine) {
        Result lastResult = scaffoldResult.getLastResult();
        Field allMergedField = lastResult.freezeAllMergedField();

        // 残りのミノ
        PieceCounter remainder = lastResult.getRemainderPieceCounter();

        // 残りのブロック
        Field restBlock = targetPiece.freezeLineField();
        restBlock.reduce(allMergedField);
        List<NextRestFields> nextRestFieldList = extractNextRestFields(restBlock);

        int targetY = targetPiece.getTargetY();
        assert (allMergedField.getFilledLine() & KeyOperators.getBitKey(targetY)) == 0L;
        return fill(remainder, allMergedField, firstFilledLine, targetY, nextRestFieldList, 0, new ArrayList<>());
    }

    // ブロックを抽出
    private List<NextRestFields> extractNextRestFields(Field restBlock) {
        int maxBoardCount = restBlock.getBoardCount();

        assert !restBlock.isPerfect();

        List<NextRestFields> pairs = new ArrayList<>();

        do {
            NextRestFields nextRestFields = calcNextRestField(restBlock, maxBoardCount);
            pairs.add(nextRestFields);
            restBlock = nextRestFields.getRestBlock();
        } while (!restBlock.isPerfect());

        assert 1 <= pairs.size();

        return pairs;
    }

    private Stream<List<SimpleOriginalPiece>> fill(
            PieceCounter remainder, Field field, long firstFilledLine, int targetY, List<NextRestFields> nextRestFieldList, int index, List<SimpleOriginalPiece> prev
    ) {
        int maxSize = nextRestFieldList.size();

        NextRestFields nextRestFields = nextRestFieldList.get(index);
        int minX = nextRestFields.getMinX();
        int blockCount = nextRestFields.getTargetBlockCount();

        return lineFillRunner.search(remainder, field, firstFilledLine, minX, targetY, blockCount)
                .flatMap(lineFillResult -> {
                    List<SimpleOriginalPiece> simpleOriginalPiece = lineFillResult.getSimpleOriginalPiece();

                    if (index == maxSize - 1) {
                        List<SimpleOriginalPiece> result = Stream.concat(prev.stream(), simpleOriginalPiece.stream())
                                .collect(Collectors.toList());
                        return Stream.of(result);
                    } else {
                        Field nextField = field.freeze();
                        nextField.merge(lineFillResult.getUsingField());

                        PieceCounter usingPieceCount = new PieceCounter(simpleOriginalPiece.stream().map(Operation::getPiece));
                        PieceCounter nextRemainder = remainder.removeAndReturnNew(usingPieceCount);

                        ArrayList<SimpleOriginalPiece> operations = new ArrayList<>(prev);
                        operations.addAll(simpleOriginalPiece);

                        return fill(
                                nextRemainder, nextField, firstFilledLine, targetY, nextRestFieldList, index + 1, operations
                        );
                    }
                });
    }

    private NextRestFields calcNextRestField(Field restBlock, int boardCount) {
        assert !restBlock.isPerfect();

        switch (boardCount) {
            case 1: {
                long low = restBlock.getBoard(0);

                {
                    long nextBoard = getNextBoard(low);
                    Field nextRestBlock = new SmallField(nextBoard);
                    return toNextRestFields(low, nextBoard, nextRestBlock);
                }
            }
            case 2: {
                long low = restBlock.getBoard(0);
                long high = restBlock.getBoard(1);

                if (low != 0L) {
                    long nextBoard = getNextBoard(low);
                    Field nextRestBlock = new MiddleField(nextBoard, high);
                    return toNextRestFields(low, nextBoard, nextRestBlock);
                }

                {
                    long nextBoard = getNextBoard(high);
                    Field nextRestBlock = new MiddleField(low, nextBoard);
                    return toNextRestFields(high, nextBoard, nextRestBlock);
                }
            }
            case 4: {
                long low = restBlock.getBoard(0);
                long midLow = restBlock.getBoard(1);
                long midHigh = restBlock.getBoard(2);
                long high = restBlock.getBoard(3);

                if (low != 0L) {
                    long nextBoard = getNextBoard(low);
                    Field nextRestBlock = new LargeField(nextBoard, midLow, midHigh, high);
                    return toNextRestFields(low, nextBoard, nextRestBlock);
                }

                if (midLow != 0L) {
                    long nextBoard = getNextBoard(midLow);
                    Field nextRestBlock = new LargeField(low, nextBoard, midHigh, high);
                    return toNextRestFields(midLow, nextBoard, nextRestBlock);
                }

                if (midHigh != 0L) {
                    long nextBoard = getNextBoard(midHigh);
                    Field nextRestBlock = new LargeField(low, midLow, nextBoard, high);
                    return toNextRestFields(midHigh, nextBoard, nextRestBlock);
                }

                {
                    long nextBoard = getNextBoard(high);
                    Field nextRestBlock = new LargeField(low, midLow, midHigh, nextBoard);
                    return toNextRestFields(high, nextBoard, nextRestBlock);
                }
            }
        }

        throw new IllegalStateException();
    }

    private long getNextBoard(long board) {
        return ((board | (board - 1)) + 1) & board;
    }

    private NextRestFields toNextRestFields(long currentBoard, long nextBoard, Field nextRestBlock) {
        long targetBoard = currentBoard ^ nextBoard;
        int targetBlockCount = Long.bitCount(targetBoard);

        long board = targetBoard;
        board = board | (board >> 20);
        board = board | (board >> 20);
        board = board | (board >> 10);
        long lowerBit = board & (-board);
        int minX = BitOperators.bitToX(lowerBit);

        return new NextRestFields(minX, targetBlockCount, nextRestBlock);
    }
    */
//
//class NextRestFields {
//    private final int minX;
//    private final int targetBlockCount;
//    private final Field restBlock;
//
//    NextRestFields(int minX, int targetBlockCount, Field restBlock) {
//        this.minX = minX;
//        this.targetBlockCount = targetBlockCount;
//        this.restBlock = restBlock;
//    }
//
//    Field getRestBlock() {
//        return restBlock;
//    }
//
//    int getMinX() {
//        return minX;
//    }
//
//    int getTargetBlockCount() {
//        return targetBlockCount;
//    }
//}

//

//
//    //
//    private Stream<Result> searchFirst2(FillResult prevFillResult, Result result, TargetPieceCandidate targetPieceCandidate) {
//        SimpleOriginalPiece firstOriginalPiece = targetPieceCandidate.getSimpleOriginalPiece();
//
//        // すでにラインが揃っているか確認する
//        long filledLine = result.getAllMergedField().getFilledLine();
//        long pivotFilledLine = firstOriginalPiece.getUsingKey() & filledLine;
//
//        int lowerPivotFilledY = pivotFilledLine != 0L ? KeyOperators.bitToY(KeyOperators.extractLowerBit(pivotFilledLine)) : Integer.MAX_VALUE;
//
//        return targetPieceCandidate.getTargetPieces(fieldHeight)
//                .flatMap(targetPiece -> {
//                    long targetLineKey = targetPiece.getTargetLineKey();
//
//                    // すでにターゲットラインが揃っているとき
//                    if ((targetLineKey & pivotFilledLine) != 0L) {
//                        int targetY = targetPiece.getTargetY();
//
//                        if (lowerPivotFilledY == targetY) {
//                            // 指定したラインが揃っている
//                            return next(prevFillResult, result, targetPiece);
//                        } else if (lowerFilledY < targetY) {
//                            // 指定したラインより下が揃っている
//                            return Stream.empty();
//                        }
//                    }
//                });
//
//        return targetPieceCandidate.getTargetPieces(maxHeight)
//                .flatMap(targetPiece -> {
//                    SimpleOriginalPiece targetSimpleOriginalPiece = targetPiece.getPiece();
//
//                    // すでにターゲットラインが揃っているとき
//                    long targetLineKey = targetPiece.getTargetLineKey();
//                    if ((targetLineKey & scaffoldFilledLine) != 0L) {
//                        int targetY = targetPiece.getTargetY();
//
//                    }
//
//                    long rangeKey = belowMaxHeightKey & aboveTargetKey;
//        return Stream.of(result);
//    }
//
//    private Stream<Result> next(FillResult prevFillResult, Result result, TargetPiece targetPiece) {
//        PieceCounter reminderPieceCounter = result.getRemainderPieceCounter();
//
//        // 結果を記録する
//        SimpleOriginalPiece targetOriginalPiece = targetPiece.getPiece();
//        EmptyLineFillResult fillResult = new EmptyLineFillResult(result, createTOperations(targetOriginalPiece));
//        FillResult currentResult = new RecursiveFillResult(prevFillResult, fillResult);
//
//        // 使えるミノが残っていないとき探索終了
//        if (reminderPieceCounter.isEmpty()) {
//            return Stream.of(currentResult);
//        }
//
//        Field allMergedField = result.getAllMergedField();
//        long currentFilledLine = allMergedField.getFilledLine();
//
//        // 積み重ねる
//        long rangeKey = belowMaxHeightKey & aboveTargetKey;
//        long targetCandidateKey = (~currentFilledLine) & rangeKey;
//        Stream<FillResult> nextStream = this.next(
//                targetPieceCandidates, belowMaxHeightKey, currentResult, reminderPieceCounter,
//                allMergedField, targetCandidateKey, targetSimpleOriginalPiece
//        );
//
//        // 残りにTが含まれていないので、再探索はしない
//        if (!reminderPieceCounter.containsAll(T_PIECE_COUNTER)) {
//            return Stream.concat(Stream.of(currentResult), nextStream);
//        }
//
//        // ライン消去後に再探索
//        return Stream.concat(
//                Stream.concat(Stream.of(currentResult), nextStream),
//                fillRunner.searchSecond(result, initFilledLine2)
//        );
//    }
//
//    private List<SimpleOriginalPiece> createTOperations(SimpleOriginalPiece piece) {
//        if (piece.getPiece() == Piece.T) {
//            return Collections.singletonList(piece);
//        }
//        return Collections.emptyList();
//    }
//
//    private Stream<Result> searchFirst(FillResult prevFillResult) {
//        Result initLastResult = prevFillResult.getLastResult();
//

//
//
//        // List<TargetPieceCandidate> targetPieceCandidates
//
////        return Stream.empty();
//        /**
//         long initFilledLine = initLastResult.getAllMergedField().getFilledLine();
//         long initFilledLine2 = prevFillResult.getInitFilledLine();
//
//         // 軸となる1ピースを選ぶ
//         return targetPieceCandidates.stream()
//         .flatMap(targetPieceCandidate -> {
//         // 足場 + 1ピースを探索
//         SimpleOriginalPiece firstOriginalPiece = targetPieceCandidate.getSimpleOriginalPiece();
//         long usingKey = firstOriginalPiece.getUsingKey();
//
//         return scaffoldRunner.build2(initLastResult, firstOriginalPiece,usingKey| (aboveTargetKey & belowMaxHeightKey)  )
//         .map(ScaffoldResult::getLastResult)
//         .flatMap(result -> {
//         // すでにラインが揃っているか確認する
//         long scaffoldFilledLine = result.getAllMergedField().getFilledLine();
//         long pieceFilledLine = usingKey & scaffoldFilledLine;
//         int lowerFilledY = pieceFilledLine != 0L ? KeyOperators.bitToY(KeyOperators.extractLowerBit(pieceFilledLine)) : Integer.MAX_VALUE;
//
//         return targetPieceCandidate.getTargetPieces(maxHeight)
//         .flatMap(targetPiece -> {
//         SimpleOriginalPiece targetSimpleOriginalPiece = targetPiece.getPiece();
//
//         // すでにターゲットラインが揃っているとき
//         long targetLineKey = targetPiece.getTargetLineKey();
//         if ((targetLineKey & scaffoldFilledLine) != 0L) {
//         int targetY = targetPiece.getTargetY();
//         if (lowerFilledY == targetY) {
//         // 指定したラインが揃っている
//
//         PieceCounter reminderPieceCounter = result.getRemainderPieceCounter();
//
//         // 結果を記録する
//         EmptyLineFillResult fillResult = new EmptyLineFillResult(result, createTOperations(firstOriginalPiece));
//         FillResult currentResult = new RecursiveFillResult(prevFillResult, fillResult);
//
//         // 使えるミノが残っていないとき探索終了
//         if (reminderPieceCounter.isEmpty()) {
//         return Stream.of(currentResult);
//         }
//
//         Field allMergedField = result.getAllMergedField();
//         long currentFilledLine = allMergedField.getFilledLine();
//
//         // 積み重ねる
//         long rangeKey = belowMaxHeightKey & aboveTargetKey;
//         long targetCandidateKey = (~currentFilledLine) & rangeKey;
//         Stream<FillResult> nextStream = this.next(
//         targetPieceCandidates, belowMaxHeightKey, currentResult, reminderPieceCounter,
//         allMergedField, targetCandidateKey, targetSimpleOriginalPiece
//         );
//
//         // 残りにTが含まれていないので、再探索はしない
//         if (!reminderPieceCounter.containsAll(T_PIECE_COUNTER)) {
//         return Stream.concat(Stream.of(currentResult), nextStream);
//         }
//
//         // ライン消去後に再探索
//         return Stream.concat(
//         Stream.concat(Stream.of(currentResult), nextStream),
//         fillRunner.searchSecond(result, initFilledLine2)
//         );
//         } else if (lowerFilledY < targetY) {
//         // 指定したラインより下が揃っている
//         return Stream.empty();
//         }
//         }
//
//         long rangeKey = belowMaxHeightKey & aboveTargetKey;
//
//         // 最低でも1行を揃える
//         return fill(result, initFilledLine, targetPiece)
//         .flatMap(fillResult -> {
//         Result lastResult = fillResult.getLastResult();
//         PieceCounter reminderPieceCounter = lastResult.getRemainderPieceCounter();
//
//         // 結果を記録する
//         FillResult currentResult = new RecursiveFillResult(prevFillResult, fillResult);
//
//         // 使えるミノが残っていないとき探索終了
//         if (reminderPieceCounter.isEmpty()) {
//         return Stream.of(currentResult);
//         }
//
//         Field allMergedField = lastResult.getAllMergedField();
//         long currentFilledLine = allMergedField.getFilledLine();
//
//         // 積み重ねる
//         long targetCandidateKey = (~currentFilledLine) & rangeKey;
//         Stream<FillResult> nextStream = this.next(
//         targetPieceCandidates, belowMaxHeightKey, currentResult, reminderPieceCounter,
//         allMergedField, targetCandidateKey, targetSimpleOriginalPiece
//         );
//
//         // 残りにTが含まれていないので、再探索はしない
//         if (!reminderPieceCounter.containsAll(T_PIECE_COUNTER)) {
//         return Stream.concat(Stream.of(currentResult), nextStream);
//         }
//
//         // ライン消去後に再探索
//         return Stream.concat(
//         Stream.concat(Stream.of(currentResult), nextStream),
//         fillRunner.searchSecond(lastResult, initFilledLine2)
//         );
//         });
//         });
//         });
//         });
//    }
//
//     private static final Set<Integer> EMPTY_SET = Collections.emptySet();
//
//     private final FirstFillRunner runner1;
//     private final SecondFillRunner runner2;
//
//
//
//     public Stream<FillResult> search(Result result) {
//     EmptyFillResult fillResult = new EmptyFillResult(result);
//
//     PieceCounter remainderPieceCounter = fillResult.getLastResult().getRemainderPieceCounter();
//
//     return Stream.empty();
//     //        return searchFirst(fillResult);
//     }
//
//     private Stream<FillResult> searchFirst(EmptyFillResult fillResult) {
//     Result result = fillResult.getLastResult();
//
//     Field field = result.getAllMergedField();
//     PieceCounter reminderPieceCounter = result.getRemainderPieceCounter();
//
//     // 探索1回目の対象ミノ
//     // ライン消去がないミノで、消去ラインの左端に位置するもの
//     List<TargetPieceCandidate> targetPieces = filledPieces.first(field, reminderPieceCounter, 0);
//
//     return runner1.search(targetPieces, fillResult);
//     }
//
//     // 探索2回目
//     Stream<FillResult> searchSecond(Result result, long initFilledLine) {
//     Field field = result.getAllMergedField();
//     PieceCounter reminderPieceCounter = result.getRemainderPieceCounter();
//
//     // 探索2回目の対象ミノ
//     // ライン消去が絡むミノを選ぶ
//     List<TargetPieceCandidate> targetPieces = filledPieces.second(field, initFilledLine, reminderPieceCounter, 0);
//
//     EmptyFillResult fillResult = new EmptyFillResult(result, initFilledLine);
//     return runner2.search(targetPieces, fillResult);
//     }
//}
//*/