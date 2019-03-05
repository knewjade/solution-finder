package searcher.spins.fill;

import common.datastore.PieceCounter;
import core.field.Field;
import core.field.KeyOperators;
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

public class FillRunner {
    private final LineFillRunner lineFillRunner;
    private final ScaffoldRunner scaffoldRunner;
    private final int maxTargetHeight;

    public FillRunner(LineFillRunner lineFillRunner, ScaffoldRunner scaffoldRunner, int maxTargetHeight) {
        this.lineFillRunner = lineFillRunner;
        this.scaffoldRunner = scaffoldRunner;
        this.maxTargetHeight = maxTargetHeight;
    }

    public Stream<FillResult> search(Result result) {
        EmptyFillResult emptyFillResult = new EmptyFillResult(result);
        return search1(emptyFillResult, 0)
                .flatMap(this::addScaffold);
    }

    private Stream<FillResult> search1(FillResult fillResult, int lowerY) {
        Result result = fillResult.getLastResult();

        Field field = result.getAllMergedField();
        PieceCounter pieceCounter = result.getRemainderPieceCounter();
        long firstFilledLine = result.getAllMergedFilledLine();

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
                                long filledLine = lineFillResult.getAllMergedFilledLine();

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

    private Stream<FillResult> search(FillResult fillResult, int lowerY) {
        Result result = fillResult.getLastResult();

        Field field = result.getAllMergedField();
        PieceCounter pieceCounter = result.getRemainderPieceCounter();
        long firstFilledLine = result.getAllMergedFilledLine();

        if (pieceCounter.isEmpty()) {
            return Stream.empty();
        }

        long belowY = KeyOperators.getMaskForKeyBelowY(maxTargetHeight);

        return IntStream.range(lowerY, maxTargetHeight)
                .boxed()
                .filter(y -> (firstFilledLine & KeyOperators.getBitKey(y)) == 0L)
                .flatMap(y -> {
                    long aboveY = belowY & KeyOperators.getMaskForKeyAboveY(y + 1);

                    return lineFillRunner.search(field, pieceCounter, y)
                            .flatMap(lineFillResult -> {
                                // すでに揃っているライン
                                long filledLine = lineFillResult.getAllMergedFilledLine();

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