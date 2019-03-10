package searcher.spins.scaffold.results;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.SpinCommons;
import searcher.spins.results.Result;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ScaffoldResultWithT extends ScaffoldResult {
    static List<SimpleOriginalPiece> extractAirOperations(Result result, SimpleOriginalPiece operationT, Stream<SimpleOriginalPiece> targetOperationStream) {
        Field initField = result.getInitField();

        // 地形からTミノを除く
        Field field = result.getAllMergedField().freeze();
        field.reduce(operationT.getMinoField());

        long filledLine = result.getAllMergedFilledLine();

        long onePieceFilledKeyWithoutT = result.getOnePieceFilledKey() & ~operationT.getUsingKey();

        return targetOperationStream
                .filter(operation -> !SpinCommons.existsOnGround(initField, field, filledLine, onePieceFilledKeyWithoutT, operation))
                .collect(Collectors.toList());
    }

    // Tミノを取得
    public abstract SimpleOriginalPiece getOperationT();

    abstract Field getNotAllowed();
}
