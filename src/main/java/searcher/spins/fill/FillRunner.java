package searcher.spins.fill;

import common.datastore.PieceCounter;
import core.field.Field;
import core.field.KeyOperators;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.fill.line.LineFillRunner;
import searcher.spins.fill.results.AddLastFillResult;
import searcher.spins.fill.results.EmptyFillResult;
import searcher.spins.fill.results.FillResult;
import searcher.spins.results.Result;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FillRunner {
    private final LineFillRunner lineFillRunner;
    private final int maxTargetHeight;

    public FillRunner(LineFillRunner lineFillRunner, int maxTargetHeight) {
        this.lineFillRunner = lineFillRunner;
        this.maxTargetHeight = maxTargetHeight;
    }

    public Stream<FillResult> search(Result result) {
        EmptyFillResult emptyFillResult = new EmptyFillResult(result);
        long filledLine = result.getAllMergedFilledLine();
        return search(emptyFillResult, 0, filledLine);
    }

    private Stream<FillResult> search(FillResult fillResult, int lowerY, long initFilledLine) {
        Result result = fillResult.getLastResult();

        Field field = result.getAllMergedField();
        PieceCounter pieceCounter = result.getRemainderPieceCounter();
        long currentFilledLine = result.getAllMergedFilledLine();

        if (pieceCounter.isEmpty()) {
            return Stream.empty();
        }

        long belowY = KeyOperators.getMaskForKeyBelowY(maxTargetHeight);

        return IntStream.range(lowerY, maxTargetHeight)
                .boxed()
                .filter(y -> (currentFilledLine & KeyOperators.getBitKey(y)) == 0L)
                .flatMap(y -> {
                    long aboveY = belowY & KeyOperators.getMaskForKeyAboveY(y + 1);

                    return lineFillRunner.search(field, pieceCounter, y, initFilledLine)
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
                                        search(nextFillResult, nextTargetY, initFilledLine)
                                );
                            });
                });
    }
}